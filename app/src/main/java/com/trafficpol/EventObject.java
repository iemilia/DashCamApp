package com.trafficpol;

import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Emilia on 3/16/2016.
 */
public class EventObject {
    private String timeEvent;
    private String locationEvent;
    private String statusEvent;
    private String dayEvent;
    private String monthEvent;
    private String yearEvent;
    private String hourEvent;
    private File sourceFile;
    private String monthName;
    private File locationFile;
    private Date reportedTime;
    private String messagePolice;

    public String getMessagePolice() {
        return messagePolice;
    }

    public void setMessagePolice(String messagePolice) {
        this.messagePolice = messagePolice;
    }

    public Date getReportedTime() {
        return reportedTime;
    }

    public void setReportedTime(Date reportedTime) {
        this.reportedTime = reportedTime;
    }



    public File getLocationFile() {
        return locationFile;
    }

    public void setLocationFile(File locationFile) {
        this.locationFile = locationFile;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public String getYearEvent() {
        return yearEvent;
    }

    public void setYearEvent(String yearEvent) {
        this.yearEvent = yearEvent;
    }


    private Bitmap thumbEvent;
    EventObject (File file){
        this.sourceFile = file;
    }

    public File getSourceFile() {
        return sourceFile;
    }
    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getDayEvent() {
        return dayEvent;
    }

    public void setDayEvent(String dayVideo) {
        this.dayEvent = dayVideo;
    }

    public String getMonthEvent() {
        return monthEvent;
    }

    public void setMonthEvent(String monthEvent) {
        this.monthEvent = monthEvent;
    }

    public String getHourEvent() {
        return hourEvent;
    }

    public void setHourEvent(String hourEvent) {
        this.hourEvent = hourEvent;
    }


    public String getTimeEvent() throws IOException {
        return timeEvent;
    }

    public void setTimeEvent(String timeEvent) {
        this.timeEvent = timeEvent;
    }
    public String getLocationEvent() throws IOException {
        return locationEvent;
    }

    public void setLocationEvent(String locationEvent) {
        this.locationEvent = locationEvent;
    }

    public String getStatusEvent() {
        return statusEvent;
    }

    public void setStatusEvent(String statusEvent) {
        this.statusEvent = statusEvent;
    }

    public void setThumbnail( Bitmap thumb){
        this.thumbEvent=thumb;
    }
    public Bitmap getThumbnail(){
        return thumbEvent;
    }

   }
