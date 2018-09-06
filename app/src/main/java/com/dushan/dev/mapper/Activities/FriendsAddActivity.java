package com.dushan.dev.mapper.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dushan.dev.mapper.Adapters.CheckboxListAdapter;
import com.dushan.dev.mapper.Adapters.MarkersAdapter;
import com.dushan.dev.mapper.Adapters.SimpleListAdapter;
import com.dushan.dev.mapper.Data.MarkerData;
import com.dushan.dev.mapper.Data.SavedMarkerData;
import com.dushan.dev.mapper.Data.User;
import com.dushan.dev.mapper.Data.UserData;
import com.dushan.dev.mapper.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class FriendsAddActivity extends AppCompatActivity {

    private final int SEND_TAB = 1;
    private final int PENDING_TAB = 2;
    private int selectedTab;

    private SharedPreferences sharedPref;

    private Toolbar toolbar;
    private TextView sendTab, pendingTab;
    private View sendHighlight, pendingHighlight;
    private RecyclerView recyclerView;
    private CheckboxListAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshRecyclerList;

    private UserData userData;
    private FirebaseAuth mAuth;
    private String userId;

    private ArrayList<User> modelList = new ArrayList<>();
    private ArrayList<User> checkedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_add);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        userData = UserData.getInstance(userId);
        sharedPref = getSharedPreferences("mapper", MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.friendsAddToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        selectedTab = SEND_TAB;
        connectViews();
        setAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friends_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.friendsAddAction) {

            return true;
        }
        else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void connectViews() {
        sendTab = findViewById(R.id.sendTab);
        pendingTab = findViewById(R.id.pendingTab);
        sendHighlight = findViewById(R.id.sendHighlight);
        pendingHighlight = findViewById(R.id.pendingHighlight);
        recyclerView = findViewById(R.id.friendsAddRecyclerView);
        swipeRefreshRecyclerList = findViewById(R.id.friendsAddSwipeRefreshLayout);
        setUpListeners();
    }

    private void setUpListeners() {
        swipeRefreshRecyclerList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (swipeRefreshRecyclerList.isRefreshing())
                            swipeRefreshRecyclerList.setRefreshing(false);
                    }
                }, 2000);

            }
        });

        sendTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab = SEND_TAB;
                repaintTabs();
            }
        });

        pendingTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab = PENDING_TAB;
                repaintTabs();
            }
        });
    }

    private void setAdapter() {
        modelList.add(userData.getUser());

        mAdapter = new CheckboxListAdapter(FriendsAddActivity.this, modelList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(FriendsAddActivity.this, R.drawable.divider_recyclerview));
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(mAdapter);

        mAdapter.SetOnCheckedListener(new CheckboxListAdapter.OnCheckedListener() {
            @Override
            public void onChecked(View view, boolean isChecked, int position, User model) {
                if (isChecked)
                    checkedList.add(modelList.get(position));
                else
                    checkedList.remove(modelList.get(position));
            }
        });
    }

    private void repaintTabs(){
        sendTab.setTextColor(getApplicationContext().getResources().getColor(R.color.unselectedTabTextColor));
        pendingTab.setTextColor(getApplicationContext().getResources().getColor(R.color.unselectedTabTextColor));
        sendHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
        pendingHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
        switch (selectedTab) {
            case (SEND_TAB):
                sendTab.setTextColor(getApplicationContext().getResources().getColor(R.color.altTextColor));
                sendHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.tabHighlightColor));
                break;
            case (PENDING_TAB):
                pendingTab.setTextColor(getApplicationContext().getResources().getColor(R.color.altTextColor));
                pendingHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.tabHighlightColor));
                break;
            default:
                break;
        }
    }
}
