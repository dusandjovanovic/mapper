package com.dushan.dev.mapper.Activities;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.dushan.dev.mapper.R;

public class HomePageActivity extends AppCompatActivity {

    private Button homePageGetStartedBtn, homePageSignInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getApplicationContext().getResources().getColor(R.color.homePageStatusBarColor));
        }
        connectViews();
    }

    private void connectViews() {
        homePageGetStartedBtn = findViewById(R.id.homePageGetStartedBtn);
        homePageSignInBtn = findViewById(R.id.homePageSignInBtn);
        setUpListeners();
    }

    private void setUpListeners() {
        homePageGetStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getStartedIntent = new Intent(HomePageActivity.this, GetStartedActivity.class);
                startActivity(getStartedIntent);
            }
        });
        homePageSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(HomePageActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}
