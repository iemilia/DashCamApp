package com.trafficpol;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

import parseObjects.EditedEvent;
import parseObjects.Event;


public class ParseApplication extends Application {
    public String UserId;
    public String UserName;
    private Tracker mTracker;

    public String getUserId() {

        return UserId;
    }

    public void setUserId(String aUserId) {

        UserId = aUserId;

    }

    public String getUserName() {

        return UserName;
    }

    public void setUserName(String aUserName) {

        UserName = aUserName;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        //ParseObject.registerSubclass(Movie.class);

        // Add your initialization code here:TraficPilotTest

        //TraficPolExperiment
        Parse.initialize(this, "code1", "code2");

        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(EditedEvent.class);

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
 
        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);
 
        ParseACL.setDefaultACL(defaultACL, true);        Log.d("passsing", "1-ssssssssssssssssssecond");

    }
    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            GoogleAnalytics.getInstance(this).setLocalDispatchPeriod(0);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);

        }
        return mTracker;
    }
}