package com.trafficpol;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Emilia on 3/27/2016.
 */
public  class CustomMediaControllerSeeOnly extends MediaController {
    private ViewHolderVideoPlayer player;
    private static SeekBar seekBar;

    private boolean audioType=true;
    private int nrCuts=0;

    private ArrayList<Integer> cutPositions = new ArrayList<Integer>();
    private int lengthCut;


    public boolean isCutAudio() {
        return audioType;
    }

    public int getNrCuts() {
        return nrCuts;
    }

    public ArrayList<Integer> getCutPositions() {
        return cutPositions;
    }



    public CustomMediaControllerSeeOnly(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomMediaControllerSeeOnly(Context context) {
        super(context);
    }

    public CustomMediaControllerSeeOnly(Context context, ViewHolderVideoPlayer player,boolean audio) {
        super(context);
        this.audioType=audio;
        this.player = player;
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        final ImageView audioButton = new ImageView(getContext());
        final TextView audio_type_label=new TextView(getContext());
        //final SeekBar
        audioButton.setMaxWidth(10);
        if(audioType==true){
            audio_type_label.setText("audio is available");
            audioButton.setImageResource(R.drawable.high_volume);
        }
        else{
            audio_type_label.setText("no audio content");
            audioButton.setImageResource(R.drawable.mute);
        }

        audio_type_label.setTextColor(0xffffff00);

        //left,top,right,bottom

        audioButton.setPadding(20, 20, 20, 20);
        audio_type_label.setPadding(0, 0, 0, 0);

        LayoutParams params_audio = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params_audio.gravity = Gravity.LEFT;
        params_audio.leftMargin=1630;
        params_audio.topMargin=60;


        LayoutParams params_label = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params_label.gravity = Gravity.LEFT;
        params_label.leftMargin=1750;
        params_label.topMargin=80;

        addView(audioButton, params_audio);

        addView(audio_type_label, params_label);

    }
    @Override
    public void hide()
    {
        show();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
        {

            ((Activity) getContext()).finish();
        }
        return true;
    }
}