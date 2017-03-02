package com.trafficpol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


/**
 * Created by Emi on 05.05.2015.
 */

public class SignUp extends Activity {
    EditText password;
    EditText email; //this is the email
    EditText username;
    Button signup;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from main.xml
       //getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        setContentView(R.layout.signup);
        String usernametxt=getIntent().getStringExtra("usernametxt");
        String passwordtxt=getIntent().getStringExtra("passwordtxt");
        String emailtxt=getIntent().getStringExtra("emailtxt");


        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        email=(EditText) findViewById(R.id.email);

        username.setText(usernametxt);
        password.setText(passwordtxt);
        email.setText(emailtxt);

        signup = (Button) findViewById(R.id.signup);
        // Sign up Button Click Listener
        signup.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                final String passwordtxtfinal = password.getText().toString();
                final String emailtxtfinal=email.getText().toString();
                final String usernamefinal = username.getText().toString();



                // Force user to fill up the form
                if (usernamefinal.equals("") ||  passwordtxtfinal.equals("") || emailtxtfinal.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please complete the sign up form",
                            Toast.LENGTH_LONG).show();

                } else {
                    // Save new user data into Parse.com Data Storage
                    final ParseUser user = new ParseUser();
                    user.setUsername(usernamefinal);
                    user.setPassword(passwordtxtfinal);
                    user.setEmail(emailtxtfinal);


                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Show a simple Toast message upon successful registration
                                Toast.makeText(getApplicationContext(),
                                        "Successfully Signed up, please log in.",
                                        Toast.LENGTH_LONG).show();

                                Intent intent= new Intent(getApplicationContext(),LoginSignupActivity.class);
                                String id= user.getObjectId();

                                intent.putExtra("objectId",id);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Sign up Error", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
                }


            }
        });


}}
