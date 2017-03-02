package com.trafficpol;

import android.graphics.Bitmap;
import android.location.Location;
import java.io.File;
import java.io.IOException;

/**
 * Created by Emilia on 3/16/2016.
 */
public class VideoObject {
    private String timeVideo;
    private String name;
    private String dayVideo;
    private String monthVideo;
    private String hourVideo;
    private String yearVideo;
    private String monthName;
    private File locationFile;
    private String locationFromFileVideo;
    private String locationVideo;
    private Location realLocation;

    public boolean getAudioType() {
        return audioType;
    }

    public void setAudioType(boolean audioType) {
        this.audioType = audioType;
    }

    private boolean audioType;

    public File getLocationFile() {
        return locationFile;
    }

    public void setLocationFile(File locationFile) {
        this.locationFile = locationFile;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getRealLocation() {
        return realLocation;
    }

    public void setRealLocation(Location realLocation) {
        this.realLocation = realLocation;
    }

    private String statusVideo;//edited/reported or not

    private File sourceFile;
    private Bitmap thumbEvent;

    private String categoryVideo;
    private boolean audio;//is it on or off

    public String getYearVideo() {
        return yearVideo;
    }

    public void setYearVideo(String yearVideo) {
        this.yearVideo = yearVideo;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    VideoObject(File file){
        this.sourceFile = file;
    }

    public String getDayVideo() {
        return dayVideo;
    }

    public void setDayVideo(String dayVideo) {
        this.dayVideo = dayVideo;
    }

    public String getMonthVideo() {
        return monthVideo;
    }

    public void setMonthVideo(String monthVideo) {
        this.monthVideo = monthVideo;
    }

    public String getHourVideo() {
        return hourVideo;
    }

    public void setHourVideo(String hourVideo) {
        this.hourVideo = hourVideo;
    }

    public String getCategoryVideo() {
        return categoryVideo;
    }

    public void setCategoryVideo(String categoryVideo) {
        this.categoryVideo = categoryVideo;
    }

    public boolean isAudio() {
        return audio;
    }

    public void setAudio(boolean audio) {
        this.audio = audio;
    }

    public String getTimeVideo() throws IOException {
        return timeVideo;
    }

    public void setTimeVideo(String timeVideo) {

        this.timeVideo = timeVideo;
    }


    public String getLocationVideo() throws IOException {
        return locationVideo;
    }

    public void setLocationVideo(String locationVideo) {
        this.locationVideo = locationVideo;
    }

    public String getLocationFromFileVideo() throws IOException {
        return locationFromFileVideo;
    }

    public void setLocationFromFileVideo(String locationFromFileVideo) {
        this.locationFromFileVideo = locationFromFileVideo;
    }

    public String getStatusVideo() {
        return statusVideo;
    }

    public void setStatusVideo(String statusEvent) {
        this.statusVideo = statusEvent;
    }



    public void setThumbnail( Bitmap thumb){
        this.thumbEvent=thumb;
    }
    public Bitmap getThumbnail(){
        return thumbEvent;
    }

   }
