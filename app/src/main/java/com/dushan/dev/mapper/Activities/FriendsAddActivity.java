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
import java.io.IOException;

import com.dushan.dev.mapper.Adapters.BluetoothDiscoveryAdapter;
import com.dushan.dev.mapper.Adapters.CheckboxListAdapter;
import com.dushan.dev.mapper.Adapters.MarkersAdapter;
import com.dushan.dev.mapper.Adapters.SimpleListAdapter;
import com.dushan.dev.mapper.Data.BluetoothDeviceInstance;
import com.dushan.dev.mapper.Data.MarkerData;
import com.dushan.dev.mapper.Data.SavedMarkerData;
import com.dushan.dev.mapper.Data.Social;
import com.dushan.dev.mapper.Data.SocialData;
import com.dushan.dev.mapper.Data.User;
import com.dushan.dev.mapper.Data.UserData;
import com.dushan.dev.mapper.R;
import com.google.firebase.auth.FirebaseAuth;

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

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        userData = UserData.getInstance(userId);
        socialData = SocialData.getInstance(userId, getApplicationContext());

        sharedPref = getSharedPreferences("mapper", MODE_PRIVATE);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        bluetoothDevices = new ArrayList<>();


        toolbar = (Toolbar) findViewById(R.id.friendsAddToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        selectedTab = SEND_TAB;
        connectViews();
        initializeRecyclerView();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null){
            startDiscovery();
      /*      */
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

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
              //  manageMyConnectedSocket(socket);
                Toast.makeText(getApplicationContext(), "it works! sockets!", Toast.LENGTH_SHORT).show();
                try {
                    mmServerSocket.close();
                } catch (IOException ex){

                }
                break;
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {

        }
    }



    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(getApplicationContext(), device.getName(), Toast.LENGTH_SHORT).show();
               // String deviceName = device.getName();
               // String deviceHardwareAddress = device.getAddress();// MAC address
              //  BluetoothDeviceInstance instance = new BluetoothDeviceInstance(deviceName, deviceHardwareAddress);
                bluetoothDevices.add(device);
                setSendAdapter();
                BluetoothServerSocket tmp = null;
                try {
                    // MY_UUID is the app's UUID string, also used by the client code.
                    UUID uuid = UUID.fromString(getApplicationContext().getResources().getString(R.string.BASE_UUID));
                    tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(getApplicationContext().getResources().getString(R.string.app_name), uuid);
                } catch (IOException e) {
                }
                mmServerSocket = tmp;
                run();
            }
        }
    };


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
                    BluetoothSocket tmp = null;
                    try {
                        UUID uuid = UUID.fromString(getApplicationContext().getResources().getString(R.string.BASE_UUID));
                        tmp = model.createRfcommSocketToServiceRecord(uuid);
                    } catch (IOException ex){

                    }
                    BluetoothSocket socket = tmp;
                    try {
                        // Connect to the remote device through the socket. This call blocks
                        // until it succeeds or throws an exception.
                        socket.connect();
                    } catch (IOException connectException) {
                        // Unable to connect; close the socket and return.
                        try {
                            socket.close();
                        } catch (IOException closeException) {

                        }
                        return;
                    }

                    Toast.makeText(getApplicationContext(), "socket is made", Toast.LENGTH_SHORT).show();

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
