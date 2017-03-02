package com.trafficpol;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;



/**
 * Created by Emilia on 3/22/2016.
 */
public class ViewHolderDetails extends MyRecyclerViewVideosAdapter
        .VideoObjectHolder implements OnMapReadyCallback {
    TextView day;
    TextView month;
    TextView location;
    TextView hourVideo;
    Button report;
    GoogleMap gMap;
    MapView map;
    Double latitude,longitude;

    //ImageView thumbnailEvent;

    public ViewHolderDetails(View itemView, String coordinates) {
        super(itemView);
        day = (TextView) itemView.findViewById(R.id.day);
        month = (TextView) itemView.findViewById(R.id.month);
        report = (Button) itemView.findViewById(R.id.report);
        location = (TextView) itemView.findViewById(R.id.location);
        hourVideo = (TextView) itemView.findViewById(R.id.time);
        map = (MapView) itemView.findViewById(R.id.map);
        Log.d("coordinates", coordinates);
        if(!coordinates.equals("no location found saved")&&!coordinates.equals("location available")) {

            latitude = Double.parseDouble(coordinates.split(" ")[0]);
            longitude = Double.parseDouble(coordinates.split(" ")[1]);
        }

        if (map != null) {
            map.onCreate(null);
            map.onResume();
            map.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //initialize the Google Maps Android API if features need to be used before obtaining a map
        MapsInitializer.initialize(itemView.getContext());
        gMap = googleMap;

        if(latitude!=null && longitude!=null){
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(latitude,
                        longitude));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        gMap.moveCamera(center);
        gMap.animateCamera(zoom);
        }

    }


}
