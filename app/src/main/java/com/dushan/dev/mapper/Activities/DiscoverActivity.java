package com.dushan.dev.mapper.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dushan.dev.mapper.Adapters.MarkersAdapter;
import com.dushan.dev.mapper.Data.Marker;
import com.dushan.dev.mapper.Data.MarkerData;
import com.dushan.dev.mapper.Data.UserData;
import com.dushan.dev.mapper.Interfaces.ClickListener;
import com.dushan.dev.mapper.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

public class DiscoverActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private SharedPreferences sharedPref;

    private Toolbar toolbar;
    private RecyclerView discoverRecycler;
    private MarkersAdapter markersAdapter;

    private MarkerData markerData;
    private UserData userData;

    private String userId;
    private String email;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        email = mAuth.getCurrentUser().getEmail();
        markerData = MarkerData.getInstance(userId);
        userData = UserData.getInstance(userId);
        sharedPref = getSharedPreferences("mapper", MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.discoverToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.discoverDrawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

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
        markerData.setEventListener(new MarkerData.ListUpdatedEventListener() {
            @Override
            public void onListUpdated() {
                initiateRecyclerView(markerData.getMarkers());
            }
        });
    }
}
