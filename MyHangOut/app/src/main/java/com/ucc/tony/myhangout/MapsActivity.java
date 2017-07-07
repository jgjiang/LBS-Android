package com.ucc.tony.myhangout;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double venueLatD;
    private double venueLngD;
    private String venueTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // use Intent to obtain the venue attributes
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        venueLatD = bundle.getDouble("venueLat");
        venueLngD = bundle.getDouble("venueLng");
        venueTitle = bundle.getString("title");
    }


    // setup Google Map with marker
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng venueLatLng = new LatLng(venueLatD, venueLngD);
        mMap.addMarker(new MarkerOptions().position(venueLatLng).title(venueTitle));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(venueLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
    }
}
