package com.trafficpol;

/**
 * Created by Emi on 06.05.2015.
 */

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GPSTracker extends Service implements LocationListener {


    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = true;

    // flag for network status
    boolean isNetworkEnabled = true;

    // flag for GPS status
    boolean canGetLocation = false;

    String timestamp;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    ArrayList<Location> locationArray = new ArrayList<Location>();
    ArrayList<String> timestampArray = new ArrayList<String>();

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10 * 1; // 1 minute;10 sec

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                Intent viewIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(viewIntent);
                //showSettingsAlert();
            } else {
                Log.d("hello", "1");
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    Log.d("hello", "network");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            locationArray.add(location);
                            timestampArray.add(new SimpleDateFormat("HH:mm:ss")
                                    .format(new Date()));

                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                locationArray.add(location);
                                timestampArray.add(new SimpleDateFormat("HH:mm:ss")
                                        .format(new Date()));
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            String msg = e.getMessage();
            System.out.println("The problem is :"+msg);
            e.printStackTrace();
        }
        return location;
    }
    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }


    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.create();
        alertDialog.show();
    }
    int counter = 0;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            if(location != null){
                counter = 0;
                locationArray.add(location);
                timestampArray.add(new SimpleDateFormat("HH:mm:ss")
                        .format(new Date()));
            }
            else
            {
                counter++;
                if(counter > 0)
                {
                    // Remove the listener you previously added

                    locationManager.removeUpdates(this);
                    //locationManager = null;

                    //Let user know there was a problem and that GPS was shut off....
                }

            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    @Override
    public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider.


    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    //location
    public static String getLocationName(Geocoder geo, double latitude, double longitude,Context ctx){
        String add_for_display;
        if(ADrivingActivity.netConnect(ctx))
        try{
            List<android.location.Address> addresses = geo.getFromLocation(latitude, longitude, 1);
            if (addresses.isEmpty()) {
                return ("Waiting for Location");
            }
            else {
                if (addresses.size() > 0) {
                    add_for_display=addresses.get(0).getThoroughfare()+" "+addresses.get(0).getFeatureName()+","+addresses.get(0).getLocality();
                    return add_for_display;
                    //location.setText(addresses.toString());

                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
