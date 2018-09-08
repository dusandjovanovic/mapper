package com.dushan.dev.mapper.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.dushan.dev.mapper.Adapters.MarkersAdapter;
import com.dushan.dev.mapper.Data.Marker;
import com.dushan.dev.mapper.Data.MergedData;
import com.dushan.dev.mapper.Interfaces.ClickListener;
import com.dushan.dev.mapper.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener{

    private int DIAMETER_MAX = 1000;
    private String CATEGORY_ALL = "All";

    private SharedPreferences sharedPref;
    private Toolbar toolbar;
    private RecyclerView searchRecycler;
    private MarkersAdapter markersAdapter;

    private String userId;
    private String email;

    private ArrayList<String> filterKeywords = new ArrayList<String>();
    private String filterCategory = CATEGORY_ALL;
    private int filterDiameter = DIAMETER_MAX;

    private ImageButton searchButton;
    private EditText searchKeywordsEditText;
    private TextView searchDiameterText;
    private Spinner searchSpinner;
    private SeekBar searchDiameterSeekBar;

    private MergedData markerData;

    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng lastKnowLocation = new LatLng(43.3203158, 21.9170784);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        sharedPref = getSharedPreferences("mapper", MODE_PRIVATE);
        userId = sharedPref.getString("userId", null);
        email = sharedPref.getString("email", null);
        markerData = MergedData.getInstance(userId, getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.searchDrawer);
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
                            inititateActivity();
                        }
                    }
                });

        connectViews();
    }

    private void connectViews() {
        searchButton = findViewById(R.id.searchButton);
        searchKeywordsEditText = findViewById(R.id.searchKeywordsEditText);
        searchDiameterText = findViewById(R.id.searchDiameterText);
        searchDiameterSeekBar = findViewById(R.id.searchDiameterSeekBar);
        searchSpinner = findViewById(R.id.searchSpinner);
        searchRecycler = findViewById(R.id.searchRecycler);

        NavigationView navigationView = (NavigationView) findViewById(R.id.searchNavView);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.navigationDrawerEmail);
        navUsername.setText(email);
    }

    private void inititateActivity() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_filter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSpinner.setAdapter(adapter);
        searchSpinner.setOnItemSelectedListener(this);
        initiateRecyclerView(markerData.filteredMarkers(lastKnowLocation, filterKeywords, filterCategory, filterDiameter));
        setupListeners();
    }

    private void initiateRecyclerView(List<Marker> markerList) {
        if (markersAdapter == null) {
            ClickListener listener = (view, position) -> {
                Intent markerActivity = new Intent(SearchActivity.this, MarkerActivity.class);
                markerActivity.putExtra("name", markerData.getFilteredMarkers().get(position).getName());
                markerActivity.putExtra("author", markerData.getFilteredMarkers().get(position).getAuthor());
                markerActivity.putExtra("address", markerData.getFilteredMarkers().get(position).getAddress());
                markerActivity.putExtra("description", markerData.getFilteredMarkers().get(position).getDescription());
                markerActivity.putExtra("category", markerData.getFilteredMarkers().get(position).getCategory());
                markerActivity.putExtra("imageURL", markerData.getFilteredMarkers().get(position).getImageURL());
                markerActivity.putExtra("latitude", markerData.getFilteredMarkers().get(position).getLatitude());
                markerActivity.putExtra("longitude", markerData.getFilteredMarkers().get(position).getLongitude());
                markerActivity.putExtra("dateTime", markerData.getFilteredMarkers().get(position).getDateTime());
                markerActivity.putExtra("markerKey", markerData.getFilteredMarkers().get(position).getKey());
                startActivity(markerActivity);
            };
            markersAdapter = new MarkersAdapter(getApplicationContext(), markerList, listener);
            searchRecycler.setLayoutManager(new LinearLayoutManager(this));
            searchRecycler.setAdapter(markersAdapter);
        } else {
            markersAdapter.setmMarkerList(markerList);
            markersAdapter.notifyDataSetChanged();
        }
    }

    private void setupListeners(){
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterKeywords.clear();
                filterKeywords = new ArrayList<String>(Arrays.asList(searchKeywordsEditText.getText().toString().split("\\s+")));
                initiateRecyclerView(markerData.filteredMarkers(lastKnowLocation, filterKeywords, filterCategory, filterDiameter));
            }
        });

        searchDiameterSeekBar.setMax(DIAMETER_MAX);
        searchDiameterSeekBar.setProgress(DIAMETER_MAX);
        searchDiameterText.setText(String.valueOf(DIAMETER_MAX) + "km");
        searchDiameterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                filterDiameter = i;
                searchDiameterText.setText(String.valueOf(filterDiameter) + "km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                searchDiameterText.setText(String.valueOf(filterDiameter) + "km");
            }
        });

        markerData.setListener(new MergedData.MergedUpdatedEventListener() {
            @Override
            public void onUpdated() {
                initiateRecyclerView(markerData.filteredMarkers(lastKnowLocation, filterKeywords, filterCategory, filterDiameter));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.searchMenuMap) {
            Intent activityIntent = new Intent(SearchActivity.this, MapsActivity.class);
            activityIntent.putExtra("search", true);
            startActivity(activityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.searchDrawer);
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
                Intent activityIntent = new Intent(SearchActivity.this, HomeActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationDiscover: {
                Intent activityIntent = new Intent(SearchActivity.this, DiscoverActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationFriends: {
                Intent activityIntent = new Intent(SearchActivity.this, FriendsActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationStatistics: {
                Intent activityIntent = new Intent(SearchActivity.this, StatisticsActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationSettings: {
                Intent activityIntent = new Intent(SearchActivity.this, SettingsActivity.class);
                startActivity(activityIntent);
                break;
            }
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.searchDrawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        filterCategory = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        filterCategory = CATEGORY_ALL;
    }
}
