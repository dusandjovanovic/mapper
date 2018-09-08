package com.dushan.dev.mapper.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dushan.dev.mapper.Adapters.ReachListAdapter;
import com.dushan.dev.mapper.Data.SocialData;
import com.dushan.dev.mapper.Data.User;
import com.dushan.dev.mapper.Data.UserData;
import com.dushan.dev.mapper.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class StatisticsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private SharedPreferences sharedPref;
    private Toolbar toolbar;
    private LineChart mChart;
    private RecyclerView recyclerView;

    private String userId;
    private String email;
    private UserData userData;
    private SocialData socialData;

    private ReachListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        sharedPref = getSharedPreferences("mapper", MODE_PRIVATE);
        userId = sharedPref.getString("userId", null);
        email = sharedPref.getString("email", null);
        userData = UserData.getInstance(userId);
        socialData = SocialData.getInstance(userId, getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.statisticsToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.statisticsDrawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        connectViews();
        initiateChart();
        setAdapter();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.statisticsDrawer);
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
                Intent activityIntent = new Intent(StatisticsActivity.this, HomeActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationDiscover: {
                Intent activityIntent = new Intent(StatisticsActivity.this, DiscoverActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationFriends: {
                Intent activityIntent = new Intent(StatisticsActivity.this, FriendsActivity.class);
                startActivity(activityIntent);
                break;
            }
            case R.id.navigationSettings: {
                Intent activityIntent = new Intent(StatisticsActivity.this, SettingsActivity.class);
                startActivity(activityIntent);
                break;
            }
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.statisticsDrawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setAdapter() {
        mAdapter = new ReachListAdapter(StatisticsActivity.this, initiateList());
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(StatisticsActivity.this, R.drawable.divider_recyclerview));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new ReachListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, User model) {
                return;
            }
        });
    }

    private ArrayList<User> initiateList() {
        ArrayList<User> reachList = new ArrayList<User>();
        reachList.add(userData.getUser());
        reachList.addAll(socialData.getSocialFriends());
        Collections.sort(reachList, new Comparator<User>(){
            public int compare(User o1, User o2){
                if(o1.getReachImpact() == o2.getReachImpact())
                    return 0;
                return o1.getReachImpact() > o2.getReachImpact() ? -1 : 1;
            }
        });
        return reachList;
    }

    private void initiateChart() {
        mChart = findViewById(R.id.statisticsChart);
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        mChart.setBackgroundColor(Color.WHITE);
        mChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        setData(24, 30);
        mChart.invalidate();
        Legend l = mChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(69, 98, 69));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(22f);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(69, 98, 69));

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void setData(int count, float range) {
        long before24h = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis()) - 24;
        ArrayList<Entry> values = new ArrayList<Entry>();
        float from = before24h;
        float to = before24h + count;
        for (float x = from; x < to; x++) {
            float y = byHour(x);
            values.add(new Entry(x, y));
        }

        LineDataSet set1 = new LineDataSet(values, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.rgb(60, 240, 190));
        set1.setValueTextColor(Color.rgb(60, 240, 190));
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.rgb(60, 240, 190));
        set1.setHighLightColor(Color.rgb(144, 117, 117));
        set1.setDrawCircleHole(false);

        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        mChart.setData(data);
    }

    private float byHour(float dateTime) {
        float byHour = 0;
        for (long marker: userData.getUserReach()) {
            float markerTime = TimeUnit.MILLISECONDS.toHours(marker);
            if (markerTime == dateTime )
                byHour++;
        }

        return byHour;
    }

    private void connectViews() {
        recyclerView = findViewById(R.id.statisticsRecyclerView);
        NavigationView navigationView = (NavigationView) findViewById(R.id.statisticsNavView);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.navigationDrawerEmail);
        navUsername.setText(email);
    }
}
