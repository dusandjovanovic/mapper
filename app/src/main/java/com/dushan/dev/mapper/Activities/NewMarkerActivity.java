package com.dushan.dev.mapper.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

import com.dushan.dev.mapper.R;
import com.dushan.dev.mapper.Services.CloudUploadService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;

public class NewMarkerActivity extends AppCompatActivity {
    private final int MARKER_PHOTO = 0;
    private final int MARKER_PHOTO_CAPTURE = 1;
    private final int MARKER_VIDEO = 2;
    private int mode;

    Button newMarkerNextButton, newMarkerBrowseCaptureButton;
    CheckBox newMarkerCheckBox;
    ProgressDialog progressDialog;

    private static final String TAG = "NEW_MARKER_ACTIVITY";
    private static final int RC_TAKE_PICTURE = 101;
    private static final int RC_CAPTURE_PICTURE = 102;
    private static final int RC_TAKE_VIDEO = 103;

    private static final String KEY_FILE_URI = "key_file_uri";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";

    private BroadcastReceiver mBroadcastReceiver;
    private Uri mDownloadUrl = null;
    private Uri mCapturedUri = null;
    private Uri mFileUri = null;

    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng lastKnowLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_marker);

        mode = MARKER_PHOTO;

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
                        }
                    }
                });

        connectViews();
    }

    private void connectViews() {
        newMarkerNextButton = findViewById(R.id.newMarkerNextButton);
        newMarkerBrowseCaptureButton = findViewById(R.id.newMarkerBrowseCaptureButton);
        newMarkerCheckBox = findViewById(R.id.newMarkerCheckBox);
        initiateActivity();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(CloudUploadService.EXTRA_DOWNLOAD_URL)) {
            onUploadResultIntent(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mBroadcastReceiver, CloudUploadService.getIntentFilter());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                mFileUri = data.getData();
                if (mFileUri != null) {
                    uploadFromUri(mFileUri);
                } else {
                    Log.w(TAG, "File URI is null");
                }
            } else {
                Toast.makeText(this, "Taking picture failed.", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == RC_CAPTURE_PICTURE) {
            if (resultCode == RESULT_OK) {
                mFileUri = mCapturedUri;
                if (mFileUri != null) {
                    uploadFromUri(mFileUri);
                } else {
                    Log.w(TAG, "File URI is null");
                }
            }
        }
        else if (requestCode == RC_TAKE_VIDEO) {
            if (resultCode == RESULT_OK) {
                mFileUri = data.getData();
                if (mFileUri != null) {
                    uploadFromUri(mFileUri);
                } else {
                    Log.w(TAG, "File URI is null");
                }
            } else {
                Toast.makeText(this, "Taking picture failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFromUri(Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());
        mFileUri = fileUri;
        mDownloadUrl = null;

        startService(new Intent(this, CloudUploadService.class)
                .putExtra(CloudUploadService.EXTRA_FILE_URI, fileUri)
                .setAction(CloudUploadService.ACTION_UPLOAD));

        showProgressDialog(getString(R.string.progress_uploading));
    }

    private void onUploadResultIntent(Intent intent) {
        Log.d(TAG, "onUploadBackgroundServiceIntent: " + intent.getParcelableExtra(CloudUploadService.EXTRA_DOWNLOAD_URL));
        mFileUri = intent.getParcelableExtra(CloudUploadService.EXTRA_FILE_URI);
        mDownloadUrl = intent.getParcelableExtra(CloudUploadService.EXTRA_DOWNLOAD_URL);
    }

    private void showProgressDialog(String caption) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
        }

        progressDialog.setMessage(caption);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void capturePhoto() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, RC_TAKE_PICTURE);
    }

    private void capturePhotoCamera() {
        mCapturedUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"fname_" +
                String.valueOf(System.currentTimeMillis()) + ".jpg"));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedUri);
        startActivityForResult(intent, RC_CAPTURE_PICTURE);
    }

    private void captureVideo() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("video/*");
        startActivityForResult(intent, RC_TAKE_VIDEO);
    }

    private void initiateActivity() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive:" + intent);
                hideProgressDialog();

                switch (intent.getAction()) {
                    case CloudUploadService.UPLOAD_COMPLETED:
                    case CloudUploadService.UPLOAD_ERROR:
                        onUploadResultIntent(intent);
                        break;
                }
            }
        };

        newMarkerNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()) {
                    Intent activityIntent = new Intent(NewMarkerActivity.this, NewMarkerDetailActivity.class);
                    activityIntent.putExtra("imageURL", mDownloadUrl.toString());
                    activityIntent.putExtra("latitude", lastKnowLocation.latitude);
                    activityIntent.putExtra("longitude", lastKnowLocation.longitude);
                    startActivity(activityIntent);
                }
            }
        });

        newMarkerBrowseCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == MARKER_PHOTO) {
                    capturePhoto();
                }
                else if (mode == MARKER_PHOTO_CAPTURE) {
                    capturePhotoCamera();
                }
                else
                    captureVideo();
            }
        });
    }

    private boolean validateInputs() {
        boolean validation = true;
        if (mDownloadUrl == null) {
            Toast.makeText(getApplicationContext(), "Add a photo or short video!", Toast.LENGTH_SHORT).show();
            validation = false;
        }
        else if (!newMarkerCheckBox.isChecked()) {
            Toast.makeText(getApplicationContext(), "Make sure to accept location sharing!", Toast.LENGTH_SHORT).show();
            validation = false;
        }

        return validation;
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.newMarkerPhotoRadio:
                if (checked)
                    mode = MARKER_PHOTO;
                    break;
            case R.id.newMarkerPhotoCameraRadio:
                if (checked)
                    mode = MARKER_PHOTO_CAPTURE;
                    break;
            default:
                    break;
        }
    }
}
