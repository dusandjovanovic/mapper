package com.dushan.dev.mapper.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.dushan.dev.mapper.Data.Marker;
import com.dushan.dev.mapper.Interfaces.GlideApp;
import com.dushan.dev.mapper.R;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    private SharedPreferences sharedPref;
    private Toolbar toolbar;
    Spinner settingsBackgroundServiceSpinner;
    Switch settingsBackgroundServiceSwitch;

    private String userId;
    private String email;
    private int interval;
    private boolean backgroundService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPref = getSharedPreferences("mapper", MODE_PRIVATE);
        userId = sharedPref.getString("userId", null);
        email = sharedPref.getString("email", null);
        interval = sharedPref.getInt("backgroundInterval", 10000);
        backgroundService = sharedPref.getBoolean("backgroundService", true);

        toolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.settingsDrawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.settingsNavView);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.navigationDrawerEmail);
        navUsername.setText(email);
        connectViews();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.settingsDrawer);
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
                Intent activityIntent = new Intent(SettingsActivity.this, HomeActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationDiscover: {
                Intent activityIntent = new Intent(SettingsActivity.this, DiscoverActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationFriends: {
                Intent activityIntent = new Intent(SettingsActivity.this, FriendsActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationSearch: {
                Intent activityIntent = new Intent(SettingsActivity.this, SearchActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationStatistics: {
                Intent activityIntent = new Intent(SettingsActivity.this, StatisticsActivity.class);
                startActivity(activityIntent);
                break;
            }
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.settingsDrawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        interval = Integer.parseInt(adapterView.getItemAtPosition(i).toString());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("backgroundInterval", interval);
        editor.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void connectViews() {
        settingsBackgroundServiceSpinner = findViewById(R.id.settingsBackgroundServiceSpinner);
        settingsBackgroundServiceSwitch = findViewById(R.id.settingsBackgroundServiceSwitch);
        initiateActivity();
    }

    private void initiateActivity() {
        settingsBackgroundServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("backgroundService", isChecked);
                editor.commit();
            }
        });

        settingsBackgroundServiceSwitch.setChecked(backgroundService);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.intervals, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        settingsBackgroundServiceSpinner.setAdapter(adapter);
        settingsBackgroundServiceSpinner.setOnItemSelectedListener(this);
        switch (interval) {
            case 1000:
                settingsBackgroundServiceSpinner.setSelection(0);
                break;
            case 10000:
                settingsBackgroundServiceSpinner.setSelection(1);
                break;
            case 60000:
                settingsBackgroundServiceSpinner.setSelection(2);
                break;
            case 120000:
                settingsBackgroundServiceSpinner.setSelection(3);
                break;
        }
    }
}
