package com.trafficpol;

/**
 * Created by Emilia on 3/16/2016.
 */
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Geocoder;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class CardViewAllVideos extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewAllVideos";
    final ParseApplication globalVariable= CardViewUnreportedActivity.globalVariable;
    private Tracker mTracker;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);

        ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        setTitle("       Street raw videos");
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mTracker = globalVariable.getDefaultTracker();

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
            mAdapter = new MyRecyclerViewVideosAdapter(this,getDataSet());
            Log.d("TEST 4: ", "test");
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
        ((MyRecyclerViewVideosAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewVideosAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
            }
        });
        Log.i("activity", "Setting screen name: " + "CardViewAllVideos");
        mTracker.setScreenName("SCREEN: View Raw Videos");
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
                Toast.makeText(this, "Item 1:Events selected", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), CardViewUnreportedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.reported:
                Toast.makeText(this, "Item 2: Reported selected", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), CardViewReportedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.all_videos:
                Toast.makeText(this, "You are here: All Videos", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), CardViewAllVideos.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.settings:
                Toast.makeText(this, "Item 4: Settings selected", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), UserSettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return true;
    }

    private ArrayList<VideoObject> getDataSet() throws IOException {
        ArrayList videos = new ArrayList<VideoObject>();
        File[] filesVideo = ADrivingCamera.getFiles(false);

        for (int i = 0; i < filesVideo.length; i++) {
            VideoObject video = new VideoObject(filesVideo[i]);

            //create and set date of event
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            FileInputStream fis = new FileInputStream(filesVideo[i].toString());
            mmr.setDataSource(fis.getFD()); //gets the underlying file descriptor
            // video.setTimeVideo(filesVideo[i].getName().split("_")[1].split(".mp4")[0] + ", " + filesVideo[i].getName().split("_")[0]);
            //create & set thumbnail of event
            Bitmap thumb = createThumbnail(filesVideo[i]);
            video.setThumbnail(thumb);
            video.setSourceFile(filesVideo[i]);
            video.setLocationVideo(mmr.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_LOCATION
            ));
            video.setTimeVideo(filesVideo[i].getName().split("_")[1].split(".mp4")[0].substring(0, 2)
                    + ":" + filesVideo[i].getName().split("_")[1].split(".mp4")[0].substring(2, 4)
                    + ":" + filesVideo[i].getName().split("_")[1].split(".mp4")[0].substring(4, 6));
            video.setDayVideo(filesVideo[i].getName().split("_")[0].substring(6, 8));
            video.setMonthVideo(filesVideo[i].getName().split("_")[0].substring(4, 6));
            video.setYearVideo(filesVideo[i].getName().split("_")[0].substring(0, 4));
            video.setMonthName(DisplayNicely.displayMonth(filesVideo[i].getName().split("_")[0].substring(4, 6)));
            //set Location
            File filePossibleLocationOne = ManageFiles.getOutputDir("locationEvent", filesVideo[i].getName().split(".mp4")[0]);
            File filePossibleLocationTwo = ManageFiles.getOutputDir("locationEvent", filesVideo[i].getName().split(".mp4")[0]+"-saved");
            File fileLocationVideoInit;

            if(filePossibleLocationOne.exists()){
                fileLocationVideoInit=filePossibleLocationOne;
            }
            else
                if(filePossibleLocationTwo.exists())
                    fileLocationVideoInit=filePossibleLocationTwo;
                else{
                       String coordinates=mmr.extractMetadata(
                               MediaMetadataRetriever.METADATA_KEY_LOCATION
                       );
                        Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                        //split coordinate
                        // Get the position of the -/+ sign in location String, which indicates
                        // the beginning of the longtitude.
                        if(coordinates!=null){
                            int index = coordinates.lastIndexOf('-');
                            if (index == -1) {
                                index = coordinates.lastIndexOf('+');
                            }
                            double latitude = Double.parseDouble(coordinates.substring(0, index - 1));
                            double longitude = Double.parseDouble(coordinates.substring(index));
                            video.setLocationFromFileVideo(GPSTracker.getLocationName(geo, latitude, longitude,getApplicationContext()));
                            }
                    if(ManageFiles.getFiles(false,ManageFiles.getOutputDir("default")).length!=0)
                        fileLocationVideoInit=ManageFiles.getFiles(false,ManageFiles.getOutputDir("default"))[0];
                    else{
                        fileLocationVideoInit=new File(ManageFiles.getOutputDir("default"),"default.txt");
                        BufferedWriter writer = new BufferedWriter(new FileWriter(fileLocationVideoInit));
                        writer.write("no location available ");
                        writer.close();
                    }
                    }
            // ManageFiles.getFiles(false, ManageFiles.getOutputDir("default"))[0];
            //search the name among location files
         /*   for (int j = 0; j < ManageFiles.getFiles(false, ManageFiles.getOutputDir("locationEvent")).length; j++) {
                if (ManageFiles.getFiles(false, ManageFiles.getOutputDir("locationEvent"))[j].getName().split(".txt")[0].equals(filesVideo[i].getName().split(".mp4")[0])) {
                    fileLocationVideoInit = ManageFiles.getFiles(false, ManageFiles.getOutputDir("locationEvent"))[j];
                    Log.d("Event name FINDING: ", filesVideo[i].getName().split(".mp4")[0]);
                    Log.d("Location name FINDING: ", ManageFiles.getFiles(false, ManageFiles.getOutputDir("locationEvent"))[j].getName().split(".txt")[0]);

                    break;
                }
            }
*/
            //File fileLocationVideoInit=ManageFiles.getFiles(false,ManageFiles.getOutputDir("locationVideo"))[1];
            video.setLocationFile(fileLocationVideoInit);
            if(video.getLocationFromFileVideo()==null)
                video.setLocationFromFileVideo(ManageFiles.readLocationFromFile(fileLocationVideoInit,getApplicationContext()));
            if(video.getLocationFromFileVideo().equals("no location found saved")||
                    video.getLocationFromFileVideo().equals("no location available ")){
                String coordinates=mmr.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_LOCATION
                );
                Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                //split coordinate
                // Get the position of the -/+ sign in location String, which indicates
                // the beginning of the longtitude.
                if(coordinates!=null){
                    int index = coordinates.lastIndexOf('-');
                    if (index == -1) {
                        index = coordinates.lastIndexOf('+');
                    }
                    double latitude = Double.parseDouble(coordinates.substring(0, index - 1));
                    double longitude = Double.parseDouble(coordinates.substring(index));
                    video.setLocationFromFileVideo(GPSTracker.getLocationName(geo, latitude, longitude,getApplicationContext()));
                }
            }

            videos.add(i, video);
        }
        return videos;
    }

    public Bitmap createThumbnail(File filePath) {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePath.toString(), MediaStore.Video.Thumbnails.MINI_KIND);
        return thumb;
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}