package com.dushan.dev.mapper.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dushan.dev.mapper.Adapters.MarkersAdapter;
import com.dushan.dev.mapper.Data.Marker;
import com.dushan.dev.mapper.Data.MarkerData;
import com.dushan.dev.mapper.Data.User;
import com.dushan.dev.mapper.Data.UserData;
import com.dushan.dev.mapper.Interfaces.ClickListener;
import com.dushan.dev.mapper.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final int RECENT_TAB = 1;
    private final int SAVED_TAB = 2;
    private final int VIEW_TAB = 3;

    private SharedPreferences sharedPref;

    private Toolbar toolbar;
    private TextView recentTab, savedTab, viewTab;
    private View recentHighlight, savedHighlight, viewHighlight;
    private RecyclerView mainRecycler;
    private int selectedTab;
    private MarkersAdapter markersAdapter;
    private FloatingActionButton addMarkerButton;

    private String userId;
    private String email;

    private MarkerData markerData;
    private UserData userData;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        email = mAuth.getCurrentUser().getEmail();
        markerData = MarkerData.getInstance(userId);
        userData = UserData.getInstance(userId);
        sharedPref = getSharedPreferences("mapper", MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.homeDrawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        selectedTab = RECENT_TAB;
        connectViews();
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

            mAuth.signOut();
            Intent homeActivityIntent = new Intent(HomeActivity.this, HomePageActivity.class);
            startActivity(homeActivityIntent);
            return true;
        }
        else if (id == R.id.homeMenuSearch) {
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
            case R.id.navigationHome: {

                break;
            }
            case R.id.navigationDiscover: {

                break;
            }
            case R.id.navigationFriends: {
                Intent activityIntent = new Intent(HomeActivity.this, FriendsActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationSearch: {

                break;
            }
            case R.id.navigationStatistics: {

                break;
            }
            case R.id.navigationSettings: {
                Intent activityIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(activityIntent);
                break;
            }
            default:
                return false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.homeDrawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void connectViews() {
        recentTab = findViewById(R.id.recentTab);
        savedTab = findViewById(R.id.savedTab);
        viewTab = findViewById(R.id.viewTab);
        recentHighlight = findViewById(R.id.recentHighlight);
        savedHighlight = findViewById(R.id.savedHighlight);
        viewHighlight = findViewById(R.id.viewHighlight);
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
        initiateRecyclerView(markerData.getMarkers());
    }

    private void initiateRecyclerView(List<Marker> markerList) {
        if (markersAdapter == null) {
            ClickListener listener = (view, position) -> {
                Intent markerActivity = new Intent(HomeActivity.this, MarkerActivity.class);
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
            mainRecycler.setLayoutManager(new LinearLayoutManager(this));
            mainRecycler.setAdapter(markersAdapter);
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

        recentTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab = RECENT_TAB;
                initiateRecyclerView(markerData.getMarkers());
                repaintTabs();
            }
        });

        savedTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab = SAVED_TAB;
                initiateRecyclerView(markerData.getMarkers());
                repaintTabs();
            }
        });

        viewTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab = VIEW_TAB;
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
        viewTab.setTextColor(getApplicationContext().getResources().getColor(R.color.unselectedTabTextColor));
        recentHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
        savedHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
        viewHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
        switch (selectedTab) {
            case (RECENT_TAB):
                recentTab.setTextColor(getApplicationContext().getResources().getColor(R.color.altTextColor));
                recentHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.tabHighlightColor));
                break;
            case (SAVED_TAB):
                savedTab.setTextColor(getApplicationContext().getResources().getColor(R.color.altTextColor));
                savedHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.tabHighlightColor));
                break;
            case (VIEW_TAB):
                viewTab.setTextColor(getApplicationContext().getResources().getColor(R.color.altTextColor));
                viewHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.tabHighlightColor));
                break;
            default:
                break;
        }
    }
}
