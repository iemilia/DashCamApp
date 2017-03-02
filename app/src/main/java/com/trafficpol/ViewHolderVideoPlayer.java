package com.trafficpol;


import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.FrameLayout;

import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;
import java.util.ArrayList;


/**
 * Created by Emilia on 3/22/2016.
 */
public class ViewHolderVideoPlayer extends MyRecyclerViewVideosAdapter
        .VideoObjectHolder{
    VideoView videoPlayer;
    MediaController videoController ;
    Context mContext;
    boolean audio;
    EventObject eventObject;
    VideoObject videoObject;
    MediaPlayer mediaPlayer;

    boolean audioType=true;
    int retrievedAudio=0;
    int nrCuts;
    long cutOne,cutTwo;
    ArrayList<Integer> cutsVideo=new ArrayList<Integer>();

    public ViewHolderVideoPlayer(View itemView,Context context) {
        super(itemView);
        videoPlayer=(VideoView)itemView.findViewById(R.id.videoView);
        this.audio=true;
        mContext=context;
    }
    public ViewHolderVideoPlayer(View itemView,Context context,boolean audio,int nrCuts) {
        super(itemView);
        videoPlayer=(VideoView)itemView.findViewById(R.id.videoView);
        this.audio=audio;
        this.nrCuts=nrCuts;
        //videoController=(MediaController) itemView.findViewById(R.id.mediaController);
        mContext=context;
    }
    public ViewHolderVideoPlayer(View itemView,Context context,boolean audio,int nrCuts,long cutOne,long cutTwo) {
        super(itemView);
        videoPlayer=(VideoView)itemView.findViewById(R.id.videoView);
        this.audio=audio;
        this.nrCuts=nrCuts;
        this.cutOne=cutOne;
        this.cutTwo=cutTwo;
        //videoController=(MediaController) itemView.findViewById(R.id.mediaController);
        mContext=context;
    }



    public void bind(EventObject eventObject) {
        videoController = new CustomMediaController(mContext,this,audio,nrCuts,cutOne,cutTwo);
        // set correct height
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoPlayer.getLayoutParams();
        params.height =  videoController.getHeight();
        videoPlayer.setLayoutParams(params);

        videoPlayer.setVideoPath(eventObject.getSourceFile().toString());
        videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer pMp) {
                //use a global variable to get the object
                mediaPlayer = pMp;
                //mediaPlayer.start();

            }
        });
        videoController.show(0);
        this.eventObject=eventObject;
        videoPlayer.setMediaController(videoController);
        //pBar.setVisibility(View.GONE);

        FrameLayout f = (FrameLayout) videoController.getParent();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_BOTTOM, videoPlayer.getId());

        ((LinearLayout) f.getParent()).removeView(f);
        ((RelativeLayout) videoPlayer.getParent()).addView(f, lp);

        videoController.setAnchorView(videoPlayer);

    }


    public void bind_seeOnly(EventObject eventObject) {
        videoController = new CustomMediaControllerSeeOnly(mContext,this,audio);
        // set correct height
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoPlayer.getLayoutParams();
        params.height =  videoController.getHeight();
        videoPlayer.setLayoutParams(params);
        videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer pMp) {
                //use a global variable to get the object
                mediaPlayer = pMp;
                // mediaPlayer.start();

            }
        });

        videoPlayer.setVideoPath(eventObject.getSourceFile().toString());
        videoController.setMediaPlayer(videoPlayer);

        //videoController.show(0);
        this.eventObject = eventObject;
        videoPlayer.setMediaController(videoController);

        FrameLayout f = (FrameLayout) videoController.getParent();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_BOTTOM, videoPlayer.getId());

        ((LinearLayout) f.getParent()).removeView(f);
        ((RelativeLayout) videoPlayer.getParent()).addView(f, lp);

        videoController.setAnchorView(videoPlayer);

    }

    public void bind_video(VideoObject videoObject) {
        videoController = new CustomMediaController(mContext,this,audio,nrCuts,cutOne,cutTwo);

        // set correct height
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoPlayer.getLayoutParams();
        params.height =  videoController.getHeight();
        videoPlayer.setLayoutParams(params);
        videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer pMp) {
                //use a global variable to get the object
                mediaPlayer = pMp;
                //mediaPlayer.start();

            }
        });
        videoPlayer.setVideoPath(videoObject.getSourceFile().toString());
        videoController.setMediaPlayer(videoPlayer);
        videoController.show(0);this.videoObject=videoObject;
        this.retrievedAudio=MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO;
        if(retrievedAudio!=2)
            this.audio=false;
        videoPlayer.setMediaController(videoController);
        //pBar.setVisibility(View.GONE);

        FrameLayout f = (FrameLayout) videoController.getParent();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_BOTTOM, videoPlayer.getId());

        ((LinearLayout) f.getParent()).removeView(f);
        ((RelativeLayout) videoPlayer.getParent()).addView(f, lp);

        videoController.setAnchorView(videoPlayer);

    }

    public void cutAudio(){
        this.audioType=false;
        mediaPlayer.setVolume(0, 0);
    }

    public void backAudio(){
        this.audioType=true;
        mediaPlayer.setVolume(1, 1);
        //createAudioVideo(videoObject.getSourceFile(), audioType);
    }

    public int getPositionVideo(){
        //audioType=true;
        return mediaPlayer.getCurrentPosition();
    }

    public void setCutsVideo(ArrayList<Integer> cuts){
        //audioType=true;
        cutsVideo=cuts;
    }
    public ArrayList<Integer> getCutsVideo(){
        //audioType=true;
        return cutsVideo;
    }



}
