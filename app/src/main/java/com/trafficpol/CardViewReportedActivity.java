package com.trafficpol;

/**
 * Created by Emilia on 3/16/2016.
 */
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class CardViewReportedActivity extends Activity{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewReportedActivity";
    final ParseApplication globalVariable= CardViewUnreportedActivity.globalVariable;
    private Tracker mTracker;
    String check_net="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);

        ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        setTitle("       Reported Events");

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mTracker = globalVariable.getDefaultTracker();
    }

    @Override
    protected void onStart(){
        super.onStart();
        ArrayList<EventObject> events=new ArrayList<EventObject>();
        ArrayList<Integer> positions=new ArrayList<Integer>();
        Object[] event_loc;
        try {
            event_loc=getDataSet();
            events= (ArrayList<EventObject>) event_loc[0];
            positions= (ArrayList<Integer>) event_loc[1];
            if(check_net.equals("available"))
                mAdapter = new MyRecyclerViewEventsAdapter(this,events,positions,"REPORTED");
            else
                mAdapter = new MyRecyclerViewEventsAdapter(this,events,positions,"unavailable");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mRecyclerView.setAdapter(mAdapter);
        if(events.size()==0)
            Toast.makeText(this, "There are no reported events to display", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (globalVariable.getUserName().equals("null")) {
            finish();
        }
        ((MyRecyclerViewEventsAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewEventsAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
            }
        });
        Log.i("activity", "Setting screen name: " + "CardViewReported");
        mTracker.setScreenName("SCREEN: View Reported Events");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.title:
                Toast.makeText(this, "Driving mode", Toast.LENGTH_SHORT).show();
                Intent intent= new Intent(getApplicationContext(),ADrivingActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                Toast.makeText(this, "Driving mode", Toast.LENGTH_SHORT).show();
                intent= new Intent(getApplicationContext(),ADrivingActivity.class);
                startActivity(intent);
                break;
            case R.id.events:
                Toast.makeText(this, "Item 1: Unreported Events selected", Toast.LENGTH_SHORT).show();
                intent= new Intent(getApplicationContext(),CardViewUnreportedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.reported:
                Toast.makeText(this, "You are here", Toast.LENGTH_SHORT).show();
                break;
            case R.id.all_videos:
                Toast.makeText(this, "Item 3: All Videos selected", Toast.LENGTH_SHORT).show();
                intent= new Intent(getApplicationContext(),CardViewAllVideos.class);
                startActivity(intent);
                break;
            case R.id.settings:
                Toast.makeText(this, "Menu Item 3 selected", Toast.LENGTH_SHORT).show();
                intent= new Intent(getApplicationContext(),UserSettingActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private Object[] getDataSet() throws IOException, ParseException {
        ArrayList events = new ArrayList<EventObject>();
        ArrayList positions = new ArrayList<Integer>();
        ArrayList reported_positions = new ArrayList<Integer>();
        ArrayList<Date> reported_dates = new ArrayList<Date>();
        ArrayList<String> statuses = new ArrayList<String>();



        File[] filesEvent = ADrivingCamera.getEventFiles(false);
        if(ADrivingActivity.netConnect(getApplicationContext())){
            try {
                Object[] result_parse=new DataRetriever(filesEvent,CardViewReportedActivity.this,"EditedEvent").execute().get();
                reported_positions=(ArrayList<Integer>)result_parse[0];
                reported_dates=(ArrayList<Date>)result_parse[1];
                statuses=(ArrayList<String>)result_parse[2];
                check_net="available";
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        else{
            check_net="unavailable";
            Toast.makeText(this, "Network is unavailable", Toast.LENGTH_SHORT).show();
        }
        int position=0;
        if(check_net.equals("available"))
            for (int i =reported_positions.size()-1; i >-1 ; i--) {
                 int index=(Integer)reported_positions.get(i);
                EventObject event= new EventObject(filesEvent[index]);
                //set event status
                String status= statuses.get(i);

                /*if status is REPORTED than I display it
                * if it is EDITED& REPORTED, I don't because it means that there is the video created by
                * cutting from this element and it can be found in REPORTED,so I will have it from there*/
                positions.add(position,index);
                Log.d("index", String.valueOf(index));
                Log.d("i",String.valueOf(i));
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                FileInputStream fis = new FileInputStream(filesEvent[i].toString());
                mmr.setDataSource(fis.getFD()); //gets the underlying file descriptor
                event.setTimeEvent(filesEvent[index].getName().split("_")[1].split(".mp4")[0].substring(0,2)
                        + ":" +filesEvent[index].getName().split("_")[1].split(".mp4")[0].substring(2,4));
                event.setDayEvent(filesEvent[index].getName().split("_")[0].split("-")[1].substring(6, 8));
                event.setMonthEvent(filesEvent[index].getName().split("_")[0].split("-")[1].substring(4, 6));
                event.setYearEvent(filesEvent[index].getName().split("_")[0].split("-")[1].substring(0, 4));
                event.setMonthName(DisplayNicely.displayMonth(filesEvent[index].getName().split("_")[0].split("-")[1].substring(4, 6)));
                    //create & set thumbnail of event
                Bitmap thumb=createThumbnail(filesEvent[index]);
                event.setThumbnail(thumb);
                event.setSourceFile(filesEvent[index]);
                    //set Location
                    //search the name among location files
                File filePossibleLocationOne = ManageFiles.getOutputDir("locationEvent", filesEvent[index].getName().split("-")[1].split(".mp4")[0]);
                File filePossibleLocationTwo = ManageFiles.getOutputDir("locationEvent", filesEvent[index].getName().split("-")[1].split(".mp4")[0]+"-saved");
                File fileLocationVideoInit;

                if(filePossibleLocationOne.exists()){
                    fileLocationVideoInit=filePossibleLocationOne;
                }
                else
                if(filePossibleLocationTwo.exists())
                    fileLocationVideoInit=filePossibleLocationTwo;
                else{
                    if(ManageFiles.getFiles(false,ManageFiles.getOutputDir("default")).length!=0)
                        fileLocationVideoInit=ManageFiles.getFiles(false,ManageFiles.getOutputDir("default"))[0];
                    else{
                        fileLocationVideoInit=new File(ManageFiles.getOutputDir("default"),"default.txt");
                        BufferedWriter writer = new BufferedWriter(new FileWriter(fileLocationVideoInit));
                        writer.write("no location available ");
                        writer.close();
                    }
                }
                //get reported date
                Date report_date= null;
                //report_date = getDateEvent(event.getSourceFile().getName());
                report_date=reported_dates.get(i);
                event.setReportedTime(report_date);
                event.setLocationEvent(CardViewUnreportedActivity.readLocationFromFile(fileLocationVideoInit,getApplicationContext()));
                event.setStatusEvent(status);
                events.add(position, event);
                position++;
                }
        if(check_net.equals("unavailable"))
            for (int i = 0; i < filesEvent.length; i++) {
                EventObject event= new EventObject(filesEvent[i]);

                positions.add(position,i);
                String status= "unavailable";

                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                FileInputStream fis = new FileInputStream(filesEvent[i].toString());
                mmr.setDataSource(fis.getFD()); //gets the underlying file descriptor
                event.setTimeEvent(filesEvent[i].getName().split("_")[1].split(".mp4")[0].substring(0, 2)
                        + ":" + filesEvent[i].getName().split("_")[1].split(".mp4")[0].substring(2, 4));
                event.setDayEvent(filesEvent[i].getName().split("_")[0].split("-")[1].substring(6, 8));
                event.setMonthEvent(filesEvent[i].getName().split("_")[0].split("-")[1].substring(4, 6));
                event.setYearEvent(filesEvent[i].getName().split("_")[0].split("-")[1].substring(0, 4));
                event.setMonthName(DisplayNicely.displayMonth(filesEvent[i].getName().split("_")[0].split("-")[1].substring(4, 6)));
                //create & set thumbnail of event
                Bitmap thumb=createThumbnail(filesEvent[i]);
                event.setThumbnail(thumb);
                event.setSourceFile(filesEvent[i]);
                //set Location
                //search the name among location files
                File filePossibleLocationOne = ManageFiles.getOutputDir("locationEvent", filesEvent[i].getName().split("-")[1].split(".mp4")[0]);
                File filePossibleLocationTwo = ManageFiles.getOutputDir("locationEvent", filesEvent[i].getName().split("-")[1].split(".mp4")[0]+"-saved");
                File fileLocationVideoInit;

                if(filePossibleLocationOne.exists()){
                    fileLocationVideoInit=filePossibleLocationOne;
                }
                else
                if(filePossibleLocationTwo.exists())
                    fileLocationVideoInit=filePossibleLocationTwo;
                else{
                    if(ManageFiles.getFiles(false,ManageFiles.getOutputDir("default")).length!=0)
                        fileLocationVideoInit=ManageFiles.getFiles(false,ManageFiles.getOutputDir("default"))[0];
                    else
                        fileLocationVideoInit=new File(ManageFiles.getOutputDir("default"),"default");
                    BufferedWriter writer = new BufferedWriter(new FileWriter(fileLocationVideoInit));
                    writer.write("no location available ");
                    writer.close();
                } //get reported date
                event.setLocationEvent(CardViewUnreportedActivity.readLocationFromFile(fileLocationVideoInit,getApplicationContext()));
                event.setStatusEvent(status);
                events.add(position, event);
                position++;
        }
        return new Object[]{events,positions};
    }

    public Object[] getReportedPositons(File[] filesEvent) throws ParseException {
        ParseQuery query;
        ArrayList<String> namesFile=new ArrayList<String>();
        ArrayList<Integer> found=new ArrayList<Integer>();
        ArrayList<Date> updatedDates=new ArrayList<Date>();
        ArrayList<String> statuses=new ArrayList<String>();

        for(int i=0;i<filesEvent.length;i++){
            namesFile.add(i,filesEvent[i].getName());
        }
        query= new ParseQuery("EditedEvent");
        query.whereContains("user", globalVariable.getUserName());
        query.orderByDescending("CreatedDate");

        List<ParseObject> list_to_update=query.find();
        //List<String> names_reported=list_to_update.getString("title");
        int j=0;
        for(int i=0;i<list_to_update.size();i++)
            if(namesFile.contains(list_to_update.get(i).getString("title"))){
                found.add(j,namesFile.indexOf(list_to_update.get(i).getString("title")));
                updatedDates.add(j,list_to_update.get(i).getCreatedAt());
                statuses.add(j,list_to_update.get(i).getString("status_event"));
                j++;
            }
        return new Object[]{found,updatedDates,statuses};
    }
    public Date getDateEvent(String eventName) throws ParseException {
        Date date =new Date();

        ParseQuery query;
        if(eventName.length()>26)
            query = new ParseQuery("EditedEvent");
        else
            query = new ParseQuery("Event");
        query.whereEqualTo("title", eventName.split("-edited.mp4")[0]+".mp4");
        query.whereEqualTo("status_event","EDITED & REPORTED");
      //  query.wherewhereEqualTo("title", eventName);
        query.whereEqualTo("user", globalVariable.getUserName());


        List<ParseObject> list_to_update=query.find();

        ParseObject to_update;
        if(list_to_update.size()!=0) {
            to_update = list_to_update.get(0);
            return to_update.getCreatedAt();
        }
        return date;
    }
    public String getStatusEvent(String eventName) throws ParseException {

        ParseQuery query;
        if(eventName.length()>26)
            query = new ParseQuery("EditedEvent");
        else
            query = new ParseQuery("Event");
        //query.whereEqualTo("title", eventName.split("-edited.mp4")[0]+".mp4");

        query.whereEqualTo("title", eventName);
        List<ParseObject> list_to_update=query.find();
        ParseObject to_update;
        if(list_to_update.size()!=0) {
            to_update = list_to_update.get(0);
            return to_update.getString("status_event");
        }
        else return "loading...";
    }
    public Bitmap createThumbnail(File filePath){
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePath.toString(), MediaStore.Video.Thumbnails.MINI_KIND);
        Bitmap new_thumb=Bitmap.createScaledBitmap(thumb,300,160,false);
        return new_thumb;
    }


}