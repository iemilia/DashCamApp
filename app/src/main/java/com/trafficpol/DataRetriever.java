package com.trafficpol;

import android.app.ProgressDialog;
import android.content.Context;

import android.os.AsyncTask;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Emilia on 3/28/2016.
 */
public class DataRetriever extends AsyncTask<File[], Void, Object[]> {


    private File[] filesEvent;
    private ProgressDialog progressDialog;
    private String type;
    final ParseApplication globalVariable= CardViewUnreportedActivity.globalVariable;

    private Context mContext;


    public DataRetriever(File[] filesEvent,Context context,String type) {
        this.filesEvent = filesEvent;
        this.mContext = context;
        this.type=type;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(mContext,
                "Retrieving videos", "Please wait :)...", true);
        //super.onPreExecute();
    }

    @Override
    protected Object[] doInBackground(File[]... params) {
        return getReportedPositons();
    }

    @Override
    protected void onPostExecute(Object[] result) {
        progressDialog.dismiss();
        super.onPostExecute(result);
    }



    private Object[] getReportedPositons() {
        ParseQuery query;
        ArrayList<String> namesFile=new ArrayList<String>();
        ArrayList<Integer> found=new ArrayList<Integer>();
        ArrayList<Date> updatedDates=new ArrayList<Date>();
        ArrayList<String> statuses=new ArrayList<String>();

        for(int i=0;i<filesEvent.length;i++){
            namesFile.add(i,filesEvent[i].getName());
        }
        //query= new ParseQuery(type);
        query=ParseQuery.getQuery(type);
        if(type.equals("Event"))
            query.whereContains("status_event","UNREPORTED");
        query.whereContains("user", globalVariable.getUserName());
        query.orderByAscending("CreatedDate");

        List<ParseObject> list_to_update= null;
        try {
            list_to_update = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //List<String> names_reported=list_to_update.getString("title");
        int j=0;
        for(int i=0;i<list_to_update.size();i++)
            if(namesFile.contains(list_to_update.get(i).getString("title"))){
                found.add(j,namesFile.indexOf(list_to_update.get(i).getString("title")));
                updatedDates.add(j,list_to_update.get(i).getCreatedAt());
                statuses.add(j,list_to_update.get(i).getString("status_event"));
                j++;
            }
        return new Object[]{found,updatedDates,statuses};
    }

}
