package com.trafficpol;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Emilia on 3/22/2016.
 */
public class CardEditVideoActivity extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String[] mDataset;
    private static String LOG_TAG = "Card Edit VideoActivity";
    private int position_video_to_edit;
    final ParseApplication globalVariable= CardViewUnreportedActivity.globalVariable;

    private int mDatasetTypes[] = {1, 2, 3}; //view types
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerviewedit);
        position_video_to_edit = getIntent().getIntExtra("position_video_to_edit", 1);
        mTracker = globalVariable.getDefaultTracker();

        ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        setTitle("       Edit & Report");
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mTracker = globalVariable.getDefaultTracker();

        // Code to Add an item with default animation
        //((MyRecyclerViewEventsAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MyRecyclerViewEventsAdapter) mAdapter).deleteItem(index);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(!ADrivingActivity.netConnect(getApplicationContext()))
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.no_internet_warning),
                    Toast.LENGTH_LONG
            ).show();
        GoogleAnalytics.getInstance(CardEditVideoActivity.this).reportActivityStart(this);
       Log.d("globalvar",globalVariable.getUserName());
        try {
            mAdapter = new MyRecyclerViewVideoEditAdapter(this, getDataSet(position_video_to_edit), mDatasetTypes);

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
        ((MyRecyclerViewVideoEditAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewVideoEditAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
            }
        });
        Log.i("activity", "Setting screen name: " + "CardEditVideoActivity");
        mTracker.setScreenName("SCREEN: Edit Video");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }
    @Override
    public void onStop() {
        super.onStop();

        GoogleAnalytics.getInstance(CardEditVideoActivity.this).reportActivityStop(this);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactionbar, menu);
        return true;
    }

    private VideoObject getDataSet(int position) throws IOException {
        File fileVideo = ADrivingCamera.getFiles(false)[position];
        VideoObject video = new VideoObject(fileVideo);

        File filePossibleLocationOne = ManageFiles.getOutputDir("locationEvent", fileVideo.getName().split(".mp4")[0]);
        File filePossibleLocationTwo = ManageFiles.getOutputDir("locationEvent", fileVideo.getName().split(".mp4")[0]+"-saved");
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
        video.setLocationFile(fileLocationVideoInit);
        //retreive data about the file
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        FileInputStream fis = new FileInputStream(fileVideo.toString());
        mmr.setDataSource(fis.getFD()); //gets the underlying file descriptor
        video.setTimeVideo(fileVideo.getName().split("_")[1].split(".mp4")[0] + ", " + fileVideo.getName().split("_")[0]);
        video.setDayVideo(fileVideo.getName().split("_")[0].substring(6, 8));
        video.setMonthVideo(DisplayNicely.displayMonth(fileVideo.getName().split("_")[0].substring(4, 6)));
        video.setHourVideo(fileVideo.getName().split("_")[1].split(".mp4")[0].substring(0, 2)
                + ":" + fileVideo.getName().split("_")[1].split(".mp4")[0].substring(2, 4)
                + ":" + fileVideo.getName().split("_")[1].split(".mp4")[0].substring(4, 6));
        //create & set thumbnail of event
        Bitmap thumb = createThumbnail(fileVideo);
        video.setThumbnail(thumb);

        video.setLocationVideo(mmr.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_LOCATION
        ));
//de lucrat location din MyRecycleViewVideosAdapter

        return video;
    }

    public Bitmap createThumbnail(File filePath) {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePath.toString(), MediaStore.Video.Thumbnails.MINI_KIND);
        return thumb;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.title:
                Toast.makeText(this, "Driving mode", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ADrivingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case android.R.id.home:
                Toast.makeText(this, "Driving mode", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), ADrivingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.events:
                Toast.makeText(this, "Menu item 1 selected", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), CardViewUnreportedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.reported:
                Toast.makeText(this, "Menu item 2 selected", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), CardViewReportedActivity.class);
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
                intent = new Intent(getApplicationContext(), UserSettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
}