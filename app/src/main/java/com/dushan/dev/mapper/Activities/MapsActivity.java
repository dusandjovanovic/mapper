package com.dushan.dev.mapper.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.dushan.dev.mapper.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    static final int PERMISSION_ACCESS_FINE_LOCATION = 1;

    public static final int SHOW_MAP = 0;
    public static final int CENTER_PLACE_ON_MAP = 1;
    private int state = 0;
    private LatLng currentLocation;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else{
            if(state == SHOW_MAP)
                mMap.setMyLocationEnabled(true);
            else
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            initiateMarkers();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResult) {
        switch (requestCode) {
            case 1: {
                if(grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_DENIED) {
                    if(state == SHOW_MAP)
                        mMap.setMyLocationEnabled(true);
                    else
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    initiateMarkers();
                }
            }
        }
    }

    private void initiateMarkers() {
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
