package com.dushan.dev.mapper.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dushan.dev.mapper.Adapters.BluetoothDiscoveryAdapter;
import com.dushan.dev.mapper.Adapters.CheckboxListAdapter;
import com.dushan.dev.mapper.Data.SocialData;
import com.dushan.dev.mapper.Data.User;
import com.dushan.dev.mapper.Data.UserData;
import com.dushan.dev.mapper.R;
import com.dushan.dev.mapper.Threads.BluetoothReceiverService;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FriendsAddActivity extends AppCompatActivity {

    private final int SEND_TAB = 1;
    private final int PENDING_TAB = 2;
    private int selectedTab;

    private SharedPreferences sharedPref;
    private BluetoothAdapter mBluetoothAdapter;

    private List<BluetoothDevice> bluetoothDevices;
    private BluetoothServerSocket mmServerSocket;


    private Toolbar toolbar;
    private TextView sendTab, pendingTab;
    private View sendHighlight, pendingHighlight;
    private RecyclerView recyclerView;
    private CheckboxListAdapter mAdapter;
    private BluetoothDiscoveryAdapter bluetoothDiscoveryAdapter;
    private SwipeRefreshLayout swipeRefreshRecyclerList;

    private UserData userData;
    private SocialData socialData;
    private FirebaseAuth mAuth;
    private String userId;

    private ArrayList<User> modelList = new ArrayList<>();
    private ArrayList<User> checkedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_add);
        checkedList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        userData = UserData.getInstance(userId);
        socialData = SocialData.getInstance(userId, getApplicationContext());

        sharedPref = getSharedPreferences("mapper", MODE_PRIVATE);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        bluetoothDevices = new ArrayList<>();

        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
        toolbar = (Toolbar) findViewById(R.id.friendsAddToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        selectedTab = SEND_TAB;
        connectViews();
        initializeRecyclerView();
        setAdapter();
        Intent brs = new Intent(this, BluetoothReceiverService.class);
        startService(brs);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null){
           startDiscovery();

        } else {
            Toast.makeText(getApplicationContext(), "Cannot use bluetooth services bla bla", Toast.LENGTH_SHORT).show();
        }
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
            for (User user : checkedList){
                socialData.acceptUserRequest(user.getKey());
            }
            checkedList.clear();
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

    private void startDiscovery(){
        mBluetoothAdapter.startDiscovery();
    }



    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(getApplicationContext(), device.getName(), Toast.LENGTH_SHORT).show();
                bluetoothDevices.add(device);
                setSendAdapter();
            }
        }
    };

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            UUID uuid = UUID.fromString(getApplicationContext().getResources().getString(R.string.BASE_UUID));
            try {
                tmp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {
            mBluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }
            String uid = "";
            try {
                 byte[] recievedArray = new byte[28];
                 mmSocket.getInputStream().read(recievedArray);
                uid = new String(recievedArray);
                List<User> users = socialData.getSocialRequests();
                boolean requestSent = false;
                for (User user: users){
                    if (user.getKey().equals(uid)) {
                        requestSent = true;
                        break;
                    }
                }
                if (!requestSent) {
                    socialData.sendUserRequest(uid);
                }
            } catch (IOException ex){

            }
            Toast.makeText(getApplicationContext(), "client socket works, number is " + uid
                     , Toast.LENGTH_SHORT).show();
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
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
                setSendAdapter();
                repaintTabs();
            }
        });

        pendingTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab = PENDING_TAB;
                setAdapter();
                repaintTabs();
            }
        });
    }

    private void initializeRecyclerView(){
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(FriendsAddActivity.this, R.drawable.divider_recyclerview));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setSendAdapter(){
        if (bluetoothDiscoveryAdapter == null) {
            bluetoothDiscoveryAdapter = new BluetoothDiscoveryAdapter(getApplicationContext(), bluetoothDevices);
            recyclerView.setAdapter(bluetoothDiscoveryAdapter);
            bluetoothDiscoveryAdapter.SetOnItemClickListener(new BluetoothDiscoveryAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position, BluetoothDevice model) {
                    ConnectThread thred = new ConnectThread(model);
                    thred.run();
                }
            });

        } else {
            bluetoothDiscoveryAdapter.updateList(bluetoothDevices);
            recyclerView.setAdapter(bluetoothDiscoveryAdapter);
        }

    }

    private void setAdapter() {
        mAdapter = new CheckboxListAdapter(FriendsAddActivity.this, socialData.getSocialRequests());
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

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

}
