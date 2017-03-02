package parseObjects;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Emilia on 4/6/2016.
 */
@ParseClassName("Event")
public class Event extends ParseObject{

    public ParseUser user;
    public String date;
    public int nrcuts;
    public ArrayList<Integer> cutPositions;
    public String location;
    public ArrayList<Double> longHistory;

    public void setLocationFileTitle(String locationFile_title) {
        put("locationFile_title",locationFile_title);
    }

    public String getLocationFileTitle() {
        return getString("locationFile_title");
    }

    public void setTitle(String title) {
        put("title",title);
    }

    public String getTitle() {
        return getString("title");
    }


    public void setLatHistory(ArrayList<Double> latHistory) {
        put("latitudes_history",latHistory);
    }

    public void setLongHistory(ArrayList<Double> longHistorygHistory) {
        put("longitudes_history",longHistory);
    }

    public List<Double> getLatHistory() {
        return getList("latitudes_history");
    }
    public List<Double> getLongHistory() {
        return getList("latitudes_history");
    }
    public void getLongHistory(ArrayList<Double> longHistorygHistory) {
        put("longitudes_history",longHistory);
    }
    public String getOBJECT_KEY() {
        return getString("OBJECT_KEY");    }

    public void setOBJECT_KEY(String OBJECT_KEY) {
        put("OBJECT_KEY",OBJECT_KEY);
    }

    public String OBJECT_KEY;
    public String address;

    public Date getReportedAt() {
        return getDate("reportedAt");
    }

    public void setReportedAt(Date reportedAt) {
        put("reportedAt", reportedAt);     }

    public String getResolution() {
        return getString("resolution");    }

    public void setResolution(String resolution) {
        put("resolution",resolution);     }

    public String resolution;

    public boolean isCutaudio() {
        return getBoolean("is_audio_cut");    }

    public void setCutaudio(boolean cutaudio) {
        put("is_audio_cut", cutaudio);      }

    public int getNrcuts() {
        return nrcuts;
    }

    public void setNrcuts(int nrcuts) {
        put("nrCuts", nrcuts);      }

    public double getLongitude() {
        return getDouble("longitude");    }

    public void setLongitude(double longitude) {
        put("longitude", longitude);      }

    public double getLatitude() {
        return getDouble("latitude");    }

    public void setLatitude(double latitude) {
        put("latitude", latitude);      }

    public String getAddress() {
        return getString("address");    }

    public void setAddress(String address) {
        put("address", address);      }






    public Event() {
        // A default constructor is required.
    }

    public String getStatus() {
        return getString("status_event");
    }

    public void setStatus(String status) {
        put("status_event", status);    }

    public String getParentVideo() {
        return getString("parent_video");
    }

    public void setParentVideo(String parentVideo) {
        put("parent_video", parentVideo);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public String getCategory() {
        return getString("category");
    }

    public void setCategory(String category) {
        put("category", category);

    }

    public String getDate() {
        return getString("date_event");
    }

    public void setDate(String date) {
        put("date_event", date);
    }


    public boolean isCutAudio() {
        return getBoolean("audio_on");
    }

    public void setCutAudio(boolean cutaudio) {
        put("audio_on", cutaudio);
    }


    public ArrayList<Integer> getCutPositions() {
        return cutPositions;
    }

    public void setCutPositions(ArrayList<Integer> cutPositions) {
        this.cutPositions = cutPositions;
    }

    public String getLinkAmazon() {
        return getString("link_Amazon");
    }

    public void setLinkAmazon(String linkAmazon) {
        put("link_Amazon",linkAmazon);
    }

    public ParseUser getUser() {
        return user;
    }

    public void setUser(String user) {
        put("user",user) ;
    }

    public String getUserId() {
        return getString("user_id");
    }

    public void setUserId(String userId) {
        put("user_id",userId);
    }

    public Date getCreatedDate(){return getDate("createdAt");}

    public void setSize(String size) {
        put("file_size",size) ;
    }

    public String setSize() {
        return getString("file_size");
    }
    public void setDuration(Integer duration) {
        put("video_duration",duration) ;
    }

    public Integer setDuration() {
        return getInt("video_duration");
    }

    public void setLocation(String location) {
        put("video_location",location) ;
    }


    public Integer setLocation() {
        return getInt("video_location");
    }


    public ParseFile getLocationFile() {

        return getParseFile("Location_File");
    }

    public void setLocationFile(ParseFile file) {

        put("Location_File", file);
    }
}
