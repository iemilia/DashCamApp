package com.trafficpol;

import android.app.Activity;
import android.content.Context;
import android.location.Geocoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Emilia on 3/22/2016.
 */
public class ManageFiles {
    //set directory for videos/events
    public static File getOutputDir(String type, String filename) {
        File mediaStorageDir=null;
        if(type.equals("locationEvent")||type.equals("locationVideo"))
            mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory()+"/TraficPolLocation/"+filename+".txt"
            );

        return mediaStorageDir;
    }
    //set directory for videos/events
    public static File getOutputDir(String type) {
        File mediaStorageDir=null;
        if(type.equals("video"))
            mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory()+"/TraficPol"
            );
        else if(type.equals("event"))
            mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory()+"/TraficPolEvents"
            );
        else if(type.equals("locationEvent")||type.equals("locationVideo"))
            mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory()+"/TraficPolLocation"
            );
        else if(type.equals("default"))
            mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory()+"/TraficPolDefault"
            );
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        return mediaStorageDir;
    }

    public static File getOutputFile(String type) throws IOException {
        File mediaStorageDir = getOutputDir(type);
        File mediaFile = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        if (type.equals("video"))
            mediaFile = new File(mediaStorageDir, timeStamp + ".mp4");
        else if (type.equals("locationVideo")) {
            mediaFile = new File(mediaStorageDir, timeStamp + ".txt");
            mediaFile.createNewFile();
        } else if (type.equals("event"))
            mediaFile = new File(mediaStorageDir, String.format("interm-%s.mp4", timeStamp));
        else if (type.equals("locationEvent")){
            mediaFile = new File(mediaStorageDir, String.format("interm-%s.txt", timeStamp));
            mediaFile.createNewFile();
        }
        return mediaFile;
    }


    public static File[] getFiles(boolean isAscending,File dir) {
        //File dir = getOutputDir(type);

        if (dir == null) {
            return null;
        }

        File files[] = dir.listFiles();

        if (isAscending) {
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.valueOf(f1.lastModified())
                            .compareTo(f2.lastModified());
                }
            });
        }
        else {
            Arrays.sort(files, new Comparator<File>(){
                public int compare(File f1, File f2) {
                    return Long.valueOf(f2.lastModified())
                            .compareTo(f1.lastModified());
                }
            });
        }
        return files;
    }

    private long getFreeSpace() {
        StatFs stat = new StatFs(
                Environment.getExternalStorageDirectory().getPath()
        );

        return (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
    }
    //set directory for editted events
    public static File getEdittedOutputDir() {
        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory()+"/TraficPolEditted"
        );
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return mediaStorageDir;
    }
    //set directory for editted events
    public static File getEventOutputDir() {
        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory()+"/TraficPolEvents"
        );
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return mediaStorageDir;
    }

    public static void updateGallery(File[] files, Activity mAct)
    {
        for (int i = 0; i < files.length; i++) {
            MediaScannerConnection.scanFile(mAct,
                    new String[]{files[i].toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });}
    }

    public static String readLocationFromFile(File locationFile,Context context) {
        InputStream inputStream = null;
        BufferedReader br = null;

        try {
            // read this file into InputStream
            inputStream = new FileInputStream(locationFile);
            br = new BufferedReader(new InputStreamReader(inputStream));
            String location_to_returnOne;
            String location_to_return=br.readLine();
            if ((location_to_returnOne = br.readLine()) != null&& !location_to_returnOne.equals("null"))
                return location_to_returnOne;

            if(ADrivingActivity.netConnect(context)&& location_to_return!=null){
                //Log.d("UNREP","1");
                double latitude=Double.parseDouble(location_to_return.split(" ")[1]);
                double longitude=Double.parseDouble(location_to_return.split(" ")[2]);
                Geocoder geo = new Geocoder(context, Locale.getDefault());
                String location=GPSTracker.getLocationName(geo,latitude,longitude,context);
                if (location!=null)
                    return location;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "no location found saved";
    }
    public static String readCoordinatesFromFile(File locationFile) {
        InputStream inputStream = null;
        BufferedReader br = null;

        try {
            // read this file into InputStream
            inputStream = new FileInputStream(locationFile);
            br = new BufferedReader(new InputStreamReader(inputStream));
            String location_to_return=br.readLine();
            if (location_to_return  != null)
                return location_to_return.split(" ")[1]+" "+location_to_return.split(" ")[2];

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "no location found saved";
    }

}
