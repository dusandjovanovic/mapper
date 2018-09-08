package com.dushan.dev.mapper.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dushan.dev.mapper.Data.Marker;
import com.dushan.dev.mapper.Data.MergedData;
import com.dushan.dev.mapper.Data.SocialData;
import com.dushan.dev.mapper.Data.User;
import com.dushan.dev.mapper.Interfaces.GlideApp;
import com.dushan.dev.mapper.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    static final String TAG = "MapsActivity";

    public static final int SHOW_MAP = 0;
    private boolean searchView;
    private int state = 0;

    private GoogleMap mMap;
    private LatLng currentLocation = null;

    private FirebaseAuth mAuth;
    private String userId;
    private MergedData mergedData;
    private SocialData socialData;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        userId = mAuth.getCurrentUser().getUid();
        socialData = SocialData.getInstance(userId, getApplicationContext());
        mergedData = MergedData.getInstance(userId, getApplicationContext());
        Intent intent = getIntent();

        searchView = intent.getBooleanExtra("search", false);
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);
        if (latitude != 0) {
            currentLocation = new LatLng(latitude, longitude);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.mapsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else{
            mMap.setMyLocationEnabled(true);
            initiateMarkers();
        }

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
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
        mMap.setOnMarkerClickListener(this);
        if (searchView) {
            for (Marker marker: mergedData.getFilteredMarkers()) {
                LatLng markerLoation = new LatLng(marker.getLatitude(), marker.getLongitude());
                Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_location_marker_place);
                BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                mMap.addMarker(new MarkerOptions()
                        .position(markerLoation)
                        .snippet(marker.getDescription())
                        .title(marker.getName())
                        .icon(markerIcon)
                );
            }
        }
        else {
            for (Marker marker: mergedData.getMarkers()) {
                LatLng markerLoation = new LatLng(marker.getLatitude(), marker.getLongitude());
                Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_location_marker_place);
                BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                mMap.addMarker(new MarkerOptions()
                        .position(markerLoation)
                        .snippet(marker.getDescription())
                        .title(marker.getName())
                        .icon(markerIcon)
                );
            }
            for (User user: socialData.getSocialFriends()) {
                GlideApp.with(MapsActivity.this)
                        .asBitmap()
                        .fitCenter()
                        .override(60, 60)
                        .load(user.getImage())
                        .apply(RequestOptions.circleCropTransform())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                LatLng markerLoation = new LatLng(user.getLatitude(), user.getLongitude());
                                com.google.android.gms.maps.model.Marker marker = mMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                        .position(markerLoation)
                                );
                                marker.setTag(user);
                            }
                        });
            }
        }
        if (currentLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        }
    }

    @Override
    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
        if (marker.getTag() instanceof User) {
            User userData = (User) marker.getTag();
            Intent activityIntent = new Intent(MapsActivity.this, AccountActivity.class);
            activityIntent.putExtra("name", userData.getName());
            activityIntent.putExtra("lastName", userData.getLastName());
            activityIntent.putExtra("phone", userData.getPhoneNumber());
            activityIntent.putExtra("email", userData.getEmail());
            activityIntent.putExtra("latitude", userData.getLatitude());
            activityIntent.putExtra("longitude", userData.getLongitude());
            activityIntent.putExtra("about", userData.getAbout());
            activityIntent.putExtra("reachImpact", userData.getReachImpact());
            startActivity(activityIntent);
            return true;
        }
        return false;
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
