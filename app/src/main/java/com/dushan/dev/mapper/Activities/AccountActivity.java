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

import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    private TextView accountName, accountLastName;

    private String name, lastName;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectViews();
        initiateActivity();
    }

    private void connectViews() {
        accountName = findViewById(R.id.accountName);
        accountLastName = findViewById(R.id.accountLastName);
    }

    private void initiateActivity() {
        accountName.setText(name);
        accountLastName.setText(lastName);
    }
}
