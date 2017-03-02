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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * Created by Emilia on 3/22/2016.
 */
public class CardEditUnreportedEventActivity extends Activity{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String[] mDataset ;
    private static String LOG_TAG = "CardEditUnrepEventActivity";
    private int position_video_to_edit;
    final ParseApplication globalVariable= CardViewUnreportedActivity.globalVariable;
    private Tracker mTracker;

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
        setTitle("       Edit & Report");


        position_video_to_edit=getIntent().getIntExtra("position_video_to_edit",1);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        try {
            mAdapter = new MyRecyclerViewEditAdapter(this,getDataSet(position_video_to_edit),mDatasetTypes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecyclerView.setAdapter(mAdapter);
        mTracker = globalVariable.getDefaultTracker();

    }
    @Override
    protected void onStart(){
        super.onStart();
        Log.d("client id",mTracker.get("&cid"));
        if(!ADrivingActivity.netConnect(getApplicationContext()))
        Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.no_internet_warning),
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (globalVariable.getUserName().equals("null")) {
            finish();
        }
        ((MyRecyclerViewEditAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewEditAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
            }
        });
        Log.i("activity", "Setting screen name: " + "CardEditUnreportedEventActivity");
        mTracker.setScreenName("SCREEN: Edit Unreported Event");
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
        event.setLocationFile(fileLocationVideoInit);
        //read location from file
        String location=readLocationFromFile(fileLocationVideoInit,getApplicationContext());
        event.setLocationEvent(location);
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
            else
                if(ADrivingActivity.netConnect(context)&& location_to_return!=null){
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
