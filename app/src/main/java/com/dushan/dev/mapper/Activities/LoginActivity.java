package com.dushan.dev.mapper.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dushan.dev.mapper.R;

public class LoginActivity extends AppCompatActivity {

    private Button loginNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        connectViews();
    }

    private void connectViews() {
        loginNextButton = findViewById(R.id.loginNextButton);
        setUpListeners();
    }

    private void setUpListeners(){
        loginNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainActivityIntent);
            }
        });
    }
}
