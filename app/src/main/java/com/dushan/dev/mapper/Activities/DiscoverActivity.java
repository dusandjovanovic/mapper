package com.dushan.dev.mapper.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dushan.dev.mapper.Adapters.MarkersAdapter;
import com.dushan.dev.mapper.Data.Marker;
import com.dushan.dev.mapper.Data.MergedData;
import com.dushan.dev.mapper.Data.UserData;
import com.dushan.dev.mapper.Interfaces.ClickListener;
import com.dushan.dev.mapper.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiscoverActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    static final String TAG = "DISCOVER_ACTIVITY";

    private SharedPreferences sharedPref;
    private Toolbar toolbar;
    private RecyclerView discoverRecycler;
    private MarkersAdapter markersAdapter;

    private MergedData markerData;
    private UserData userData;

    private String userId;
    private String email;

    private FirebaseAuth mAuth;

    private GoogleMap mMap;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng lastKnowLocation = new LatLng(43.3203158, 21.9170784);;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.discoverMap);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        email = mAuth.getCurrentUser().getEmail();
        markerData = MergedData.getInstance(userId, getApplicationContext());
        userData = UserData.getInstance(userId);
        sharedPref = getSharedPreferences("mapper", MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.discoverToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.discoverDrawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            lastKnowLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(lastKnowLocation)
                                    .zoom(13)
                                    .build();
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    }
                });

        connectViews();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.discoverDrawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.navigationHome: {
                Intent activityIntent = new Intent(DiscoverActivity.this, HomeActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationFriends: {
                Intent activityIntent = new Intent(DiscoverActivity.this, FriendsActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationSearch: {
                Intent activityIntent = new Intent(DiscoverActivity.this, SearchActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationStatistics: {
                Intent activityIntent = new Intent(DiscoverActivity.this, StatisticsActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationSettings: {
                Intent activityIntent = new Intent(DiscoverActivity.this, SettingsActivity.class);
                startActivity(activityIntent);
                break;
            }
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.discoverDrawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void connectViews() {
        discoverRecycler = findViewById(R.id.discoverRecycler);
        NavigationView navigationView = (NavigationView) findViewById(R.id.discoverNavView);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.navigationDrawerEmail);
        navUsername.setText(email);
        initiateActivity();
    }

    private void initiateActivity() {
        setupListeners();
        initiateRecyclerView(markerData.getMarkers());
    }

    private void initiateRecyclerView(List<Marker> markerList) {
        if (markersAdapter == null) {
            ClickListener listener = (view, position) -> {
                Intent markerActivity = new Intent(DiscoverActivity.this, MarkerActivity.class);
                markerActivity.putExtra("name", markerData.getMarkers().get(position).getName());
                markerActivity.putExtra("author", markerData.getMarkers().get(position).getAuthor());
                markerActivity.putExtra("address", markerData.getMarkers().get(position).getAddress());
                markerActivity.putExtra("description", markerData.getMarkers().get(position).getDescription());
                markerActivity.putExtra("category", markerData.getMarkers().get(position).getCategory());
                markerActivity.putExtra("imageURL", markerData.getMarkers().get(position).getImageURL());
                markerActivity.putExtra("latitude", markerData.getMarkers().get(position).getLatitude());
                markerActivity.putExtra("longitude", markerData.getMarkers().get(position).getLongitude());
                markerActivity.putExtra("dateTime", markerData.getMarkers().get(position).getDateTime());
                markerActivity.putExtra("markerKey", markerData.getMarkers().get(position).getKey());
                startActivity(markerActivity);
            };
            markersAdapter = new MarkersAdapter(getApplicationContext(), markerList, listener);
            discoverRecycler.setLayoutManager(new LinearLayoutManager(this));
            discoverRecycler.setAdapter(markersAdapter);
        } else {
            markersAdapter.setmMarkerList(markerList);
            markersAdapter.notifyDataSetChanged();
        }
    }

    private void setupListeners(){
        markerData.setListener(new MergedData.MergedUpdatedEventListener() {
            @Override
            public void onUpdated() {
                initiateRecyclerView(markerData.getMarkers());
            }
        });
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
            initiateHeatMap();
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

    private void initiateHeatMap() {
        ArrayList<LatLng> heatMap = new ArrayList<LatLng>();
        for (Marker marker: markerData.recentMarkers())
            heatMap.add(new LatLng(marker.getLatitude(), marker.getLongitude()));

        mProvider = new HeatmapTileProvider.Builder()
                .data(heatMap)
                .radius(50)
                .build();

        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
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
