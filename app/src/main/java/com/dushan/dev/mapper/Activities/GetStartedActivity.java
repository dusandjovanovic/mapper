package com.dushan.dev.mapper.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dushan.dev.mapper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GetStartedActivity extends AppCompatActivity {

    private String TAG = "GET_STARTED_ACTIVITY";
    private FirebaseAuth mAuth;
    ImageView getStartedPreviewImage;
    TextView getStartedAddPhoto;
    EditText getStartedNameEditText, getStartedLastNameEditText,
             getStartedPasswordEditText, getStartedEmailEditText,
             getStartedPhoneEditText;
    Button getStartedNextButton;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        mAuth = FirebaseAuth.getInstance();
        connectViews();
    }

    private void connectViews() {
        getStartedAddPhoto = findViewById(R.id.getStartedAddPhoto);
        getStartedPreviewImage = findViewById(R.id.getStartedPreviewImage);
        getStartedNameEditText = findViewById(R.id.getStartedNameEditText);
        getStartedLastNameEditText = findViewById(R.id.getStartedLastNameEditText);
        getStartedPasswordEditText = findViewById(R.id.getStartedPasswordEditText);
        getStartedEmailEditText = findViewById(R.id.getStartedEmailEditText);
        getStartedPhoneEditText = findViewById(R.id.getStartedPhoneEditText);
        getStartedNextButton = findViewById(R.id.getStartedNextButton);
        setUpListeners();
    }

    private void setUpListeners() {
        getStartedAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        getStartedNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()) {
                    String email = getStartedEmailEditText.getText().toString();
                    String password = getStartedPasswordEditText.getText().toString();
                    createAcount(email, password);
                }
            }
        });
    }

    private void createAcount(String email, String password) {
        progressDialog = new ProgressDialog(GetStartedActivity.this);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            Intent mainActivityIntent = new Intent(GetStartedActivity.this, HomeActivity.class);
                            startActivity(mainActivityIntent);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(GetStartedActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateInputs() {
        boolean validation = true;
        if (getStartedEmailEditText.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            validation = false;
        }
        else if (getStartedPasswordEditText.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            validation = false;
        }
        else if (getStartedPasswordEditText.getText().toString().length() < 5) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 5 characters!", Toast.LENGTH_SHORT).show();
            validation = false;
        }

        return validation;
    }
}
