package com.dushan.dev.mapper.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dushan.dev.mapper.Adapters.MarkersAdapter;
import com.dushan.dev.mapper.Data.LocationData;
import com.dushan.dev.mapper.Data.Marker;
import com.dushan.dev.mapper.Data.MarkerData;
import com.dushan.dev.mapper.Data.MergedData;
import com.dushan.dev.mapper.Data.SavedMarkerData;
import com.dushan.dev.mapper.Data.SocialData;
import com.dushan.dev.mapper.Data.UserData;
import com.dushan.dev.mapper.Interfaces.ClickListener;
import com.dushan.dev.mapper.R;
import com.dushan.dev.mapper.Services.LocationService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final int RECENT_TAB = 1;
    private final int SAVED_TAB = 2;
    private int selectedTab;

    private SharedPreferences sharedPref;

    private Toolbar toolbar;
    private TextView recentTab, savedTab;
    private View recentHighlight, savedHighlight;
    private RecyclerView mainRecycler;
    private MarkersAdapter markersAdapter;
    private FloatingActionButton addMarkerButton;

    private String userId;
    private String email;
    private boolean backgroundService;

    private LocationData locationData;
    private MarkerData markerData;
    private SavedMarkerData savedData;
    private MergedData mergedData;
    private UserData userData;
    private SocialData socialData;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        email = mAuth.getCurrentUser().getEmail();

        locationData = LocationData.getInstance(userId, getApplicationContext());
        userData = UserData.getInstance(userId);
        markerData = MarkerData.getInstance(userId);
        socialData = SocialData.getInstance(userId, getApplicationContext());
        savedData = SavedMarkerData.getInstance(userId);
        mergedData = MergedData.getInstance(userId, getApplicationContext());

        sharedPref = getSharedPreferences("mapper", MODE_PRIVATE);
        if (!sharedPref.contains("backgroundService")) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("backgroundInterval", 10000);
            editor.putBoolean("backgroundService", true);
            backgroundService = true;
            editor.commit();
        }
        else
            backgroundService = sharedPref.getBoolean("backgroundService", true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            if (backgroundService && !isMyServiceRunning(LocationService.class)) {
                Intent serviceIntent = new Intent(this, LocationService.class);
                startService(serviceIntent);
            }
        }

        toolbar = (Toolbar) findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.homeDrawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        selectedTab = RECENT_TAB;
        connectViews();

        // debugging backend api calls
        // socialData.sendUserRequest("lm57cclCT5QleqxeTOQnESDPtdg2");
        // socialData.acceptUserRequest("5UF94tC3hXg23JqA5LLJ2xds8ty2");
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResult) {
        switch (requestCode) {
            case 1: {
                if(grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_DENIED) {
                    if (backgroundService && !isMyServiceRunning(LocationService.class)) {
                        Intent serviceIntent = new Intent(this, LocationService.class);
                        startService(serviceIntent);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.homeDrawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.homeMenuSignOut) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.commit();

            locationData.reinitateSingleton();
            userData.reinitateSingleton();
            markerData.reinitateSingleton();
            socialData.reinitateSingleton();
            savedData.reinitateSingleton();
            mergedData.reinitateSingleton();

            mAuth.signOut();
            Intent homeActivityIntent = new Intent(HomeActivity.this, HomePageActivity.class);
            startActivity(homeActivityIntent);
            return true;
        }
        else if (id == R.id.homeMenuMap) {
            Intent activityIntent = new Intent(HomeActivity.this, MapsActivity.class);
            startActivity(activityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.navigationDiscover: {
                Intent activityIntent = new Intent(HomeActivity.this, DiscoverActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationFriends: {
                Intent activityIntent = new Intent(HomeActivity.this, FriendsActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationSearch: {
                Intent activityIntent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationStatistics: {
                Intent activityIntent = new Intent(HomeActivity.this, StatisticsActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationSettings: {
                Intent activityIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(activityIntent);
                break;
            }
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.homeDrawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void connectViews() {
        recentTab = findViewById(R.id.recentTab);
        savedTab = findViewById(R.id.savedTab);
        recentHighlight = findViewById(R.id.recentHighlight);
        savedHighlight = findViewById(R.id.savedHighlight);
        mainRecycler = findViewById(R.id.mainRecycler);
        addMarkerButton = findViewById(R.id.homeAddMarkerButton);
        NavigationView navigationView = (NavigationView) findViewById(R.id.homeNavView);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.navigationDrawerEmail);
        navUsername.setText(email);
        initiateActivity();
    }

    private void initiateActivity() {
        setupListeners();
        initiateRecyclerView(mergedData.getMarkers());
    }

    private void initiateRecyclerView(List<Marker> markerList) {
        if (markersAdapter == null) {
            ClickListener listener = (view, position) -> {
                Intent markerActivity = new Intent(HomeActivity.this, MarkerActivity.class);
                if (selectedTab == RECENT_TAB) {
                    markerActivity.putExtra("name", mergedData.getMarkers().get(position).getName());
                    markerActivity.putExtra("author", mergedData.getMarkers().get(position).getAuthor());
                    markerActivity.putExtra("address", mergedData.getMarkers().get(position).getAddress());
                    markerActivity.putExtra("description", mergedData.getMarkers().get(position).getDescription());
                    markerActivity.putExtra("category", mergedData.getMarkers().get(position).getCategory());
                    markerActivity.putExtra("imageURL", mergedData.getMarkers().get(position).getImageURL());
                    markerActivity.putExtra("latitude", mergedData.getMarkers().get(position).getLatitude());
                    markerActivity.putExtra("longitude", mergedData.getMarkers().get(position).getLongitude());
                    markerActivity.putExtra("dateTime", mergedData.getMarkers().get(position).getDateTime());
                    markerActivity.putExtra("authorKey", mergedData.getMarkers().get(position).getAuthorKey());
                    markerActivity.putExtra("markerKey", mergedData.getMarkers().get(position).getKey());
                }
                else {
                    markerActivity.putExtra("name", savedData.getMarkers().get(position).getName());
                    markerActivity.putExtra("author", savedData.getMarkers().get(position).getAuthor());
                    markerActivity.putExtra("address", savedData.getMarkers().get(position).getAddress());
                    markerActivity.putExtra("description", savedData.getMarkers().get(position).getDescription());
                    markerActivity.putExtra("category", savedData.getMarkers().get(position).getCategory());
                    markerActivity.putExtra("imageURL", savedData.getMarkers().get(position).getImageURL());
                    markerActivity.putExtra("latitude", savedData.getMarkers().get(position).getLatitude());
                    markerActivity.putExtra("longitude", savedData.getMarkers().get(position).getLongitude());
                    markerActivity.putExtra("dateTime", savedData.getMarkers().get(position).getDateTime());
                    markerActivity.putExtra("authorKey", savedData.getMarkers().get(position).getAuthorKey());
                    markerActivity.putExtra("markerKey", savedData.getMarkers().get(position).getKey());
                }
                startActivity(markerActivity);
            };
            markersAdapter = new MarkersAdapter(getApplicationContext(), markerList, listener);
            mainRecycler.setLayoutManager(new LinearLayoutManager(this));
            mainRecycler.setAdapter(markersAdapter);
        } else {
            markersAdapter.setmMarkerList(markerList);
            markersAdapter.notifyDataSetChanged();
        }
    }

    private void setupListeners(){
        mergedData.setListener(new MergedData.MergedUpdatedEventListener() {
            @Override
            public void onUpdated() {
                if (selectedTab == RECENT_TAB) {
                    initiateRecyclerView(mergedData.getMarkers());
                }
            }
        });

        savedData.setEventListener(new SavedMarkerData.ListUpdatedEventListener() {
            @Override
            public void onListUpdated() {
                if (selectedTab == SAVED_TAB)
                    initiateRecyclerView(savedData.getMarkers());
            }
        });

        recentTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab = RECENT_TAB;
                initiateRecyclerView(mergedData.getMarkers());
                repaintTabs();
            }
        });

        savedTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab = SAVED_TAB;
                initiateRecyclerView(savedData.getMarkers());
                repaintTabs();
            }
        });

        addMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityIntent = new Intent(HomeActivity.this, NewMarkerActivity.class);
                startActivity(activityIntent);
            }
        });
    }

    private void repaintTabs(){
        recentTab.setTextColor(getApplicationContext().getResources().getColor(R.color.unselectedTabTextColor));
        savedTab.setTextColor(getApplicationContext().getResources().getColor(R.color.unselectedTabTextColor));
        recentHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
        savedHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
        switch (selectedTab) {
            case (RECENT_TAB):
                recentTab.setTextColor(getApplicationContext().getResources().getColor(R.color.altTextColor));
                recentHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.tabHighlightColor));
                break;
            case (SAVED_TAB):
                savedTab.setTextColor(getApplicationContext().getResources().getColor(R.color.altTextColor));
                savedHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.tabHighlightColor));
                break;
            default:
                break;
        }
    }
}
