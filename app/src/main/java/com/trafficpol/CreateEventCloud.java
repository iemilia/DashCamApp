package com.trafficpol;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


import parseObjects.EditedEvent;
import parseObjects.Event;

/**
 * Created by Emi on 05.05.2015.
 */
public class CreateEventCloud {

    private static String logtag="EventParseUpload";
    private CognitoCachingCredentialsProvider credentialsProvider;
    private TransferUtility transferUtility;
   //private AmazonS3Client s3;
    private Event event;
    private String description,category;
    private EditedEvent editedEvent;
    private File videoEvent;
    private File locations_file;
    Location location;
    ArrayList<Double> latArray,longArray;
    ArrayList<Location> historyLocations;
    public String eventId;
    public String parentVideo;
    public boolean audio;
    final ParseApplication globalVariable= CardViewUnreportedActivity.globalVariable;
    public ProgressDialog proDialog;

    String OBJECT_KEY;
    Context mcontext;
    String address;
//    private ImageToTranslate image_to_translate_now;

    //used for edited events
    public CreateEventCloud(File videoEvent,Location loc,File locations_file,String address,String OBJECT_KEY,String parentVideo,boolean audio, String description,String category,Context context){
        editedEvent=new EditedEvent();
        this.videoEvent=videoEvent;
        this.parentVideo=parentVideo;
        this.location=loc;
        this.address=address;
        this.mcontext=context;
        this.description=description;
        this.category=category;
        this.locations_file=locations_file;
        this.OBJECT_KEY=OBJECT_KEY;
        this.audio=audio;
        //createEvent();
    }


    //used for events when recording
    public CreateEventCloud(File videoEvent,ArrayList<Location> historyLocations,File locations_file,Location loc,String address,String OBJECT_KEY,Context context){
        event=new Event();
        this.videoEvent=videoEvent;
        this.location=loc;
        this.address=address;
        this.mcontext=context;
        this.OBJECT_KEY=OBJECT_KEY;
        this.historyLocations=historyLocations;
        this.locations_file=locations_file;
        //createEvent();
    }

    public CreateEventCloud(File videoEvent,Location loc, File locations_file,String address,String OBJECT_KEY,Context context){
        event=new Event();
        this.videoEvent=videoEvent;
        this.location=loc;
        this.address=address;
        this.mcontext=context;
        this.OBJECT_KEY=OBJECT_KEY;
        this.historyLocations=historyLocations;
        this.locations_file=locations_file;
        //createEvent();
    }
    public void createEditedEvent() {
        startLoading();
        final ParseApplication globalVariable= CardViewUnreportedActivity.globalVariable;
        editedEvent.setUser(globalVariable.getUserName());
        editedEvent.setTitle(videoEvent.getName());
        editedEvent.setLocationFileTitle(locations_file.getName());
        editedEvent.setUserId(globalVariable.getUserId());
        editedEvent.setStatus("REPORTED");
        editedEvent.setParentVideo(parentVideo);
        editedEvent.setDescription(description);
        editedEvent.setCategory(category);
        editedEvent.setCutAudio(audio);
        InputStream inputStream = null;
        BufferedReader br = null;

        try {
            // read this file into InputStream
            inputStream = new FileInputStream(locations_file);
            br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            sb.append(locations_file.getName()+"\r\n");
            String rsslink=null;

            while ((rsslink = br.readLine()) != null)
            {
                sb.append(rsslink);
                sb.append("\r\n");
            }
            rsslink = sb.toString();

            ParseFile file = new ParseFile(sb.toString().getBytes());
            editedEvent.setLocationFile(file);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        try {
            String uri="not saved in Amazon";
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            FileInputStream fis = new FileInputStream(videoEvent);
            mmr.setDataSource(fis.getFD()); //gets the underlying file descriptor
            String duration = mmr.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION
            );
            String resolution = mmr.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
            )+ " "+mmr.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
            ) ;
            editedEvent.setSize(duration);
            editedEvent.setResolution(resolution);
            editedEvent.setAddress(this.address);
            editedEvent.setLongitude(location.getLongitude());
            editedEvent.setLatitude(location.getLatitude());
            editedEvent.setLinkAmazon(uri);
            if(historyLocations!=null) {
                for (int j = 0; j < historyLocations.size(); j++) {
                    longArray.add(historyLocations.get(j).getLongitude());
                    latArray.add(historyLocations.get(j).getLatitude());
                }
                event.setLongHistory(longArray);
                event.setLatHistory(latArray);
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        //final String ObjectId = getIntent().getStringExtra("objectId");
        final String ObjectId = globalVariable.getUserId();

        editedEvent.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {
                    eventId = editedEvent.getObjectId();
                    String name = ADrivingCamera.getEventFiles(false)[0].getName().split(".mp4")[0] + "-" + eventId;
                    File to = new File(ADrivingCamera.getEventOutputDir(), name);
                    //stopLoading();
                    //BabaRamCamera.getEventFiles(false)[0].renameTo(to);
                    //updateActivity(ObjectId);
                } else {
                    eventId = null;
                }
            }
        });
    }

    public void createEvent() {
        boolean netAvailable=ADrivingActivity.netConnect(mcontext);
        final ParseApplication globalVariable= ADrivingActivity.globalVariable;
        event.setUser(globalVariable.getUserName());
        event.setTitle(videoEvent.getName());
        event.setLocationFileTitle(locations_file.getName());
        event.setUserId(globalVariable.getUserId());
        event.setStatus("UNREPORTED");
        InputStream inputStream = null;
        BufferedReader br = null;

        try {
            // read this file into InputStream
            inputStream = new FileInputStream(locations_file);
            br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            sb.append(locations_file.getName()+"\r\n");
            String rsslink=null;

            while ((rsslink = br.readLine()) != null)
            {
                sb.append(rsslink);
                sb.append("\r\n");
            }
            rsslink = sb.toString();
            ParseFile file;
            if(netAvailable==true){
                file= new ParseFile(sb.toString().getBytes());
                event.setLocationFile(file);
            }



        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }




        try {

            String uri="not save in Amazon";
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            FileInputStream fis = new FileInputStream(videoEvent);
            mmr.setDataSource(fis.getFD()); //gets the underlying file descriptor
            String duration = mmr.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION
            );
            String resolution = mmr.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
            )+ " "+mmr.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
            ) ;
            event.setCutAudio(true);
            event.setSize(duration);
            event.setResolution(resolution);
            event.setAddress(this.address);
            event.setLongitude(location.getLongitude());
            event.setLatitude(location.getLatitude());
            event.setLinkAmazon(uri);
            if(historyLocations!=null) {
                for (int j = 0; j < historyLocations.size(); j++) {
                    longArray.add(historyLocations.get(j).getLongitude());
                    latArray.add(historyLocations.get(j).getLatitude());
                }
                event.setLongHistory(longArray);
                event.setLatHistory(latArray);
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        event.setDescription("");
        event.setCategory("");


        //final String ObjectId = getIntent().getStringExtra("objectId");
        final String ObjectId = globalVariable.getUserId();


        if(netAvailable)
            event.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("SUCCESS", "Parse Success");
                        eventId = event.getObjectId();
                        String name = ADrivingCamera.getEventFiles(false)[0].getName().split(".mp4")[0] + "-" + eventId;
                        File to = new File(ADrivingCamera.getEventOutputDir(), name);
                        //BabaRamCamera.getEventFiles(false)[0].renameTo(to);

                        //updateActivity(ObjectId);
                    } else {
                        Log.d("FAIL", "Parse Success");

                        eventId = null;
                    }
                }
            });
        else{
            File file = new File(locations_file.getPath());
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                byte data[] = new byte[(int) file.length()];
                fis.read(data);

               // ParseObject obj = new ParseObject("Locations");
               // obj.put("name", locations_file.getName());
               // obj.put("data", data);
               // obj.saveEventually();
            } catch (IOException e) {
                e.printStackTrace();
            }
            event.saveEventually();

        }

    }
    protected void startLoading() {
        proDialog = new ProgressDialog(mcontext);
        proDialog.setMessage("Saving event...");
        proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        proDialog.setCancelable(false);
        proDialog.show();
    }
    protected void stopLoading() {
        if ((proDialog != null) && proDialog.isShowing())
            proDialog.dismiss();
        proDialog = null;
    }
    public void postEvent()
    {
        new Thread(new Runnable() {
            public void run() {
                event.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {


                        if (e == null) {
                            // updateActivity(ObjectId);


                        } else {
                        }
                    }
                });
            }
        }).start();
    }

}
