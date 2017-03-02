package com.trafficpol;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

/**
 * Created by Emilia on 4/6/2016.
 */
public class AmazonService extends AsyncTask<String, Boolean, String> {
    Context mContext;
    String OBJECT_KEY;
    File MY_FILE;
    private ProgressDialog progressDialog;

    public AmazonService(String uploadedFileName, String filePath,Context context) {
        mContext = context;
        this.OBJECT_KEY=uploadedFileName;

        this.MY_FILE=new File(filePath);

    }
    public String getkey(){
        return OBJECT_KEY;
    }
    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(mContext,
                "Uploading event", "Please wait...", true);
    }


    @Override
    protected String doInBackground(String... params) {// Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                mContext,
                "identity_ID", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        Log.d("bla", "blaaaaaaa");
        System.setProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY, "true");




        AmazonS3Client client =
                new AmazonS3Client(credentialsProvider);
        //client.setRegion(Region.getRegion(Regions.US_EAST_1));
        TransferUtility transferUtility = new TransferUtility(client, mContext);
        TransferObserver observer = transferUtility.upload("dashcamapp",OBJECT_KEY,MY_FILE );
        transferObserverListener(observer);
        Log.d("Test", observer.getState() + " " + observer.getId() + " " + observer.getBytesTransferred());
        return OBJECT_KEY;
    }

    public void transferObserverListener(TransferObserver transferObserver){
        final String TAG = "TransferObserver";
        Log.d(TAG, "upload");
        Log.d(TAG, transferObserver.getState().toString());

        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.e(TAG, state + "");
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent / bytesTotal * 100);
                Log.e(TAG, percentage + "");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG, ex.getMessage());
            }

        });
    }



    @Override
    protected void onPostExecute(String value){
        super.onPostExecute(value);
        this.OBJECT_KEY=value;


        //getOBJECT_KEY(value);
        progressDialog.dismiss();

    }
    private String getOBJECT_KEY(String OBJECT_KEY) {
        //handle value
        return OBJECT_KEY;
    }

}
