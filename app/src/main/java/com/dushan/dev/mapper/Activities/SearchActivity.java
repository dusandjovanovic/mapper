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
import android.widget.TextView;

import com.dushan.dev.mapper.R;

import java.util.Objects;

public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private SharedPreferences sharedPref;
    private Toolbar toolbar;

    private String userId;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        sharedPref = getSharedPreferences("mapper", MODE_PRIVATE);
        userId = sharedPref.getString("userId", null);
        email = sharedPref.getString("email", null);

        toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.searchDrawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.searchNavView);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.navigationDrawerEmail);
        navUsername.setText(email);
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
}
