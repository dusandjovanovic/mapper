package com.dushan.dev.mapper.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.dushan.dev.mapper.Data.Marker;
import com.dushan.dev.mapper.Data.SavedMarkerData;
import com.dushan.dev.mapper.R;
import com.dushan.dev.mapper.Services.CloudDownloadService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.Objects;

public class MarkerActivity extends AppCompatActivity {

    private static final String TAG = "MarkerActivity";
    private static final String KEY_FILE_URI = "key_download_url";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";

    private TextView markerNameText, markerAuthorText, markerDescriptionText, markerAddress, markerCategoryText, markerDateTimeText;
    private ImageView markerToolbarImage;
    private FloatingActionButton markerAddFavoriteButton;
    private Button markerGetDirectionsButton, markerVideoViewButton;
    private VideoView markerVideoView;

    Marker marker;
    SavedMarkerData savedData;

    private String userId;
    private FirebaseAuth mAuth;
    Bundle extras;

    private boolean markerVideo = false;
    private BroadcastReceiver mBroadcastReceiver;
    private ProgressDialog mProgressDialog;
    private Uri mDownloadUrl = null;
    private Uri mFileUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.markerToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        savedData = SavedMarkerData.getInstance(userId);

        extras = getIntent().getExtras();
        marker = new Marker(extras.getString("name"), extras.getString("address"), extras.getString("category"), extras.getString("author"), extras.getString("description"), extras.getString("imageURL"), extras.getDouble("latitude"), extras.getDouble("longitude"), extras.getLong("dateTime"));
        marker.setKey(extras.getString("markerKey"));
        marker.setAuthorKey(extras.getString("authorKey"));
        if (marker.getImageURL().contains("video"))
            markerVideo = true;

        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(KEY_FILE_URI);
            mDownloadUrl = savedInstanceState.getParcelable(KEY_DOWNLOAD_URL);
        }

        connectViews();
        updateViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mBroadcastReceiver, CloudDownloadService.getIntentFilter());
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putParcelable(KEY_FILE_URI, mFileUri);
        out.putParcelable(KEY_DOWNLOAD_URL, mDownloadUrl);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private void connectViews() {
        markerNameText = findViewById(R.id.markerNameText);
        markerAuthorText = findViewById(R.id.markerAuthorText);
        markerDescriptionText = findViewById(R.id.markerDescriptionText);
        markerAddress = findViewById(R.id.markerAddressText);
        markerCategoryText = findViewById(R.id.markerCategoryText);
        markerDateTimeText = findViewById(R.id.markerDateTimeText);
        markerAddFavoriteButton = findViewById(R.id.markerAddFavoriteButton);
        markerGetDirectionsButton= findViewById(R.id.markerGetDirectionsButton);
        markerToolbarImage = findViewById(R.id.markerToolbarImage);
        markerVideoViewButton = findViewById(R.id.markerVideoViewButton);
        markerVideoView = findViewById(R.id.markerVideoView);
        if (markerVideo)
            markerVideoViewButton.setVisibility(View.VISIBLE);
        setupListeners();
    }

    private void updateViews() {
        markerNameText.setText(marker.getName());
        markerAuthorText.setText(marker.getAuthor());
        markerDescriptionText.setText(marker.getDescription());
        markerAddress.setText(marker.getAddress());
        markerCategoryText.setText(marker.getCategory());
        markerDateTimeText.setText(new Date(marker.getDateTime() * 1000).toString());
        Glide.with(this).load(marker.getImageURL()).into(markerToolbarImage);
    }

    private void setupListeners(){
        markerAddFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedData.addNewMarker(marker);
            }
        });

        markerGetDirectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapsActivityIntent = new Intent(MarkerActivity.this, MapsActivity.class);
                mapsActivityIntent.putExtra("latitude", marker.getLatitude());
                mapsActivityIntent.putExtra("longitude", marker.getLongitude());
                startActivity(mapsActivityIntent);
            }
        });

        if (markerVideo) {
            markerVideoViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFileUri = Uri.parse(marker.getImageURL());
                    beginDownload();
                }
            });

            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, "onReceive:" + intent);
                    hideProgressDialog();

                    switch (intent.getAction()) {
                        case CloudDownloadService.DOWNLOAD_COMPLETED:
                            Uri uri = Uri.parse(intent.getStringExtra(CloudDownloadService.EXTRA_DOWNLOAD_PATH));
//                            File outFile = new File(getCacheDir(), uri.getLastPathSegment());
//                            markerVideoView.setVideoPath(outFile.getAbsolutePath());
                            MediaController mc = new MediaController(MarkerActivity.this);
                            markerVideoView.setMediaController(mc);
                            markerVideoView.setVideoURI(uri);
                            markerVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    markerVideoView.start();
                                }
                            });
//                            markerVideoView.requestFocus();

                            break;
                        case CloudDownloadService.DOWNLOAD_ERROR: break;
                    }
                }
            };
        }
    }

    private void beginDownload() {
        String path = mFileUri.getLastPathSegment();
        Intent intent = new Intent(this, CloudDownloadService.class)
                .putExtra(CloudDownloadService.EXTRA_DOWNLOAD_PATH, path)
                .setAction(CloudDownloadService.ACTION_DOWNLOAD);
        startService(intent);

        showProgressDialog(getString(R.string.progress_downloading));
    }

    private void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
