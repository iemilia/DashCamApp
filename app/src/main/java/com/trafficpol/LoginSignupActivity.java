package com.trafficpol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginSignupActivity extends Activity {
    public static String ObjectId;
    // Declare Variables
	Button loginbutton;
    Button signup;
	String usernametxt;
	String passwordtxt;
	EditText password;
	EditText username;
	private Tracker mTracker;


	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// Get the view from main.xml
		//getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

		setContentView(R.layout.loginsignup);

		final ParseApplication globalVariable = (ParseApplication) getApplicationContext();
		// Locate EditTexts in main.xml
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);


		// Locate Buttons in main.xml
		loginbutton = (Button) findViewById(R.id.login);
		signup = (Button) findViewById(R.id.signup);

		// Login Button Click Listener
		loginbutton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// Retrieve the text entered from the EditText
				usernametxt = username.getText().toString();
				passwordtxt = password.getText().toString();


				// Send data to Parse.com for verification
				ParseUser.logInInBackground(usernametxt, passwordtxt,
						new LogInCallback() {
							public void done(ParseUser user, ParseException e) {
								if (user != null) {
									// If user exist and authenticated, send user to Welcome.class
									String id=user.getObjectId();
                                    globalVariable.setUserId(id);
									String name=user.getUsername();
									globalVariable.setUserName(name);
                                    ObjectId=user.getObjectId();
									mTracker = globalVariable.getDefaultTracker();
									mTracker.setClientId(id);

                                    // Creating Bundle object

                                    Intent intent = new Intent(
											LoginSignupActivity.this,
											CardViewUnreportedActivity.class);
                                   // intent.putExtra("objectId",id);
									startActivity(intent);

									Toast.makeText(getApplicationContext(),
											"Successfully Logged in",
											Toast.LENGTH_LONG).show();
									finish();
								} else {
									Toast.makeText(
											getApplicationContext(),
											"No such user exist, please signup",
											Toast.LENGTH_LONG).show();
								}
							}
						});
			}
		});
		// Sign up Button Click Listener
		signup.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// Retrieve the text entered from the EditText
				usernametxt = username.getText().toString();
				passwordtxt = password.getText().toString();

                Intent intent= new Intent(getApplicationContext(),SignUp.class);
                intent.putExtra("usernametxt",usernametxt);
                intent.putExtra("passwordtxt",passwordtxt);
                startActivity(intent);

				}
		});
	}


}
