package com.dushan.dev.mapper.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.dushan.dev.mapper.R;

import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    private TextView accountName, accountLastName, accountPhone, accountEmail, accountLocation, accountAbout, accountReachImpact;
    private String name, lastName, phone, email, about;
    private double latitude, longitude;
    private long reachImpact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.accountToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        Bundle extras = getIntent().getExtras();
        name = extras.getString("name");
        lastName = extras.getString("lastName");
        phone = extras.getString("phone");
        email = extras.getString("email");
        latitude = extras.getDouble("latitude", 0);
        longitude = extras.getDouble("longitude", 0);
        reachImpact = extras.getLong("reachImpact", 0);
        about = extras.getString("about");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        connectViews();
        initiateActivity();
    }

    private void connectViews() {
        accountName = findViewById(R.id.accountName);
        accountLastName = findViewById(R.id.accountLastName);
        accountPhone = findViewById(R.id.accountPhone);
        accountEmail = findViewById(R.id.accountEmail);
        accountLocation = findViewById(R.id.accountLocation);
        accountAbout = findViewById(R.id.accountAbout);
        accountReachImpact = findViewById(R.id.accountReachImpact);
    }

    private void initiateActivity() {
        accountName.setText(name);
        accountLastName.setText(lastName);
        accountPhone.setText(phone);
        accountEmail.setText(email);
        accountAbout.setText(about);
        accountReachImpact.setText("User has Reach impact " + String.valueOf(reachImpact));
        accountLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityIntent = new Intent(AccountActivity.this, MapsActivity.class);
                activityIntent.putExtra("latitude", latitude);
                activityIntent.putExtra("longitude", longitude);
                startActivity(activityIntent);
            }
        });
    }
}
