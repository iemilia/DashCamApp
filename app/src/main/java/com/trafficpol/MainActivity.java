package com.trafficpol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;

public class MainActivity extends Activity {
	private Tracker mTracker;

	public void onCreate(Bundle savedInstanceState) {
        Log.d("passsing", "2-ssssssssssssssssssecond");
		super.onCreate(savedInstanceState);
        //getActionBar().hide();
        final ParseApplication globalVariable = (ParseApplication) getApplicationContext();
		// Determine whether the current user is an anonymous user
		if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
			// If user is anonymous, send the user to LoginSignupActivity.class
			Intent intent = new Intent(MainActivity.this,
					LoginSignupActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		} else {
			// If current user is NOT anonymous user
			// Get current user data from Parse.com
			ParseUser currentUser = ParseUser.getCurrentUser();
			if (currentUser != null) {
				// Send logged in users or boar (depends on if he knows or not Korean)
                String id= currentUser.getObjectId();
				String name=currentUser.getUsername();
				globalVariable.setUserName(name);
                globalVariable.setUserId(id);
                Log.d("GLOBALVAR", globalVariable.getUserId().toString());
				mTracker = globalVariable.getDefaultTracker();
				mTracker.setClientId(id);

				// This hit will be sent with the User ID value and be visible in
				// User-ID-enabled views (profiles).
				mTracker.send(new HitBuilders.EventBuilder()
						.setCategory("UX")
						.setAction("User Sign In")
						.build());
				Intent intent = new Intent(MainActivity.this, CardViewUnreportedActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("objectId", id);
				startActivity(intent);
				overridePendingTransition(0, 0);
				finish();
			} else {
				// Send user to LoginSignupActivity.class
				Intent intent = new Intent(MainActivity.this,
						LoginSignupActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		}

	}
}
