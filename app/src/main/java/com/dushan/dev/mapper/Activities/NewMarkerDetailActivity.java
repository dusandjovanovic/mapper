package com.dushan.dev.mapper.Activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dushan.dev.mapper.Data.Marker;
import com.dushan.dev.mapper.Data.MarkerData;
import com.dushan.dev.mapper.Data.UserData;
import com.dushan.dev.mapper.Interfaces.GlideApp;
import com.dushan.dev.mapper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Locale;

public class NewMarkerDetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String TAG = "NEW_MARKER_DETAIL_ACTIVITY";

    Spinner newMarkerDetailSpinner;
    Button newMarkerDetailShareButton;
    EditText newMarkerDetailNameText, newMarkerDetailDescriptionText;
    ImageView newMarkerDetailImage;
    private String imageURL;
    private String category = "Travel";
    private String address = "";
    private double latitude;
    private double longitude;

    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;

    private MarkerData markerData;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_marker_detail);

        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        markerData = MarkerData.getInstance(mAuth.getUid());
        userData = UserData.getInstance(mAuth.getUid());

        Intent intent = getIntent();
        imageURL = intent.getStringExtra("imageURL");
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);
        address = getCompleteAddressString(latitude, longitude);

        connectViews();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        category = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        category = "Travel";
    }

    private void connectViews() {
        newMarkerDetailSpinner = findViewById(R.id.newMarkerDetailSpinner);
        newMarkerDetailNameText = findViewById(R.id.newMarkerDetailNameText);
        newMarkerDetailDescriptionText = findViewById(R.id.newMarkerDetailDescriptionText);
        newMarkerDetailShareButton = findViewById(R.id.newMarkerDetailShareButton);
        newMarkerDetailImage = findViewById(R.id.newMarkerDetailImage);
        initiateActivity();
    }

    private void initiateActivity() {
        StorageReference storageReference = mStorage.getReferenceFromUrl(imageURL);
        GlideApp.with(NewMarkerDetailActivity.this)
                .load(storageReference)
                .into(newMarkerDetailImage);

        newMarkerDetailShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInputs()) {
                    Marker marker = new Marker(newMarkerDetailNameText.getText().toString(),
                            address,
                            category,
                            userData.getUser().getName(),
                            newMarkerDetailDescriptionText.getText().toString(),
                            imageURL,
                            latitude,
                            longitude,
                            System.currentTimeMillis());
                    markerData.addNewMarker(marker);
                    Intent activityIntent = new Intent(NewMarkerDetailActivity.this, HomeActivity.class);
                    startActivity(activityIntent);
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newMarkerDetailSpinner.setAdapter(adapter);
        newMarkerDetailSpinner.setOnItemSelectedListener(this);
    }

    private boolean validateInputs() {
        boolean validation = true;
        if (newMarkerDetailNameText.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Make sure to add a name!", Toast.LENGTH_SHORT).show();
            validation = false;
        }
        else if (newMarkerDetailDescriptionText.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Enter a brief description!", Toast.LENGTH_SHORT).show();
            validation = false;
        }

        return validation;
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            } else {
                Log.w(TAG, "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "Unable to get Address!");
        }
        return strAdd;
    }
}
