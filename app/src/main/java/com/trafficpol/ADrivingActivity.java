package com.trafficpol;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.view.View;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.IOException;

public class ADrivingActivity extends Activity {
	public static ParseApplication globalVariable;
	private ADrivingCamera mCamera;
	private FrameLayout mLayout;
	private String mGlobalUser;
	private String mGlobalUserId;
	private boolean audioType;
	private boolean screenLight;
	private boolean firstTime;

	private Tracker mTracker;


	public static int state=2; // state=0(continuous rec); state=1(event rec);state=2(innactive)

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.globalVariable = CardViewUnreportedActivity.globalVariable;
		super.onCreate(savedInstanceState);
		//getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.main);
		firstTime=true;
		final TextView recText=(TextView) findViewById(R.id.recText);

		// Obtain the shared Tracker instance.
		mTracker = globalVariable.getDefaultTracker();

		// Create the camera view and begin recording.
		mCamera = new ADrivingCamera(this, 0);
		mLayout = (FrameLayout) findViewById(R.id.camera_preview);
		mLayout.addView(mCamera);

		mLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(state==0){
					TextView additional_feedback= (TextView) findViewById(R.id.additional_feedback);
					additional_feedback.setVisibility(View.INVISIBLE);
					mTracker.send(new HitBuilders.ScreenViewBuilder().build());

					mTracker.send(new HitBuilders.EventBuilder()
							.setCategory("Event Capture")
							.setAction("Tap Screen")
							.build());
					//mCamera.stop();
					Toast.makeText(ADrivingActivity.this,
							getResources().getString(R.string.event_captured),
							Toast.LENGTH_SHORT
					).show();
					try {
						recordEvent();

					} catch (IOException e) {
						e.printStackTrace();
					}
					//mCamera.start();
				}
				else {
					if (state == 1) {
						// Build and send an Event.
						TextView additional_feedback = (TextView) findViewById(R.id.additional_feedback);
						additional_feedback.setText("Wait for the event to be captured.");
						additional_feedback.setVisibility(View.VISIBLE);

					}
					else
					if (state == 2) {
						TextView additional_feedback = (TextView) findViewById(R.id.additional_feedback);
						additional_feedback.setVisibility(View.INVISIBLE);
						//do nothing till the camera gets initialized and working again
					}
				}
			}
		});

		// Bind actions to the buttons.
		ImageButton flipBtn = (ImageButton) findViewById(R.id.flip_button);
		flipBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(state==0)
				try {
					mCamera.flip();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		//set rec feedback
		//ImageView rec_image=(ImageView) findViewById(R.id.recAnim);
		//LinearLayout recFeedback = (LinearLayout) findViewById(R.id.recFeedback);
		ImageView recAnim=(ImageView) findViewById(R.id.recAnim);
		recText.setText("Continuous Recording");
		final Animation animation = new AlphaAnimation((float) 0.5, 0); // Change alpha from fully visible to invisible
		animation.setDuration(500); // duration - half a second
		animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
		animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
		recAnim.startAnimation(animation);

		//final ImageButton audioBtn = (ImageButton) findViewById(R.id.audio_type);
		//get and use the settings
		final SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		this.audioType = sharedPrefs.getBoolean("audioType", true);

		this.screenLight = sharedPrefs.getBoolean("dimDisplay", false);

		/*
		if (audioType == true)
			audioBtn.setImageResource(R.drawable.high_volume);
		else
			audioBtn.setImageResource(R.drawable.mute);
		audioBtn.setOnClickListener(new View.OnClickListener() {
			SharedPreferences.Editor editor = sharedPrefs.edit();
			public void onClick(View v) {
				if (audioType == true) {
					audioBtn.setImageResource(R.drawable.mute);
					audioType = false;
					//editor.putBoolean("audioType",audioType);
				} else{
					audioBtn.setImageResource(R.drawable.high_volume);
					audioType = true;
				}
				editor.putBoolean("audioType",audioType);
				editor.commit();


			}
		});
*/
		//dim the display
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		if (screenLight == false)
			lp.screenBrightness = 0.1f;
		else
			lp.screenBrightness = 0.0f;

		this.getWindow().setAttributes(lp);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainactionbar, menu);
		return true;
	}

	public static ParseApplication getGlobalVar() {
		return globalVariable;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(state==1)
			Toast.makeText(getApplication(),"Please be patient till the event capturing is completex",Toast.LENGTH_SHORT).show();
		if(state==0)
		switch (item.getItemId()) {
			case R.id.events:
				Toast.makeText(this, "Item 1:Events selected", Toast.LENGTH_SHORT).show();
				try {
					mCamera.stop();
					this.onStop();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent(getApplicationContext(), CardViewUnreportedActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(0, 0);
				startActivity(intent);
				finish();
				break;
			case R.id.reported:
				Toast.makeText(this, "Item 2: Reported selected", Toast.LENGTH_SHORT).show();
				try {
					mCamera.stop();
				} catch (IOException e) {
					e.printStackTrace();
				}
				intent = new Intent(getApplicationContext(), CardViewReportedActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(0, 0);
				startActivity(intent);
				finish();
				break;
			case R.id.all_videos:
				Toast.makeText(this, "Item 3: All Videos selected", Toast.LENGTH_SHORT).show();
				try {
					mCamera.stop();
				} catch (IOException e) {
					e.printStackTrace();
				}
				intent = new Intent(getApplicationContext(), CardViewAllVideos.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(0, 0);
				startActivity(intent);
				finish();
				break;
			case R.id.settings:
				Toast.makeText(this, "Item 4: Settings selected", Toast.LENGTH_SHORT).show();
				try {
					mCamera.stop();
				} catch (IOException e) {
					e.printStackTrace();
				}
				intent = new Intent(getApplicationContext(), UserSettingActivity.class);
				startActivity(intent);
				finish();
				break;

		}
		return true;
	}

	public void recordEvent() throws IOException {
		final TextView label_rec=(TextView) findViewById(R.id.recText);
		//label_rec.setText("EVENT RECORDING");
		state=2;
		mCamera.recordStreetEvent();
	}

	@Override
	public void onStart() {

		super.onStart();

	}


	@Override
	public void onStop() {
		super.onStop();
		if(state==1)
			ADrivingCamera.ctd.cancel();
		state=2;

	}
	public static boolean netConnect(Context ctx)
	{
		ConnectivityManager cm;
		NetworkInfo info = null;
		try
		{
			cm = (ConnectivityManager)
					ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
			info = cm.getActiveNetworkInfo();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (info != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("activity", "Setting screen name: " + "ADrivignActivity");
		mTracker.setScreenName("SCREEN: ADrivingActivity");
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		if(!firstTime)
			if (globalVariable.getUserName().equals("null")) {
				finish();
				} else
		firstTime=false;


	}


}
