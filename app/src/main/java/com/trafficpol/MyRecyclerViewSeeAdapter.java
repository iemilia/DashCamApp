package com.trafficpol;

import android.content.Context;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyRecyclerViewSeeAdapter extends RecyclerView.Adapter<MyRecyclerViewVideosAdapter
        .VideoObjectHolder> {

    //items to dispaly
    private String[] mDataset;
    private int[] mDatasetTypes;
    private static MyClickListener myClickListener;
    private EventObject eventObject;
    private File location_videoObject;
    Context context;


    //items for event
    boolean audioType=true;
    String description="";
    int nrCuts=0;

    //holders
    private ViewHolderVideoPlayer holderVideo;
    private ViewHolderStatus holderStatus;
    private ViewHolderDetails holderDetails;
    final ParseApplication globalVariable= CardViewUnreportedActivity.globalVariable;



    // Provide a suitable constructor (depends on the kind of dataset)
    public MyRecyclerViewSeeAdapter(Context context, EventObject eventObject, int[] mDatasetTypes) {
        this.context = context;
        this.eventObject = eventObject;
        this.mDatasetTypes=mDatasetTypes;
        try {
            this.audioType=getAudioType(eventObject.getSourceFile().getName());
        } catch (ParseException e) {
            this.audioType=true;
            e.printStackTrace();
        }

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
            return new ViewHolderVideoPlayer(v,context,audioType,nrCuts);
        }
        else if (viewType==2) {
            v = LayoutInflater.from(context)
                    .inflate(R.layout.card_location_date_short, parent, false);
            return new ViewHolderDetails(v,ManageFiles.readCoordinatesFromFile(eventObject.getLocationFile()));
        }
        else  {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_feedback, parent, false);
            return new ViewHolderStatus(v);
        }

    }

    @Override
    public void onBindViewHolder(MyRecyclerViewVideosAdapter
                                             .VideoObjectHolder viewHolder, final int position) {


        if (viewHolder.getItemViewType() == 1) {
            holderVideo = (ViewHolderVideoPlayer) viewHolder;
            holderVideo.bind_seeOnly(eventObject);
            this.audioType=holderVideo.audioType;
        }

        else if (viewHolder.getItemViewType() == 2) {
            holderDetails = (ViewHolderDetails) viewHolder;
            try {
                holderDetails.day.setText(eventObject.getDayEvent());
                holderDetails.month.setText(eventObject.getMonthEvent());
                // Log.d("locatie",mDataset.get(position).getLocationEvent());

                //get location
                Geocoder geo = new Geocoder(this.context, Locale.getDefault());
                String location=eventObject.getLocationEvent();
                holderDetails.location.setText(location);
                holderDetails.hourVideo.setText(eventObject.getHourEvent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else  {
            holderStatus = (ViewHolderStatus) viewHolder;
            if(eventObject.getReportedTime()!=null) {
                holderStatus.day_report.setText(eventObject.getReportedTime().toString().split(" ")[2]);
                //holderStatus.day_report.setText(eventObject.getReportedTime().toString());
                holderStatus.month_report.setText(eventObject.getReportedTime().toString().split(" ")[1]);
                holderStatus.status_report.setText(eventObject.getStatusEvent().toString());
                holderStatus.feedback_received.setText(eventObject.getMessagePolice().toString());

                switch(eventObject.getStatusEvent().toString()){
                    case "UNREPORTED": {
                        holderStatus.feedback_color.setBackgroundColor(context.getResources().getColor(R.color.unreported_red_upper));
                       // holder.container_down.setBackgroundColor(context.getResources().getColor(R.color.unreported_red_down));
                        break;
                    }
                    case "REPORTED": {
                        holderStatus.feedback_color.setBackgroundColor(context.getResources().getColor(R.color.approved_green_upper));
                       // holder.container_down.setBackgroundColor(context.getResources().getColor(R.color.approvedi_green_down));
                        break;
                    }

                    case "in progress": {
                        holderStatus.feedback_color.setBackgroundColor(context.getResources().getColor(R.color.orange));
                        //holder.container_down.setBackgroundColor(context.getResources().getColor(R.color.approvedi_green_down));
                        break;
                    }
                    case "rejected": {
                        holderStatus.feedback_color.setBackgroundColor(context.getResources().getColor(R.color.rejected_grey_upper));
                        //holder.container_down.setBackgroundColor(context.getResources().getColor(R.color.rejected_grey_down));
                        break;
                    }
                    case "accepted": {
                        holderStatus.feedback_color.setBackgroundColor(context.getResources().getColor(R.color.approved_green_upper));
                        //holder.container_down.setBackgroundColor(context.getResources().getColor(R.color.approvedi_green_down));
                        break;
                    }
                    default:{
                        holderStatus.feedback_color.setBackgroundColor(context.getResources().getColor(R.color.darkBlue));
                        //holder.container_down.setBackgroundColor(context.getResources().getColor(R.color.lightBlue));
                        break;
                    }
                }
            }

            else {
                holderStatus.day_report.setText("unavailable");
                holderStatus.month_report.setText("unavailable");
            }
        }
    }

    public boolean getAudioType(String eventName) throws ParseException {
        ParseQuery query = new ParseQuery("EditedEvent");
        query.whereEqualTo("title", eventName);
        query.whereEqualTo("user", globalVariable.getUserName());
        List<ParseObject> list_to_update=query.find();
        ParseObject to_update=list_to_update.get(0);
        return to_update.getBoolean("audio_on");
    }

    public void updateParentParse(String eventName) throws com.parse.ParseException {
        ParseQuery query = new ParseQuery("Event");
        query.whereEqualTo("title", eventName);
        query.whereEqualTo("user", globalVariable.getUserName());
        List<ParseObject> list_to_update=query.find();
        ParseObject to_update=list_to_update.get(0);
        to_update.put("status_event","EDITED & REPORTED");
        to_update.save();

    }
    public void createReport(boolean audioType,ArrayList<Integer>cuts,File file,File locationFileParent,String parentName,String description,String category){
        int length=0;
        int startTime=0;
        AsyncTask<Void, Void, Void> trimVideo;

        if(cuts.get(0)<cuts.get(1)){
            startTime=cuts.get(0);
            length=cuts.get(1)-cuts.get(0);
        }
        else{
            startTime=cuts.get(1);
            length=cuts.get(0)-cuts.get(1);
        }
        startTime=startTime/1000;
        length=length/1000;
//        public TrimVideo(String mediaPath, int startTime, int length, Context context,boolean audioType, File locationParentFile, String parentVideo) {

            trimVideo = new TrimVideo(file.toString(), startTime, length,context,audioType,locationFileParent,parentName,description,category).execute();

    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;

    }
    public interface MyClickListener {
         void onItemClick(int position, View v);
    }
}