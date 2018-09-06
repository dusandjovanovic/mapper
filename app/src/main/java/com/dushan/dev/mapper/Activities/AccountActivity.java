package com.dushan.dev.mapper.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.dushan.dev.mapper.R;

import java.text.DecimalFormat;
import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    private TextView accountName, accountLastName, accountPhone, accountEmail, accountLocation, accountAbout;
    private String name, lastName, phone, email, about;
    private double latitude, longitude;
    private int reach;

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
        about = extras.getString("about");
        reach = extras.getInt("reach", 0);

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
    }

    private void initiateActivity() {
        accountName.setText(name);
        accountLastName.setText(lastName);
        accountPhone.setText(phone);
        accountEmail.setText(email);
        accountLocation.setText(new DecimalFormat(".## ").format(latitude) + "/ " + new DecimalFormat(".##").format(longitude));
        accountAbout.setText(about);
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
