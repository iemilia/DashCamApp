package com.trafficpol;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Emilia on 3/27/2016.
 * This class implements a custom media controller that allows extra editting on the videos, before
 * they can be reported as events: cut audio, cut form video
 */
public  class CustomMediaController extends MediaController {
    private ViewHolderVideoPlayer player;
    private static SeekBar seekBar;

    private boolean audioType=true;
    private long cutOneValue,cutTwoValue;
    private int nrCuts;
    int pressedOne=0,pressedTwo=0; //check if cuts where ever pressed

    private ArrayList<Integer> cutPositions = new ArrayList<Integer>();


    public CustomMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomMediaController(Context context) {
        super(context);
    }

    public CustomMediaController(Context context, ViewHolderVideoPlayer player,boolean audio, int nrCuts ) {
        super(context);
        this.player = player;
        this.audioType=audio;
        this.nrCuts=nrCuts;
    }


    public CustomMediaController(Context context, ViewHolderVideoPlayer player,boolean audio, int nrCuts,long cutOne, long cutTwo ) {
        super(context);
        this.player = player;
        this.audioType=audio;
        this.nrCuts=nrCuts;
        this.cutOneValue=cutOne;
        this.cutTwoValue=cutTwo;
    }
    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        final ImageView audioButton = new ImageView(getContext());
        final ImageView scissorsImage = new ImageView(getContext());

        final TextView cutOne = new TextView(getContext());
        final TextView cutTwo = new TextView(getContext());

        cutOne.setText(milliSecondsToTimer(cutOneValue));
        cutTwo.setText(milliSecondsToTimer(cutTwoValue));

        final TextView cutButtonOne = new TextView(getContext());
        final TextView cutButtonTwo = new TextView(getContext());
        final Button cancelOne=new Button(getContext());

        final TextView no_cuts_label=new TextView(getContext());
        scissorsImage.setImageResource(R.drawable.scissorsb);
        //final SeekBar
        if(audioType==true)
            audioButton.setImageResource(R.drawable.high_volume);
        else
            audioButton.setImageResource(R.drawable.mute);

        audioButton.setMaxWidth(10);

        cutButtonOne.setText("From");
        cutButtonOne.setTextColor(0xffffff00);
        cutButtonTwo.setText("To");
        cutButtonTwo.setTextColor(0xffffff00);

        cancelOne.setText("cancel");
        cancelOne.setBackgroundColor(Color.TRANSPARENT);
        cancelOne.setTextColor(0xffffff00);

        //no_cuts_label.setText(String.valueOf(nrCuts));
        //no_cuts_label.setTextColor(0xffcccccc);

        cutOne.setTextColor(0xffcccccc);
        cutTwo.setTextColor(0xffcccccc);

        //left,top,right,bottom

        audioButton.setPadding(20, 20, 20, 20);
        cutButtonOne.setPadding(10, 10, 10, 10);
        cutButtonTwo.setPadding(10, 10, 10, 10);
        //no_cuts_label.setPadding(0, 0, 0, 0);

        LayoutParams params_audio = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params_audio.gravity = Gravity.LEFT;
        params_audio.leftMargin=1630;
        params_audio.topMargin=60;

        LayoutParams params_scissor_iamge = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params_scissor_iamge.gravity = Gravity.LEFT;
        params_scissor_iamge.leftMargin=20;
        params_scissor_iamge.topMargin=30;

        LayoutParams params_scissor_one = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params_scissor_one.gravity = Gravity.LEFT;
        params_scissor_one.leftMargin=120;//35
        params_scissor_one.topMargin=20;

        LayoutParams params_cutOne = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params_cutOne.gravity = Gravity.LEFT;
        params_cutOne.leftMargin=310;
        params_cutOne.topMargin=35;

        LayoutParams params_cancelOne = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params_cancelOne.gravity = Gravity.LEFT;
        params_cancelOne.leftMargin=480;
        params_cancelOne.topMargin=35;



        LayoutParams params_scissor_two = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params_scissor_two.gravity = Gravity.LEFT;
        params_scissor_two.leftMargin=120;
        params_scissor_two.topMargin=140;

        LayoutParams params_cutTwo = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params_cutTwo.gravity = Gravity.LEFT;
        params_cutTwo.leftMargin=310;
        params_cutTwo.topMargin=145;

        addView(scissorsImage, params_scissor_iamge);
        addView(audioButton, params_audio);
        addView(cutButtonOne,params_scissor_one);
        addView(cutButtonTwo,params_scissor_two);
        addView(cancelOne,params_cancelOne);
        addView(cutOne,params_cutOne);
        addView(cutTwo, params_cutTwo);

        audioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioType == true) {
                    player.cutAudio();
                    audioType = false;
                    audioButton.setImageResource(R.drawable.mute);
                } else {
                    player.backAudio();
                    audioType = true;
                    audioButton.setImageResource(R.drawable.high_volume);

                }
            }
        });

        cutButtonOne.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (nrCuts == 0) {
                        cutPositions.add(0, player.getPositionVideo());
                        nrCuts++;
                        cutOneValue=player.getPositionVideo();
                        cutOne.setText(milliSecondsToTimer(player.getPositionVideo()));
                    } else if (nrCuts == 1&&pressedOne==1) {
                        if (!milliSecondsToTimer(player.getPositionVideo()).equals(milliSecondsToTimer(cutPositions.get(0)))) {
                            cutPositions.remove(0);
                            cutPositions.add(0, player.getPositionVideo());
                            cutOneValue=player.getPositionVideo();
                            cutOne.setText(milliSecondsToTimer(player.getPositionVideo()));
                        }
                    }else if (nrCuts == 1&&pressedOne==0) {
                        if (!milliSecondsToTimer(player.getPositionVideo()).equals(milliSecondsToTimer(cutPositions.get(0)))) {
                            cutPositions.add(1, player.getPositionVideo());
                            cutOneValue = player.getPositionVideo();
                            nrCuts++;
                            cutOne.setText(milliSecondsToTimer(player.getPositionVideo()));
                        }
                    }
                    else if (nrCuts == 2) {
                        if (milliSecondsToTimer(player.getPositionVideo()).equals(milliSecondsToTimer(cutPositions.get(1)))||
                                milliSecondsToTimer(player.getPositionVideo()).equals(milliSecondsToTimer(cutPositions.get(0)))) {

                        } else {
                               if(milliSecondsToTimer(cutPositions.get(0)).equals(cutOne.getText().toString())){

                                cutPositions.remove(0);
                                cutPositions.add(0, player.getPositionVideo());
                                cutOneValue=cutPositions.get(0);
                                cutTwoValue=cutPositions.get(1);
                            }
                            else if(milliSecondsToTimer(cutPositions.get(1)).equals(cutOne.getText().toString())){

                                cutPositions.remove(1);
                                cutPositions.add(1, player.getPositionVideo());
                                cutOneValue=cutPositions.get(1);
                                cutTwoValue=cutPositions.get(0);
                            }

                            cutOne.setText(milliSecondsToTimer(player.getPositionVideo()));

                            no_cuts_label.setText(String.valueOf(nrCuts));
                        }

                    }
                    pressedOne=1;
                }
            });

        cutButtonTwo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nrCuts == 0) {
                    cutPositions.add(0, player.getPositionVideo());
                    nrCuts++;
                    cutTwoValue = player.getPositionVideo();
                    cutTwo.setText(milliSecondsToTimer(player.getPositionVideo()));

                } else if (nrCuts == 1 && pressedTwo == 1) {
                    if (!milliSecondsToTimer(player.getPositionVideo()).equals(milliSecondsToTimer(cutPositions.get(0)))) {
                        cutPositions.remove(0);
                        cutPositions.add(0, player.getPositionVideo());
                        cutTwoValue = player.getPositionVideo();
                        cutTwo.setText(milliSecondsToTimer(player.getPositionVideo()));
                    }
                } else if (nrCuts == 1 && pressedTwo == 0) {
                    if (!milliSecondsToTimer(player.getPositionVideo()).equals(milliSecondsToTimer(cutPositions.get(0)))) {
                        cutPositions.add(1, player.getPositionVideo());
                        nrCuts++;
                        cutTwoValue = player.getPositionVideo();
                        cutTwo.setText(milliSecondsToTimer(player.getPositionVideo()));
                    }
                } else if (nrCuts == 2) {

                    if (milliSecondsToTimer(player.getPositionVideo()).equals(milliSecondsToTimer(cutPositions.get(0))) ||
                            milliSecondsToTimer(player.getPositionVideo()).equals(milliSecondsToTimer(cutPositions.get(1)))) {

                    } else {
                        if (milliSecondsToTimer(cutPositions.get(0)).equals(cutTwo.getText().toString())) {
                            cutPositions.remove(0);
                            cutPositions.add(0, player.getPositionVideo());
                            cutOneValue = cutPositions.get(0);
                            cutTwoValue = cutPositions.get(1);
                        } else if (milliSecondsToTimer(cutPositions.get(1)).equals(cutTwo.getText().toString())) {
                            cutPositions.remove(1);
                            cutPositions.add(1, player.getPositionVideo());
                            cutOneValue = cutPositions.get(1);
                            cutTwoValue = cutPositions.get(0);
                        }
                        cutTwo.setText(milliSecondsToTimer(player.getPositionVideo()));
                    }

                }
                pressedTwo = 1;

            }
        });
        player.setCutsVideo(cutPositions);
        //for (int i=0;i<cutPositions.size();i++)

        cancelOne.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nrCuts=0;
                pressedTwo=0;
                pressedOne=0;
                cutPositions.clear();
                cutOne.setText("0:00");
                cutTwo.setText("0:00");
                cutOneValue = 0;
                cutTwoValue = 0;
            }
        });

    }

                //http://stackoverflow.com/questions/16130324/how-to-display-time-of-videoview-in-android
        public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
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