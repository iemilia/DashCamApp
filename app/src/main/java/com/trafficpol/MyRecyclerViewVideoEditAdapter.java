package com.trafficpol;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyRecyclerViewVideoEditAdapter extends RecyclerView.Adapter<MyRecyclerViewVideosAdapter
        .VideoObjectHolder> {

    //items to dispaly
    private String[] mDataset;
    private int[] mDatasetTypes;
    private static MyClickListener myClickListener;
    private VideoObject videoObject;
    private File location_videoObject;
    Context context;
    final ParseApplication globalVariable= CardViewUnreportedActivity.globalVariable;



    //items for event
    public boolean audioType;
    String description="";
    String category="";
    int nrCuts=0;
    int cutOne=0,cutTwo=0;


    //holders
    private ViewHolderVideoPlayer holderVideo;
    private ViewHolderDescription holderDescription;
    private ViewHolderDetails holderDetails;


    // Provide a suitable constructor (depends on the kind of dataset)
    public MyRecyclerViewVideoEditAdapter(Context context, VideoObject videoObject, int[] mDatasetTypes) {
        this.context = context;
        this.videoObject = videoObject;
        this.mDatasetTypes=mDatasetTypes;
        this.audioType=true;//are sunet

    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDatasetTypes.length;
    }

    // //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        return mDatasetTypes[position];
    }

    @Override
    public MyRecyclerViewVideosAdapter
            .VideoObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        Context context = parent.getContext();
        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_video_player, parent, false);
            return new ViewHolderVideoPlayer(v,context,audioType,nrCuts,cutOne,cutTwo);
        }
        else if (viewType==2) {
            v = LayoutInflater.from(context)
                    .inflate(R.layout.card_description_catgory, parent, false);
            return new ViewHolderDescription(v,context);
        }
        else  {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_location_date, parent, false);
            String location= null;
            try {
                location = videoObject.getLocationVideo();
                //Log.d("location11",location);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Geocoder geo = new Geocoder(this.context, Locale.getDefault());
            //split coordinate
            // Get the position of the -/+ sign in location String, which indicates
            // the beginning of the longtitude.
            double latitude=0,longitude=0;
            if(location!=null){
                int index = location.lastIndexOf('-');
                if (index == -1) {
                    index = location.lastIndexOf('+');
                }
                 latitude = Double.parseDouble(location.substring(0, index - 1));
                 longitude = Double.parseDouble(location.substring(index));}
            return new ViewHolderDetails(v,String.valueOf(latitude)+" "+String.valueOf(longitude) );
        }

    }

    @Override
    public void onBindViewHolder(MyRecyclerViewVideosAdapter
                                             .VideoObjectHolder viewHolder, final int position) {


        if (viewHolder.getItemViewType() == 1) {
            holderVideo = (ViewHolderVideoPlayer) viewHolder;
            holderVideo.bind_video(videoObject);

            audioType=holderVideo.audioType;
            nrCuts=holderVideo.getCutsVideo().size();

            if(nrCuts==1){
                cutOne=holderVideo.getCutsVideo().get(0);
                cutTwo=0;
            }
            if (nrCuts==2){

                cutOne=holderVideo.getCutsVideo().get(0);
                cutTwo=holderVideo.getCutsVideo().get(1);
            }
            if (nrCuts==0){
                cutOne=0;
                cutTwo=0;
            }

        }
        else if (viewHolder.getItemViewType() == 2) {
            holderDescription = (ViewHolderDescription) viewHolder;


            //add category
        }
        else {
            holderDetails = (ViewHolderDetails) viewHolder;
            try {
                holderDetails.day.setText(videoObject.getDayVideo());
                holderDetails.month.setText(videoObject.getMonthVideo());
                // Log.d("locatie",mDataset.get(position).getLocationEvent());

                //get location
                String location=videoObject.getLocationVideo();

                Geocoder geo = new Geocoder(this.context, Locale.getDefault());
                //split coordinate
                // Get the position of the -/+ sign in location String, which indicates
                // the beginning of the longtitude.
                if(location!=null){
                    int index = location.lastIndexOf('-');
                    if (index == -1) {
                        index = location.lastIndexOf('+');
                    }
                    double latitude = Double.parseDouble(location.substring(0, index - 1));
                    double longitude = Double.parseDouble(location.substring(index));
                    Log.d("latitude",String.valueOf(latitude)+" "+String.valueOf(longitude));
                    holderDetails.location.setText(GPSTracker.getLocationName(geo, latitude, longitude, context));}

               // holderDetails.location.setText(location);
                holderDetails.hourVideo.setText(videoObject.getHourVideo());

                holderDetails.report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        // PostEvent postEvent=new PostEvent();
                        //updateVideoParse(videoObject.getName().split("-")[2]);
                        if (ADrivingActivity.netConnect(context)) {
                            description = holderDescription.getDescription();
                            category = holderDescription.getCategory();

                            ArrayList<Integer> cutPositions = holderVideo.getCutsVideo();
                            if (cutPositions.size() == 2) {
                                if(Math.abs(cutPositions.get(0) - cutPositions.get(1))<=5000){
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);

                                    builder1.setMessage("The video you are trying to cut should be longer than 5 seconds.Please reset the positions")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //do things
                                                }
                                            });
                                    AlertDialog alert = builder1.create();
                                    alert.show();
                                }
                                else {
                                    //rename location file
                                    File locationToSave = videoObject.getLocationFile();
                                    String name = videoObject.getLocationFile().getName().split(".txt")[0] + "-saved.txt";
                                    File to = new File(ManageFiles.getOutputDir("locationVideo"), name);
                                    locationToSave.renameTo(to);
                                    //  videoObject.setLocationFile(locationToSave);
                                    createReport(holderVideo.audioType, cutPositions, videoObject.getSourceFile(), videoObject.getLocationFile(), videoObject.getSourceFile().getName(), description, category);
                                    Intent intent = new Intent(v.getContext(), CardViewAllVideos.class);
                                    //intent.putExtra("position_video_to_edit", position);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    v.getContext().startActivity(intent);
                                }
                            } else if (cutPositions.size() == 1) {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                builder1.setMessage("The video will not be trimmed.There have to be 2 cut positions set. Do you want to continue?");
                                builder1.setCancelable(false);
                                builder1.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                ArrayList<Integer> cutPositions = new ArrayList<Integer>();
                                                cutPositions.add(0, 0);
                                                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                                                FileInputStream fis = null;
                                                try {
                                                    fis = new FileInputStream(videoObject.getSourceFile());
                                                    mmr.setDataSource(fis.getFD()); //gets the underlying file descriptor
                                                    String duration = mmr.extractMetadata(
                                                            MediaMetadataRetriever.METADATA_KEY_DURATION
                                                    );
                                                    cutPositions.add(1, Integer.parseInt(duration));
                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                String description = holderDescription.getDescription();
                                                String category = holderDescription.getCategory();

                                                //rename location file
                                                File locationToSave=videoObject.getLocationFile();
                                                String name=videoObject.getLocationFile().getName().split(".txt")[0]+"-saved.txt";
                                                File to= new File(ManageFiles.getOutputDir("locationVideo"),name);
                                                locationToSave.renameTo(to);
                                                //videoObject.setLocationFile(locationToSave);
                                               // Log.d("NAME",getEventFiles(false)[0].getName());

                                                //creez obiectul nou editat
                                                createReport(holderVideo.audioType, cutPositions, videoObject.getSourceFile(), videoObject.getLocationFile(), videoObject.getSourceFile().getName(), description, category);
                                                //File video_to_work_with = ManageFiles.getFiles(false, ManageFiles.getEdittedOutputDir())[0];
                                                Intent intent = new Intent(v.getContext(), CardViewAllVideos.class);
                                                //intent.putExtra("position_video_to_edit", position);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                v.getContext().startActivity(intent);
                                            }
                                        });

                                builder1.setNegativeButton(
                                        "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            } else {
                                //rename location file
                                File locationToSave=videoObject.getLocationFile();
                                String name=videoObject.getLocationFile().getName().split(".txt")[0]+"-saved.txt";
                                File to= new File(ManageFiles.getOutputDir("locationVideo"),name);
                                locationToSave.renameTo(to);
                                //videoObject.setLocationFile(locationToSave);

                                createReport(holderVideo.audioType, cutPositions, videoObject.getSourceFile(), videoObject.getLocationFile(), videoObject.getSourceFile().getName(), description, category);
                                Intent intent = new Intent(v.getContext(), CardViewAllVideos.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                v.getContext().startActivity(intent);
                            }
                        } else
                            Toast.makeText(v.getContext(), "Network is unavailable.Please come back later,for reporting.", Toast.LENGTH_SHORT).show();
                    }
            });
        } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


        public void updateParentParse(String eventName) throws com.parse.ParseException {
        ParseQuery query = new ParseQuery("Event");
        query.whereEqualTo("title", eventName);
        query.whereEqualTo("user",globalVariable.getUserName());

        List<ParseObject> list_to_update=query.find();
        ParseObject to_update;
        if(list_to_update.size()!=0) {
            to_update = list_to_update.get(0);
            to_update.put("status_event", "EDITED & REPORTED");
            to_update.save();
        }
    }
    public void createReport(boolean audioType,ArrayList<Integer>cuts,File file,File locationFileParent,String parentName,String description,String category) {
        int length = 0;
        int startTime = 0;
        AsyncTask<Void, Void, Void> trimVideo;
        if (cuts.size() > 0){
            if (cuts.get(0) < cuts.get(1)) {
                startTime = cuts.get(0);
                length = cuts.get(1) - cuts.get(0);
            } else {
                startTime = cuts.get(1);
                length = cuts.get(0) - cuts.get(1);
            }
        startTime = startTime / 1000;
        length = length / 1000;
        }
        else {
            startTime = 0;
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file.toString());
                mmr.setDataSource(fis.getFD());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String duration = mmr.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION
            );
            length = Integer.parseInt(duration) / 1000;
        }
        trimVideo = new TrimVideo(file.toString(), startTime, length,context,audioType,locationFileParent,parentName,description,category).execute();


    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;

    }
    public interface MyClickListener {
         void onItemClick(int position, View v);
    }
}