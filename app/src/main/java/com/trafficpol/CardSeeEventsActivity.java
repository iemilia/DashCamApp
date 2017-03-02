package com.trafficpol;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Emilia on 3/22/2016.
 */
public class CardSeeEventsActivity extends Activity{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    final ParseApplication globalVariable= CardViewUnreportedActivity.globalVariable;
    private Tracker mTracker;


    private String[] mDataset ;
    private static String LOG_TAG = "CardSeeVideoActivity";
    private int position_video_to_edit;
    private String status="unavailable",
            messagePolice=" ";
    private int mDatasetTypes[] = {1, 2, 3}; //view types

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerviewedit);

        ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        setTitle("       See Event");
        mTracker = globalVariable.getDefaultTracker();
        position_video_to_edit=getIntent().getIntExtra("position_video_to_edit",1);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Code to Add an item with default animation
        //((MyRecyclerViewEventsAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MyRecyclerViewEventsAdapter) mAdapter).deleteItem(index);
    }
    @Override
    public void onStart() {
        super.onStart();
        if(!ADrivingActivity.netConnect(getApplicationContext()))
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.no_internet_warning),
                    Toast.LENGTH_SHORT
            ).show();
        try {
            mAdapter = new MyRecyclerViewSeeAdapter(this, getDataSet(position_video_to_edit), mDatasetTypes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecyclerView.setAdapter(mAdapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (globalVariable.getUserName().equals("null")) {
            finish();
        }
        ((MyRecyclerViewSeeAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewSeeAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
            }
        });
        Log.i("activity", "Setting screen name: " + "CardSeeEventsActivity");
        mTracker.setScreenName("SCREEN: See Events");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactionbar, menu);
        return true;
    }



    private EventObject getDataSet(int position) throws IOException {
        File fileVideo = ADrivingCamera.getEventFiles(false)[position];

        File filePossibleLocationOne = ManageFiles.getOutputDir("locationEvent", fileVideo.getName().split("-")[1].split(".mp4")[0]);
        File filePossibleLocationTwo = ManageFiles.getOutputDir("locationEvent", fileVideo.getName().split("-")[1].split(".mp4")[0]+"-saved");
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
        }
        EventObject event= new EventObject(fileVideo);
            //ex. event-20160420_030239.mp4
            //retreive data about the file
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        FileInputStream fis = new FileInputStream(fileVideo.toString());
        mmr.setDataSource(fis.getFD()); //gets the underlying file descriptor
        event.setTimeEvent(fileVideo.getName().split("_")[1].split(".mp4")[0] + ", " + fileVideo.getName().split("_")[0]);
        event.setDayEvent(fileVideo.getName().split("_")[0].split("-")[1].substring(6, 8));
        event.setMonthEvent(DisplayNicely.displayMonth(fileVideo.getName().split("_")[0].split("-")[1].substring(4, 6)));

        event.setHourEvent(fileVideo.getName().split("_")[1].split(".mp4")[0].substring(0, 2)
                + ":" + fileVideo.getName().split("_")[1].split(".mp4")[0].substring(2, 4));
        //get reported date
        Date report_date= null;
        if(ADrivingActivity.netConnect(getApplicationContext()))
            {
                try {
                report_date = getDateEvent(event.getSourceFile().getName());
                event.setReportedTime(report_date);
                event.setStatusEvent(this.status);
                event.setMessagePolice(this.messagePolice);
            } catch (ParseException e) {
                e.printStackTrace();
        }
        }

        event.setLocationFile(fileLocationVideoInit);
            //read location from file
        event.setLocationEvent(readLocationFromFile(fileLocationVideoInit,getApplicationContext()));
        event.setLocationEvent(readLocationFromFile(fileLocationVideoInit,getApplicationContext()));
        //create & set thumbnail of event
        Bitmap thumb=createThumbnail(fileVideo);
        event.setThumbnail(thumb);
        return event;
    }
    public Bitmap createThumbnail(File filePath){
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePath.toString(), MediaStore.Video.Thumbnails.MINI_KIND);
        return thumb;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.title:
                Toast.makeText(this, "Driving mode", Toast.LENGTH_SHORT).show();
                Intent intent= new Intent(getApplicationContext(),ADrivingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case android.R.id.home:
                Toast.makeText(this, "Driving mode", Toast.LENGTH_SHORT).show();
                intent= new Intent(getApplicationContext(),ADrivingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.events:
                Toast.makeText(this, "Menu item 1 selected", Toast.LENGTH_SHORT).show();
                intent= new Intent(getApplicationContext(),CardViewUnreportedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.reported:
                Toast.makeText(this, "Menu item 2 selected", Toast.LENGTH_SHORT).show();
                intent= new Intent(getApplicationContext(),CardViewReportedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.all_videos:
                Toast.makeText(this, "Item 3: All Videos selected", Toast.LENGTH_SHORT).show();
                intent= new Intent(getApplicationContext(),CardViewAllVideos.class);
                startActivity(intent);
                break;
            case R.id.settings:
                Toast.makeText(this, "Menu Item 3 selected", Toast.LENGTH_SHORT).show();
                intent= new Intent(getApplicationContext(),UserSettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return true;
    }

    public Date getDateEvent(String eventName) throws ParseException {
        Date date =new Date();

        ParseQuery query;
        if(eventName.length()>26)
            query = new ParseQuery("EditedEvent");
        else
            query = new ParseQuery("Event");
        //query.whereEqualTo("title", eventName.split("-edited.mp4")[0]+".mp4");
        query.whereEqualTo("title", eventName);
        query.whereEqualTo("user", globalVariable.getUserName());


        List<ParseObject> list_to_update=query.find();
        ParseObject to_update;
        if(list_to_update.size()!=0) {
            to_update = list_to_update.get(0);
            this.status=to_update.getString("status_event");
            if(to_update.getString("message_police")!=null)
                this.messagePolice=to_update.getString("message_police");
            else
                this.messagePolice="We are working for your safety!";
            return to_update.getCreatedAt();
        }
        return date;
    }
    public static String readLocationFromFile(File locationFile,Context context) {
        InputStream inputStream = null;
        BufferedReader br = null;

        try {
            // read this file into InputStream
            inputStream = new FileInputStream(locationFile);
            br = new BufferedReader(new InputStreamReader(inputStream));
            String location_to_returnOne;
            String location_to_return=br.readLine();
            if ((location_to_returnOne = br.readLine()) != null&& !location_to_returnOne.equals("null"))
                return location_to_returnOne;

            if(ADrivingActivity.netConnect(context)&& location_to_return!=null &&!location_to_return.equals("no location available ")){
                double latitude=Double.parseDouble(location_to_return.split(" ")[1]);
                double longitude=Double.parseDouble(location_to_return.split(" ")[2]);
                Geocoder geo = new Geocoder(context, Locale.getDefault());
                String location=GPSTracker.getLocationName(geo,latitude,longitude,context);
                if (location!=null)
                    return location;
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
        return "no location found saved";
    }
}
