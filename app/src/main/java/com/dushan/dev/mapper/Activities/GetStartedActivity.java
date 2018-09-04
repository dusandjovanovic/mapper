package com.dushan.dev.mapper.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dushan.dev.mapper.Data.UserData;
import com.dushan.dev.mapper.Interfaces.GlideApp;
import com.dushan.dev.mapper.R;
import com.dushan.dev.mapper.Services.CloudUploadService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

public class GetStartedActivity extends AppCompatActivity {

    private static final String TAG = "GET_STARTED_ACTIVITY";
    private static final int RC_TAKE_PICTURE = 101;

    private static final String KEY_FILE_URI = "key_file_uri";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";

    private BroadcastReceiver mBroadcastReceiver;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;

    private UserData userData;
    private Uri mDownloadUrl = null;
    private Uri mFileUri = null;
    private String email;

    private ImageView getStartedPreviewImage;
    private TextView getStartedAddPhoto;
    private EditText getStartedNameEditText, getStartedLastNameEditText, getStartedAboutEditText, getStartedPhoneEditText;
    private Button getStartedNextButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        userData = UserData.getInstance(mAuth.getCurrentUser().getUid());
        email = mAuth.getCurrentUser().getEmail();

        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(KEY_FILE_URI);
            mDownloadUrl = savedInstanceState.getParcelable(KEY_DOWNLOAD_URL);
        }
        onNewIntent(getIntent());
        initiateActivity();

        connectViews();
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
        StorageReference storageReference = mStorage.getReferenceFromUrl(mDownloadUrl.toString());
        GlideApp.with(GetStartedActivity.this)
                .load(storageReference)
                .into(getStartedPreviewImage);
    }

    private void launchCamera() {
        Log.d(TAG, "launchCamera");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, RC_TAKE_PICTURE);
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
    }

    private void connectViews() {
        getStartedAddPhoto = findViewById(R.id.getStartedAddPhoto);
        getStartedPreviewImage = findViewById(R.id.getStartedPreviewImage);
        getStartedNameEditText = findViewById(R.id.getStartedNameEditText);
        getStartedLastNameEditText = findViewById(R.id.getStartedLastNameEditText);
        getStartedAboutEditText = findViewById(R.id.getStartedAboutEditText);
        getStartedPhoneEditText = findViewById(R.id.getStartedPhoneEditText);
        getStartedNextButton = findViewById(R.id.getStartedNextButton);
        setUpListeners();
    }

    private void setUpListeners() {
        getStartedAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        getStartedNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInputs()) {
                    userData.registerUser(email
                            , getStartedNameEditText.getText().toString()
                            , getStartedLastNameEditText.getText().toString()
                            , getStartedAboutEditText.getText().toString()
                            , getStartedPhoneEditText.getText().toString()
                            , mDownloadUrl);
                    Intent activityIntent = new Intent(GetStartedActivity.this, HomeActivity.class);
                    startActivity(activityIntent);
                }
            }
        });
    }

    private boolean validateInputs() {
        boolean validation = true;
        if (getStartedNameEditText.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Enter name!", Toast.LENGTH_SHORT).show();
            validation = false;
        }
        else if (getStartedAboutEditText.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Enter something short about you!", Toast.LENGTH_SHORT).show();
            validation = false;
        }
        else if (getStartedPhoneEditText.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Enter your phone number!", Toast.LENGTH_SHORT).show();
            validation = false;
        }
        else if (mDownloadUrl == null) {
            Toast.makeText(getApplicationContext(), "Add a photo!", Toast.LENGTH_SHORT).show();
            validation = false;
        }

        return validation;
    }
}
