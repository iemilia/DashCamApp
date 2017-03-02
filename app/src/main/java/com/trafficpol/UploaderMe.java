package com.trafficpol;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

/**
 * Created by Emilia on 4/4/2016.
 */
public class UploaderMe {
    // Fill in your AWS Access Key ID and Secret Access Key
    File fileToUpload;
    File fileToDownload;
    AmazonS3 s3;
    TransferUtility transferUtility;
    Context mContext;
    private String OBJECT_KEY = null;
    private File MY_FILE = null;
    private String MY_BUCKET="bucket_name";

 /*
    String accessKey="accessKey";
    String secretKey="secretKey";
   String existingBucketName = "";
    String keyName;
    String filePath;
    String amazonFileUploadLocationOriginal=existingBucketName+"/";
    AmazonS3Client s3Client;

    private Context context;
*/
    

    public UploaderMe(String uploadedFileName, String filePath, Context context) {

        this.mContext=context;
        this.OBJECT_KEY=uploadedFileName;
        this.MY_FILE=new File(filePath);
        credentialsProvider();
        setTransferUtility();
    }

    public void credentialsProvider(){

        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                mContext,
                "us-east-1:b50e1bdf-63ac-480d-a8a0-efeb2d9fa79f", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );
    }
    /**
     *  Create a AmazonS3Client constructor and pass the credentialsProvider.
     * @param credentialsProvider
     */
    public void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider){
        // Create an S3 client
        s3 = new AmazonS3Client(credentialsProvider);

        // Set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
    }
    public void setTransferUtility(){

        transferUtility = new TransferUtility(s3, mContext);
    }

    /**
     * This method is used to upload the file to S3 by using TransferUtility class
     * @param
     */
    public void setFileToUpload(){
    Log.d("upload","upload");
        if(MY_FILE.exists())
        {
            Log.d("File found ", MY_FILE.getName());
        }
        else {
            Log.d("File not found ", MY_FILE.getName());
        }

        TransferObserver transferObserver = transferUtility.upload(
                MY_BUCKET,     /* The bucket to upload to */
                OBJECT_KEY,    /* The key for the uploaded object */
                MY_FILE        /* The file where the data to upload exists */ );
        transferObserverListener(transferObserver);
       Log.d("transfer state",transferObserver.getState().toString());

    }
    public void transferObserverListener(TransferObserver transferObserver){
        Log.d("upload2","upload");
        Log.d("transfer state2",transferObserver.getState().toString());

        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.e("statechange", state + "");            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent / bytesTotal * 100);
                Log.e("percentage", percentage + "");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("error", "error");
            }

        });
    }

    }