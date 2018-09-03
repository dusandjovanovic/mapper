package com.dushan.dev.mapper.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.dushan.dev.mapper.R;

public class NewMarkerActivity extends AppCompatActivity {

    Button newMarkerNextButton, newMarkerBrowseCaptureButton;
    CheckBox newMarkerCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_marker);

        connectViews();
    }

    private void connectViews() {
        newMarkerNextButton = findViewById(R.id.newMarkerNextButton);
        newMarkerBrowseCaptureButton = findViewById(R.id.newMarkerBrowseCaptureButton);
        newMarkerCheckBox = findViewById(R.id.newMarkerCheckBox);
        initiateActivity();
    }

    private void initiateActivity() {
        newMarkerNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityIntent = new Intent(NewMarkerActivity.this, NewMarkerDetailActivity.class);
                startActivity(activityIntent);
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.newMarkerPhotoRadio:
                if (checked)

                    break;
            case R.id.newMarkerVideoRadio:
                if (checked)

                    break;
            case R.id.newMarkerAudioRadio:
                if (checked)

                    break;
        }
    }
}
