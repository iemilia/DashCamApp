package com.trafficpol;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Environment;
import android.os.StatFs;
import android.media.MediaRecorder;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileInputStream;
import java.lang.Exception;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class ADrivingCamera extends SurfaceView
		implements SurfaceHolder.Callback
{

	private static final String TAG = "BabaRamCamera";
	private static final String MP4 = ".mp4";
	private static final long MAXFILESIZE = 100 * 1000 * 1024; //video size
	private static final int MINDURATION = 2000;
	private Camera mCamera = null;
	private MediaRecorder mRecorder = null;
	private Activity mAct;
	private int mCameraId;
	private String workingPath;
	private SurfaceHolder mHolder;
	private Context contexthere;
	private AsyncTask<Void, Void, Void> trimmVideo;
	private AsyncTask<String, Integer, String> mergeVideos;
	//	private AsyncTask<String, Boolean, String> amazonserv;
	ArrayList<String> timestampArrayRec;
	ArrayList<Location> locationArrayRec;
	ArrayList<Double> latArrayRec;
	ArrayList<Double> longArrayRec;

	//GPSTracker gps;
	double coordinates[];
	//private AmazonService as;
	Timer timer;
	File locFile;
	//settings
	int maxHistory;
	boolean audioFeedback,audioType;
	int resolution_width=1920;
	int resolution_height=1080;
	static CountDownTimer ctd;
	public GPSTracker gpsRecording;


	static final long[] ROTATE_0 = new long[]{1, 0, 0, 1, 0, 0, 1, 0, 0};
	static final long[] ROTATE_90 = new long[]{0, 1, -1, 0, 0, 0, 1, 0, 0};
	static final long[] ROTATE_180 = new long[]{-1, 0, 0, -1, 0, 0, 1, 0, 0};
	static final long[] ROTATE_270 = new long[]{0, -1, 1, 0, 0, 0, 1, 0, 0};

	private long[] rotate0 = new long[] {0x00010000,0,0,0,0x00010000,0,0,0,0x40000000};
	private long[] rotate90 = new long[] {0,0x00010000,0,-0x00010000,0,0,0,0,0x40000000};
	private long[] rotate180 = new long[] {0x00010000,0,0,0,0x00010000,0,0,0, 0x40000000};
	private long[] rotate270 = new long[] {-0x00010000,0,0,0,-0x00010000,0,0,0, 0x40000000};

	public ADrivingCamera(Context context, int id) {
		super(context);

		mAct = (Activity) context;
		mCameraId = id;
		contexthere=context;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		gpsRecording= new GPSTracker(ADrivingCamera.this.getContext());
		//gps=new GPSTracker(context);
		getPreferences();


	}

	public void surfaceCreated(SurfaceHolder holder) {
		deleteOldVideos();
		try {
			start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public  void surfaceDestroyed(SurfaceHolder holder) {
		//timer.cancel();
		try {
			stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//deleteOldVideos();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	}

	public void start() throws IOException {
		//set timer
		ctd=new CountDownTimer(2000, 1000) {
			public void onTick(long millisUntilFinished) {
				//label.setText("Event Capturing in Progress."+"seconds remaining: " + millisUntilFinished / 1000);
			}

			public void onFinish() {
				//label.setText("done!");
				ADrivingActivity.state=0;

			}
		}.start();

		if (mCamera == null) {
			try {

				mCamera = Camera.open(mCameraId);
				/*Toast.makeText(mAct,
						"Camera started",
						Toast.LENGTH_LONG
				).show();*/
				//set sound of camera to mute
				Camera.CameraInfo info = new Camera.CameraInfo();
				Camera.getCameraInfo(mCameraId, info);
				if (audioFeedback==false&&info.canDisableShutterSound) {
					mCamera.enableShutterSound(false);

				}
			} catch (Exception e) {
				stop();
				Toast.makeText(mAct,
						getResources().getString(R.string.camera_error),
						Toast.LENGTH_SHORT
				).show();
				return;
			}

			try {
				mCamera.setDisplayOrientation(getCameraOrientation(true));
			} catch (Exception e) {}

			mCamera.unlock();
		}

		startRecorder();
	}

	public void startRecorder() throws IOException {
		if (mRecorder == null) {
			TextView label=(TextView)mAct.findViewById(R.id.recText);
			label.setText("Continuous recording");
			TextView additional_feedback= (TextView) mAct.findViewById(R.id.additional_feedback);
			additional_feedback.setVisibility(INVISIBLE);

			// Initialize.

			mRecorder = new MediaRecorder();
			mRecorder.setCamera(mCamera);

			// Set sources.
			//if(audioType==true)
			mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			Log.d("audioType", String.valueOf(audioType));
			mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

			// Set profile.
			if(resolution_width==1920)
				mRecorder.setProfile(CamcorderProfile.get(
						mCameraId,
						CamcorderProfile.QUALITY_HIGH
				));
			if(resolution_width==1280)
				mRecorder.setProfile(CamcorderProfile.get(
						mCameraId,
						CamcorderProfile.QUALITY_720P
				));
			// Set the rest of the properties.
			mRecorder.setOrientationHint(getCameraOrientation(false));
			Log.d("rolution", String.valueOf(resolution_width) + " " + String.valueOf(resolution_height));
			mRecorder.setVideoSize(resolution_width, resolution_height);
			File fileVideo=getOutputFile();
			mRecorder.setOutputFile(fileVideo.toString());
			locFile=createLocationfile(fileVideo.getName());
			mRecorder.setPreviewDisplay(mHolder.getSurface());
			//mRecorder.setVideoEncodingBitRate(2500);
			mRecorder.setMaxFileSize(MAXFILESIZE);

			mRecorder.setLocation((float) gpsRecording.getLatitude(), (float) gpsRecording.getLongitude());
			//gps.getLocation();
			Log.d("GPS TEST", String.valueOf(gpsRecording.locationArray.size()));
			if(gpsRecording.locationArray!=null)
			{
				locationArrayRec=gpsRecording.locationArray;

			}
			/*int delay = 0; // delay for 5 sec.
			int period = 10000; // repeat every 10 secs.
			timer=new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				int i;
				public void run() {
					gps.getLocation();
					Log.d("GPS TEST", String.valueOf(gps.locationArray.size()));
					if(gps.locationArray!=null)
					{
						locationArrayRec=gps.locationArray;

					}
					i++;
					Log.d("GPS TEST", "i=" + String.valueOf(i));
				}
			}, delay, period);*/

			//when the maxsize is reached, the old recorded videos are deleted
			mRecorder.setOnInfoListener(
					new MediaRecorder.OnInfoListener() {
						public void onInfo(MediaRecorder mr, int w, int ex) {
							if (w == MediaRecorder.
									MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
								try {
									stop();
								} catch (IOException e) {
									e.printStackTrace();
								}
								//coordinates = getCoordinates();
								//Get address base on location
								//final String add_for_display = getLocationName();
								//Log.d("location",add_for_display);
								//Toast.makeText(getContext(), "Your Location is -" + add_for_display, Toast.LENGTH_LONG).show();
								//location.setText(add_for_display.toString());
								//Log.d("GPS TEST", "locArray "+String.valueOf(locationArrayRec.size()));
								deleteOldVideos();
								try {

									start();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
			);
			mRecorder.setOnErrorListener(
					new MediaRecorder.OnErrorListener() {
						public void onError(MediaRecorder mr, int w, int ex) {
							try {
								stop();
							} catch (IOException e) {
								e.printStackTrace();
							}
							deleteOldVideos();
							try {
								start();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
			);

			// Begin recording.
			try {
				mRecorder.prepare();
				mRecorder.start();

			} catch (Exception e) {
				Log.d(TAG, "Recorder start: " + e.getMessage());
				stop();
			}
		}
	}

	public File createLocationfile(String fileName) throws IOException {

		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory()+"/TraficPolLocation"
		);
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {

			}
		}
		String timeStamp=fileName.split(".mp4")[0];
		//check if we get the location of an event or a video
		/*if(fileName.length()>19)
			//event video
			timeStamp = fileName.split("-")[1].split(".mp4")[0];
		else
			//video
			timeStamp=fileName.split(".mp4")[0];
		*/
		File locationFile = new File(mediaStorageDir, timeStamp + ".txt");
		locationFile.createNewFile();
		updateFile(locationFile);
		//updateGallery(ManageFiles.getFiles(false,getLocationOutputDir()));
		return locationFile;
	}

	private void writeToFile(File file,ArrayList<String> date, ArrayList<Location> location) throws IOException {
		Geocoder geo = new Geocoder(ADrivingCamera.this.getContext(), Locale.getDefault());
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i=0;i<date.size();i++)
				bw.append(date.get(i)+" " +location.get(i).getLatitude()+" "+ location.get(i).getLongitude()+"\r\n"+
						GPSTracker.getLocationName(geo,location.get(i).getLatitude(),location.get(i).getLongitude(),contexthere)+"\r\n");
			bw.close();
			fw.close();
		}
		catch (IOException e) {
		}
	}


	public static File getLocationOutputDir() {
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory()+"/TraficPolLocation"
		);
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		return mediaStorageDir;
	}

	public void stop() throws IOException {
		ADrivingActivity.state=2;
		stopRecorder();
		if (mCamera != null) {
			/*Toast.makeText(mAct,
					"Camera stoped",
					Toast.LENGTH_SHORT
			).show();*/
			//timer.cancel();
			mCamera.lock(); //Re-locks the camera to prevent other processes from accessing it.
			mCamera.release();// call it when you are done using the camera, otherwise it will
			// remain locked and be unavailable to other applications.
			mCamera = null;
			gpsRecording.locationArray.clear();
			gpsRecording.timestampArray.clear();
		}
		//timer.cancel();

	}

	public void stopRecorder() {
		if (mRecorder != null) {
			try {
				TextView label=(TextView)mAct.findViewById(R.id.recText);
				label.setText(" ");
				//timer.cancel();
				for(int i=0;i<gpsRecording.timestampArray.size();i++){
				}
				writeToFile(locFile, gpsRecording.timestampArray, gpsRecording.locationArray);
				mRecorder.stop();
				mRecorder.reset();//Restarts the MediaRecorder to its idle state. After calling this
				// method, you will have to configure it again as if it had just been constructed.
				mRecorder.release();//Releases resources associated with this MediaRecorder object.
				// It is good practice to call this method when you're done using the MediaRecorder
			} catch (Exception e) {
			}
			mRecorder = null;
		}
		//timer.cancel();


	}

	//flip to use either front or back camera
	public void flip() throws IOException {
		if (Camera.getNumberOfCameras() < 2) {
			return;
		}

		mCameraId = (mCameraId == 0) ? 1 : 0; //if id=0, then give id value=1 else =0
		stop();
		start();
	}

	public static File[] getFiles(boolean isAscending) {
		File dir = getOutputDir();

		if (dir == null) {
			return null;
		}

		File files[] = dir.listFiles();
		/*for (int i = 0; i < files.length; i++)
			Log.d("Pass1:Files", "FileName:" + files[i].getName());
*/
		if (isAscending) {
			Arrays.sort(files, new Comparator<File>(){
				public int compare(File f1, File f2) {
					return Long.valueOf(f1.lastModified())
							.compareTo(f2.lastModified());
				}
			});
		}
		else {
			Arrays.sort(files, new Comparator<File>(){
				public int compare(File f1, File f2) {
					return Long.valueOf(f2.lastModified())
							.compareTo(f1.lastModified());
				}
			});
		}
		return files;
	}

	public static File[] getEventFiles(boolean isAscending) {
		File dir = getEventOutputDir();

		if (dir == null) {
			return null;
		}

		File files[] = dir.listFiles();
	/*	for (int i = 0; i < files.length; i++)
			Log.d("Pass1:Files", "FileName:" + files[i].getName());
*/
		if (isAscending) {
			Arrays.sort(files, new Comparator<File>(){
				public int compare(File f1, File f2) {
					return Long.valueOf(f1.lastModified())
							.compareTo(f2.lastModified());
				}
			});
		}
		else {
			Arrays.sort(files, new Comparator<File>(){
				public int compare(File f1, File f2) {
					return Long.valueOf(f2.lastModified())
							.compareTo(f1.lastModified());
				}
			});
		}
		return files;
	}

	private void deleteOldVideos() {
		// Get all the files in the directory and sort in descending order.
		File[] files = getFiles(false);

		for (int i = 0; i < files.length; i++)
			if (files == null || files.length == 0) {
				return;
			}


		// Iterate over each file.
		long totalFileSize = 0;
		for (int i = 0; i < files.length; i++) {
			try {
				// Get the duration of this video.
				MediaMetadataRetriever mmr = new MediaMetadataRetriever();
				FileInputStream fis = new FileInputStream(files[i]);
				mmr.setDataSource(fis.getFD()); //gets the underlying file descriptor
				String duration = mmr.extractMetadata(
						MediaMetadataRetriever.METADATA_KEY_DURATION
				);

				// Delete file if it's too short, and alert the user.
				/*if (Long.parseLong(duration) <= MINDURATION) {
					Log.d("<5 FFFFFFFFFFFFfiles", files[i].toString());
					files[i].delete();
					if(ManageFiles.getOutputDir("locationEvent", files[i].getName().split(".mp4")[0]).exists())
						ManageFiles.getOutputDir("locationEvent", files[i].getName().split(".mp4")[0]).delete();
					Toast.makeText(mAct,
							getResources().getString(R.string.too_short),
							Toast.LENGTH_SHORT
					).show();
				}
*/
				// Otherwise, add the file size to the total.
	//			else {
					totalFileSize += files[i].length();

	//			}

				// Delete a video if there isn't much free space left.
				if (getFreeSpace() < MAXFILESIZE) {
					if (files[i].exists()) {
						files[i].delete();
						if(ManageFiles.getOutputDir("locationEvent", files[i].getName().split(".mp4")[0]).exists())
							ManageFiles.getOutputDir("locationEvent", files[i].getName().split(".mp4")[0]).delete();
						/*Toast.makeText(mAct,
								"Trebuie sters"+files[i].toString(),
								Toast.LENGTH_SHORT
						).show();*/
						break;
					}
				}
				// Delete one or more videos if we've exceeded our max storage size.
				if (maxHistory >= 0) {
					if (totalFileSize >= (maxHistory * 1000 * 1024)) {
						if (files[i].exists()) {
							files[i].delete();
							if(ManageFiles.getOutputDir("locationEvent", files[i].getName().split(".mp4")[0]).exists())
								ManageFiles.getOutputDir("locationEvent", files[i].getName().split(".mp4")[0]).delete();
							/*Toast.makeText(mAct,
									"MaxHistory!"+files[i].toString(),
									Toast.LENGTH_SHORT
							).show();*/
						}
					}
				}
			} catch (Exception e) {
				files[i].delete();
			}
		}


		// Delete a video if there isn't much free space left.
		if (getFreeSpace() < MAXFILESIZE) {

			for (int i = 0; i < files.length; i++) {
				if (files[i].exists()) {
					totalFileSize -= files[i].length();
					files[i].delete();
					if(ManageFiles.getOutputDir("locationEvent", files[i].getName().split(".mp4")[0]).exists())
						ManageFiles.getOutputDir("locationEvent", files[i].getName().split(".mp4")[0]).delete();
					/*makeText(mAct,
							"Trebuie sters"+files[i].toString(),
							Toast.LENGTH_SHORT
					).show();*/
					break;
				}
			}
		}



		// Refresh the gallery to make sure it is accurately reflected.
		for (int i = 0; i < files.length; i++) {
			MediaScannerConnection.scanFile(mAct,
					new String[] { files[i].toString() }, null,
					new MediaScannerConnection.OnScanCompletedListener() {
						public void onScanCompleted(String path, Uri uri) {

						}
					});
			if (files[i].exists()) {


			}
		}
	}

	private long getFreeSpace() {
		StatFs stat = new StatFs(
				Environment.getExternalStorageDirectory().getPath()
		);

		return (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
	}

	private int getCameraOrientation(boolean display) {
		android.hardware.Camera.CameraInfo info =
				new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(mCameraId, info);
		int rotate = mAct.getWindowManager().getDefaultDisplay().getRotation();

		int degrees = 0;
		switch (rotate) {
			case Surface.ROTATION_0: degrees = 0; break;
			case Surface.ROTATION_90: degrees = 90; break;
			case Surface.ROTATION_180: degrees = 180; break;
			case Surface.ROTATION_270: degrees = 270; break;
		}

		int result;
		if ((display || degrees != 0) &&
				info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
		{
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;
		}
		else {
			result = (info.orientation - degrees + 360) % 360;
		}

		return result;
	}

	public static File getOutputDir() {
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory()+"/TraficPol"
		);
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		return mediaStorageDir;
	}
	private File getOutputFile() {
		File mediaStorageDir = getOutputDir();
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile = new File(mediaStorageDir, timeStamp + MP4);
		return mediaFile;
	}

	//set directory for events
	public static File getEventOutputDir() {
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory()+"/TraficPolEvents"
		);
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		return mediaStorageDir;
	}

	private File getEventOutputFile() {
		File mediaStorageDir = getEventOutputDir();

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());

		File mediaFile = new File(mediaStorageDir, String.format("interm-%s.mp4", timeStamp));
		return mediaFile;
	}

	public void recordStreetEvent() throws IOException {
		stop();
		String TAG_RECORD_EVENT="CAZ";
		String previousDuration="0";
		int caz=5;
		File[] videos=getFiles(false);
		if(videos.length!=0) {
			previousDuration = getLengthVideo(videos[0]);

			if (Float.parseFloat(previousDuration) >= 20000) {
				caz = 1;
				int startTime = (Integer.parseInt(previousDuration) - 20000) / 1000;
				trimmVideo = new TrimmVideo(videos[0].toString(), startTime, 20).execute();
			} else {
				try {
					//cazB:check if the next most recent file was taken right before the most recent one
					String[] nameSecondFile = videos[1].getName().split("_");
					String[] nameRecentestFile = videos[0].getName().split("_");
					if (nameRecentestFile[0].equals(nameSecondFile[0])) {
						nameSecondFile = nameSecondFile[1].split(".mp4");
						nameRecentestFile = nameRecentestFile[1].split(".mp4");

						Date date = null;
						String olderFileStr = nameSecondFile[0].substring(0, 2) + ":" + nameSecondFile[0].substring(2, 4) + ":" + nameSecondFile[0].substring(4, 6);
						DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
						date = formatter.parse(olderFileStr);
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(date);

						int secs = (Integer.parseInt( getLengthVideo(videos[1])))/ 1000;

						formatter = new SimpleDateFormat("HH:mm:ss");
						olderFileStr = formatter.format(calendar.getTime());

						calendar.add(Calendar.SECOND, secs);
						String decomparat0=formatter.format(calendar.getTime());
						calendar.add(Calendar.SECOND, 1);
						String decomparat1=formatter.format(calendar.getTime());
						calendar.add(Calendar.SECOND, 1);
						String decomparat2=formatter.format(calendar.getTime());
						calendar.add(Calendar.SECOND, 1);
						String decomparat3=formatter.format(calendar.getTime());
						calendar.add(Calendar.SECOND, 1);
						String decomparat4=formatter.format(calendar.getTime());
						calendar.add(Calendar.SECOND, 1);
						String decomparat5=formatter.format(calendar.getTime());



						olderFileStr = formatter.format(calendar.getTime());
						String deComparat = nameRecentestFile[0].substring(0, 2) + ":" + nameRecentestFile[0].substring(2, 4) + ":" + nameRecentestFile[0].substring(4, 6);

						if(deComparat.equals(decomparat0)||deComparat.equals(decomparat1)||deComparat.equals(decomparat2)||deComparat.equals(decomparat3)||deComparat.equals(decomparat4)||deComparat.equals(decomparat5))

						//exists and was recorded right before, so we can use the file
						{
							caz = 2;
							String secondDuration = getLengthVideo(videos[1]);
							int length = 20000 - Integer.parseInt(previousDuration);
							int startTime = (Integer.parseInt(secondDuration) - length)/ 1000;
							trimmVideo = new TrimmVideo(videos[1].toString(), startTime, length/1000).execute();
						}
						else
							//exists but wasn't recorded right before, so we can't use the file
							caz = 3;

					}
				} catch (ArrayIndexOutOfBoundsException exception) {
            /* we deal with case 4 where there is only one video in the TraficPol directory and
            so we keep only that video and we record the next 20 secs */
					caz = 4;
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

		}
		else
			caz = 5;
		startEvent(caz);

	}

	public String getLengthVideo(File video){
		try {
			// Get the duration of this video.
			MediaMetadataRetriever mmr = new MediaMetadataRetriever();
			FileInputStream fis = new FileInputStream(video);
			mmr.setDataSource(fis.getFD()); //gets the underlying file descriptor
			String duration = mmr.extractMetadata(
					MediaMetadataRetriever.METADATA_KEY_DURATION
			);
			return duration;
		} catch (Exception e) {
			return null;
		}

	}

	public void startEvent(int caz) throws IOException {
		if (mCamera == null) {
			try {

				mCamera = Camera.open(mCameraId);
				/*Toast.makeText(mAct,
						"Event recording started",
						Toast.LENGTH_LONG
				).show();*/
				//mute the sound of the camera
				Camera.CameraInfo info = new Camera.CameraInfo();
				Camera.getCameraInfo(mCameraId, info);
				if (audioFeedback==false&&info.canDisableShutterSound) {
					mCamera.enableShutterSound(false);
				}
				ADrivingActivity.state=1;

			} catch (Exception e) {
				stop();
				Toast.makeText(mAct,
						getResources().getString(R.string.camera_error),
						Toast.LENGTH_SHORT
				).show();
				return;
			}

			try {
				mCamera.setDisplayOrientation(getCameraOrientation(true));
			} catch (Exception e) {}

			mCamera.unlock();
		}
		ADrivingActivity.state=1;
		startEventRecorder(caz);
	}

	//recording ptr event the 20 sec after tap
	public void startEventRecorder(final int caz) throws IOException {
		if (mRecorder == null) {

			// Initialize.
			mRecorder = new MediaRecorder();
			mRecorder.setCamera(mCamera);


			//set timer
			ctd=new CountDownTimer(21000, 1000) {
				TextView label=(TextView)mAct.findViewById(R.id.recText);
				public void onTick(long millisUntilFinished) {
					label.setText("Event Capturing in Progress."+"seconds remaining: " + millisUntilFinished / 1000);
				}

				public void onFinish() {
					//label.setText("done!");
				}
			}.start();
			// Set sources.
			//if(audioType==true)
			mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

			// Set profile.
			if(resolution_width==1920)
				mRecorder.setProfile(CamcorderProfile.get(
						mCameraId,
						CamcorderProfile.QUALITY_HIGH
				));
			if(resolution_width==1280)
				mRecorder.setProfile(CamcorderProfile.get(
						mCameraId,
						CamcorderProfile.QUALITY_720P
				));
			//final int cazz=caz;
			// Set the rest of the properties.
			mRecorder.setOrientationHint(getCameraOrientation(false));
			mRecorder.setVideoSize(resolution_width, resolution_height);

			File fileVideo=getEventOutputFile();
			mRecorder.setOutputFile(fileVideo.toString());

			locFile=createLocationfile(fileVideo.getName().split("-")[1]);
			mRecorder.setPreviewDisplay(mHolder.getSurface());
			//mRecorder.setMaxFileSize(MAXFILESIZE);
			mRecorder.setMaxDuration(20000);
			mRecorder.setLocation((float) getCoordinates()[0], (float) getCoordinates()[1]);
			if (gpsRecording.locationArray != null) {
				locationArrayRec = gpsRecording.locationArray;

			}
			//when the maxsize is reached, the old recorded videos are deleted
			int delay = 0; // delay for 5 sec.
			int period = 10000; // repeat every sec.
			/*timer=new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				int i;

				public void run() {
					gps.getLocation();
					if (gps.locationArray != null) {
						locationArrayRec = gps.locationArray;

					}
					i++;
				}
			}, delay, period);*/
			mRecorder.setOnInfoListener(
					new MediaRecorder.OnInfoListener() {
						public void onInfo(MediaRecorder mr, int w, int ex) {
							if (w == MediaRecorder.
									MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
							{
								try {
									stop();
								} catch (IOException e) {
									e.printStackTrace();
								}
//								deleteOldVideos();
								mergeEvent(caz);
								try {
									start();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
			);
			mRecorder.setOnErrorListener(
					new MediaRecorder.OnErrorListener() {
						public void onError(MediaRecorder mr, int w, int ex) {
							try {
								stop();
							} catch (IOException e) {
								e.printStackTrace();
							}
							deleteOldVideos();
							try {
								start();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
			);

			// Begin recording.
			try {
				mRecorder.prepare();
				mRecorder.start();
			} catch (Exception e) {
				stop();
			}
		}
	}

	public void mergeEvent(int caz){
		File[] files=getEventFiles(false);
		for (int i = 0; i < files.length; i++) {
			MediaScannerConnection.scanFile(mAct,
					new String[] { files[i].toString() }, null,
					new MediaScannerConnection.OnScanCompletedListener() {
						public void onScanCompleted(String path, Uri uri) {
						}
					});
		}

		ArrayList videosToMerge = new ArrayList<File>();
		//the recent file>=20 , we use only that one
		if(caz==1){
			File firstpart=getEventFiles(false)[1];
			File secondpart=getEventFiles(false)[0];
			videosToMerge.add(firstpart);
			videosToMerge.add(secondpart);
		}
		//the recent file<20 and the next file was recorded right before so we use both
		if(caz==2){
			File firstpart=getEventFiles(false)[1];
			File secondpart=getFiles(false)[0];
			File thirdpart=getEventFiles(false)[0];

			videosToMerge.add(firstpart);
			videosToMerge.add(secondpart);
			videosToMerge.add(thirdpart);
		}
		//the recent file<20 but the next file wasn't recorded right before(3) or doesn't exist
		if(caz==3||caz==4){
			File firstpart=getFiles(false)[0];
			File secondpart=getEventFiles(false)[0];
			//deleteOldVideos();
			videosToMerge.add(firstpart);
			videosToMerge.add(secondpart);
		}
		if(caz==5){
			//no need to merge only save in parse
			creationInParse();
		}
		if(caz!=5) {
			workingPath = getEventOutputDir().toString();
			mergeVideos = new MergeVideos(workingPath, videosToMerge).execute();
		}//up=new UploaderMe(getEventFiles(false)[0].getName(),getEventFiles(false)[0].getAbsolutePath(),contexthere);
		//up.setFileToUpload();

	}


	private class TrimmVideo extends AsyncTask<Void, Void, Void> {
		private String mediaPath;
		private double startTime;
		private double endTime;
		private int length;
		private ProgressDialog progressDialog;

		private TrimmVideo(String mediaPath, int startTime, int length) {
			this.mediaPath = mediaPath;
			this.startTime = startTime;
			this.length = length;
			this.endTime = this.startTime + this.length;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(contexthere,
					"Processing video", "Please wait...", true);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			trimVideo();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			super.onPostExecute(result);
		}

		private void trimVideo() {
			try {
				File file = new File(mediaPath);
				FileInputStream fis = new FileInputStream(file);
				FileChannel in = fis.getChannel();
				Movie movie = MovieCreator.build(in);

				List<Track> tracks = movie.getTracks();
				movie.setTracks(new LinkedList<Track>());

				boolean timeCorrected = false;

				// Here we try to find a track that has sync samples. Since we can only start decoding
				// at such a sample we SHOULD make sure that the start of the new fragment is exactly
				// such a frame
				for (Track track : tracks) {
					if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
						if (timeCorrected) {
							// This exception here could be a false positive in case we have multiple tracks
							// with sync samples at exactly the same positions. E.g. a single movie containing
							// multiple qualities of the same video (Microsoft Smooth Streaming file)

							//throw new RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.");
						} else {
							startTime = correctTimeToNextSyncSample(track, startTime);
							timeCorrected = true;
						}
					}
				}

				for (Track track : tracks) {
					long currentSample = 0;
					double currentTime = 0;
					long startSample = -1;
					long endSample = -1;

					for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
						TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
						for (int j = 0; j < entry.getCount(); j++) {
							// entry.getDelta() is the amount of time the current sample covers.

							if (currentTime <= startTime) {
								// current sample is still before the new starttime
								startSample = currentSample;
							} else if (currentTime <= endTime) {
								// current sample is after the new start time and still before the new endtime
								endSample = currentSample;
							} else {
								// current sample is after the end of the cropped video
								break;
							}
							currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
							currentSample++;
						}
					}
					movie.addTrack(new CroppedTrack(track, startSample, endSample));
				}
				//if(startTime==length)
				//throw new Exception("times are equal, something went bad in the conversion");

				IsoFile out = new DefaultMp4Builder().build(movie);

				File storagePath = getEventOutputDir();
				//storagePath.mkdirs();

				long timestamp=new Date().getTime();
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
						.format(new Date());
				File myMovie = new File(storagePath, String.format("trimmed-%s.mp4", timeStamp));
				MediaScannerConnection.scanFile(mAct,
						new String[]{myMovie.toString()}, null,
						new MediaScannerConnection.OnScanCompletedListener() {
							public void onScanCompleted(String path, Uri uri) {

							}
						});
				FileOutputStream fos = new FileOutputStream(myMovie);
				FileChannel fc = fos.getChannel();
				out.getBox(fc);

				fc.close();
				fos.close();
				fis.close();
				in.close();
			} catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		private double correctTimeToNextSyncSample(Track track, double cutHere) {
			double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
			long currentSample = 0;
			double currentTime = 0;
			for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
				TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
				for (int j = 0; j < entry.getCount(); j++) {
					if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
						// samples always start with 1 but we start with zero therefore +1
						timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
					}
					currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
					currentSample++;
				}
			}
			for (double timeOfSyncSample : timeOfSyncSamples) {
				if (timeOfSyncSample > cutHere) {
					return timeOfSyncSample;
				}
			}
			return timeOfSyncSamples[timeOfSyncSamples.length - 1];
		}
	}

	private class MergeVideos extends AsyncTask<String, Integer, String> {

		//The working path where the video files are located
		private String workingPath;
		//The file names to merge
		private ArrayList<File> videosToMerge;
		//Dialog to show to the user
		private ProgressDialog progressDialog;

		private MergeVideos(String workingPath, ArrayList<File> videosToMerge) {
			this.workingPath = workingPath;
			this.videosToMerge = videosToMerge;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(contexthere,
					"Saving event videos", "Please wait...", true);
		}

		@Override
		protected String doInBackground(String... params) {
			int count = videosToMerge.size();
			if(count>1) {
				try {
					Movie[] inMovies = new Movie[count];
					for (int i = 0; i < count; i++) {
						//File file = new File(videosToMerge.get(i));
						if (videosToMerge.get(i).exists()) {
							FileInputStream fis = new FileInputStream(videosToMerge.get(i));
							FileChannel fc = fis.getChannel();
							inMovies[i] = MovieCreator.build(fc);
							fis.close();
							fc.close();
						}
					}
					List<Track> videoTracks = new LinkedList<Track>();
					List<Track> audioTracks = new LinkedList<Track>();

					for (Movie m : inMovies) {
						for (Track t : m.getTracks()) {
							if (t.getHandler().equals("soun")) {
								audioTracks.add(t);
							}
							if (t.getHandler().equals("vide")) {
								videoTracks.add(t);
							}
							if (t.getHandler().equals("")) {

							}
						}
					}

					Movie result = new Movie();

					if (audioTracks.size() > 0) {
						result.addTrack(new AppendTrack(audioTracks
								.toArray(new Track[audioTracks.size()])));
					}
					if (videoTracks.size() > 0) {
						result.addTrack(new AppendTrack(videoTracks
								.toArray(new Track[videoTracks.size()])));
					}
					IsoFile out = new DefaultMp4Builder()
							.build(result);

					//rotate video
					out.getMovieBox().getMovieHeaderBox().setMatrix(ROTATE_270);

					long timestamp = new Date().getTime();
					String timestampS = "" + timestamp;

					File storagePath = new File(workingPath);
					storagePath.mkdirs();
					//the name of event is the same as the name of the video created from the last 20 sec recorded specially for the event
					String timeStamp = videosToMerge.get(videosToMerge.size() - 1).getName().split("-")[1].split(".mp4")[0];
					//new SimpleDateFormat("yyyyMMdd_HHmmss")
					//.format(new Date());
					File myMovie = new File(storagePath, String.format("event-%s.mp4", timeStamp));
					MediaScannerConnection.scanFile(mAct,
							new String[]{myMovie.toString()}, null,
							new MediaScannerConnection.OnScanCompletedListener() {
								public void onScanCompleted(String path, Uri uri) {

								}
							});
					FileOutputStream fos = new FileOutputStream(myMovie);
					FileChannel fco = fos.getChannel();
					fco.position(0);
					out.getBox(fco);
					fco.close();
					fos.close();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String mFileName = Environment.getExternalStorageDirectory()
						.getAbsolutePath();
				mFileName += "/output.mp4";
				return mFileName;

			}
			else
				return videosToMerge.get(0).getAbsolutePath();
		}

		@Override
		protected void onPostExecute(String value) {
			super.onPostExecute(value);
			deleteUnnecessaryVideos();

			//saving in amazon disabled

			/*System.setProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY, "true");
			amazonserv=new AmazonService(getEventFiles(false)[0].getName(),getEventFiles(false)[0].getPath(),contexthere).execute();

			try {
				Log.d("key",amazonserv.get().toString());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}*/
			creationInParse();
			progressDialog.dismiss();

			//rename the file so that I have the object_key inside
			/*
			String name=getEventFiles(false)[0].getName().split(".mp4")[0]+"-";
			File to= new File(getEventOutputDir(),name);
			getEventFiles(false)[0].renameTo(to);
			Log.d("NAME",getEventFiles(false)[0].getName());
			*/
			//CreateEventCloud(getEventFiles(false)[0]);
		}


	}
	public void creationInParse(){
		Geocoder geoo = new Geocoder(ADrivingCamera.this.getContext(), Locale.getDefault());
		String locc=GPSTracker.getLocationName(geoo,gpsRecording.getLatitude(),gpsRecording.getLongitude(),contexthere);
		if(locc==null)
			locc="unknown";
		CreateEventCloud createvent= null;
		File location_File=ManageFiles.getFiles(false,ManageFiles.getOutputDir("locationVideo"))[0];

		//rename location name


		if (timestampArrayRec!=null) {
			//createvent = new CreateEventCloud(getEventFiles(false)[0],gps.locationArray,location_File, gps.getLocation(), locc, amazonserv.get().toString(), contexthere);
			createvent = new CreateEventCloud(getEventFiles(false)[0],gpsRecording.locationArray,location_File, gpsRecording.getLocation(), locc, "not saved in Amazon", contexthere);

		}
		else
			//createvent = new CreateEventCloud(getEventFiles(false)[0],gps.getLocation(),location_File,locc,amazonserv.get().toString(),contexthere);
			createvent = new CreateEventCloud(getEventFiles(false)[0],gpsRecording.getLocation(),location_File,locc,"not saved in Amazon",contexthere);



		createvent.createEvent();
		updateGallery(getEventFiles(false));
	}

	//location
	public String getLocationName(){
		String add_for_display;
		if(ADrivingActivity.netConnect(mAct))
		try{
			Geocoder geo = new Geocoder(ADrivingCamera.this.getContext(), Locale.getDefault());
			List<android.location.Address> addresses = geo.getFromLocation(coordinates[0], coordinates[1], 1);
			if (addresses.isEmpty()) {
				return ("Waiting for Location");
			}
			else {
				if (addresses.size() > 0) {

					add_for_display=addresses.get(0).getThoroughfare()+" "+addresses.get(0).getFeatureName()+","+addresses.get(0).getLocality();
					return add_for_display;
					//location.setText(addresses.toString());

				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public double[] getCoordinates(){
		//gps = new GPSTracker(ADrivingCamera.this.getContext());

		// check if GPS enabled
		if(gpsRecording.canGetLocation()){
			double latitude = gpsRecording.getLatitude();
			double longitude = gpsRecording.getLongitude();
			// \n is for new line
			//Toast.makeText(getContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
			return new double[] {latitude, longitude};
		}else{
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gpsRecording.showSettingsAlert();
		}
		return null;
	}
	public void deleteUnnecessaryVideos(){
		File[] files=getEventFiles(false);
		ArrayList<File> arrayToDelete= new ArrayList<File>();
		for (int i = 0; i < files.length; i++) {
			if(!files[i].getName().split("-")[0].equals("event")){
				files[i].delete();
				updateFile(files[i]);
				if(ManageFiles.getOutputDir("locationEvent", files[i].getName().split("-")[1].split(".mp4")[0]).exists())
					//ManageFiles.getOutputDir("locationEvent", files[i].getName().split("-")[1].split(".mp4")[0]).delete();
					arrayToDelete.add(ManageFiles.getOutputDir("locationEvent", files[i].getName().split("-")[1].split(".mp4")[0]));
			}

		}
		updateGallery(getEventFiles(false));
		//updateGallery(ManageFiles.getFiles(false,ManageFiles.getOutputDir("locationEvent")));
	}

	public void updateFile(File file){
		MediaScannerConnection.scanFile(mAct,
				new String[] { file.toString() }, null,
				new MediaScannerConnection.OnScanCompletedListener() {
					public void onScanCompleted(String path, Uri uri) {

					}
				});
	}

	public void updateGallery(File[] files)
	{
		for (int i = 0; i < files.length; i++) {
			MediaScannerConnection.scanFile(mAct,
					new String[] { files[i].toString() }, null,
					new MediaScannerConnection.OnScanCompletedListener() {
						public void onScanCompleted(String path, Uri uri) {

						}
					});

		}


	}

	public void getPreferences(){
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(contexthere);
		this.maxHistory = Integer.parseInt(sharedPrefs.getString("prefMemoryUsed", "750"));
		this.audioFeedback=sharedPrefs.getBoolean("audioFeedback", true);
		this.audioType=sharedPrefs.getBoolean("audioType",true);
		this.resolution_width=Integer.parseInt(sharedPrefs.getString("prefResolution", "1920 1080").split(" ")[0]);
		this.resolution_height=Integer.parseInt(sharedPrefs.getString("prefResolution","1920 1080").split(" ")[1]);



	}

}

