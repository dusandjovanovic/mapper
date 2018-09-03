package com.dushan.dev.mapper.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.dushan.dev.mapper.R;

public class NewMarkerDetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner newMarkerDetailSpinner;
    Button newMarkerDetailShareButton;
    EditText newMarkerDetailNameText, newMarkerDetailDescriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_marker_detail);

        connectViews();
    }

    private void connectViews() {
        newMarkerDetailSpinner = findViewById(R.id.newMarkerDetailSpinner);
        newMarkerDetailNameText = findViewById(R.id.newMarkerDetailNameText);
        newMarkerDetailDescriptionText = findViewById(R.id.newMarkerDetailDescriptionText);
        newMarkerDetailShareButton = findViewById(R.id.newMarkerDetailShareButton);
        initiateActivity();
    }

    private void initiateActivity() {
        newMarkerDetailShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityIntent = new Intent(NewMarkerDetailActivity.this, HomeActivity.class);
                startActivity(activityIntent);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newMarkerDetailSpinner.setAdapter(adapter);
        newMarkerDetailSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
