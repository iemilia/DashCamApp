package com.trafficpol;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Geocoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import com.amazonaws.SDKGlobalConfiguration;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Emilia on 3/28/2016.
 */
public class TrimVideo extends AsyncTask<Void, Void, Void> {
    private String mediaPath;
    private double startTime;
    private double endTime;
    private int length;
    private String description,category;
    private ProgressDialog progressDialog;
    private boolean audioType;
    private File locationParentFile;
    private String parentVideo;
    private Context mContext;
    private Activity mAct;

    public TrimVideo(String mediaPath, int startTime, int length, Context context,boolean audioType, File locationParentFile, String parentVideo,String description,String category) {
        this.mediaPath = mediaPath;
        this.description=description;
        this.category=category;
        this.locationParentFile=locationParentFile;
        this.startTime = startTime;
        this.parentVideo=parentVideo;
        this.length = length;
        this.endTime = this.startTime + this.length;
        this.mContext=context;
        mAct= (Activity) context;
        this.audioType=audioType;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(mContext,
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
        //just create edited  event  will be saved in cloud
        File video_to_work_with = ManageFiles.getFiles(false, ManageFiles.getEventOutputDir())[0];
        System.setProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY, "true");

        //remove Amazon saving
        /*AsyncTask<String,Boolean,String> amazonserv = new AmazonService(video_to_work_with.getName(), video_to_work_with.getPath(),mContext).execute();

        try {
            Log.d("key",amazonserv.get().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
        Geocoder geoo = new Geocoder(mContext, Locale.getDefault());
        GPSTracker gpss = new GPSTracker(mContext);
        String address=GPSTracker.getLocationName(geoo, gpss.getLatitude(), gpss.getLongitude(),mContext);
        CreateEventCloud createvent= null;
        File location_File=locationParentFile;

        //createvent = new CreateEventCloud(video_to_work_with,gpss.getLocation(),location_File, address, amazonserv.get().toString(),parentVideo,audioType,description,category,mContext);
        createvent = new CreateEventCloud(video_to_work_with,gpss.getLocation(),location_File, address, "not saved in Amazon",parentVideo,audioType,description,category,mContext);


        createvent.createEditedEvent();
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
            IsoFile out;
            if(audioType==false) {
                List<Track> audioTracks = new LinkedList<Track>();
                List<Track> videoTracks = new LinkedList<Track>();

                for (Track t : movie.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                }
                Movie result = new Movie();
                if (videoTracks.size() > 0) {
                    result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
                }
                 out = new DefaultMp4Builder().build(result);
            }
            else
             out = new DefaultMp4Builder().build(movie);


            File storagePath = ManageFiles.getEventOutputDir();
            //storagePath.mkdirs();

            long timestamp=new Date().getTime();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            String name=parentVideo.split(".mp4")[0];
            /* numele va fi in cazul in care parentVideo e event atunci : event-20161234_122343-edited.mp4
            * daca parentVideo este video atunci: trebuie transformat din 20161234_122343-edited.mp4 in
            * event-20161234_122343-edited.mp4*/
            //deocamdata caz 1: parentVideo e event atunci : event-20161234_122343-edited.mp4
            File myMovie;
            if(name.length()>16)
                myMovie = new File(storagePath, String.format("%s-edited.mp4", name));
            else
                myMovie = new File(storagePath, String.format("event-%s-edited.mp4", name));

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
