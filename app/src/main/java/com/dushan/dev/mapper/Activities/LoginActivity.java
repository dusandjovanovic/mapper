package com.dushan.dev.mapper.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.dushan.dev.mapper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private String TAG = "LOGIN_ACTIVITY";
    private FirebaseAuth mAuth;
    private Button loginNextButton;
    private EditText loginEmailEditText, loginPasswordEditText;
    private ProgressDialog progressDialog;
    private CheckBox loginRememberMe;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        connectViews();

        sharedPref = getSharedPreferences("mapper", MODE_PRIVATE);
        String userId = sharedPref.getString("userId", "undefined");
        if (userId != "undefined") {
            String email = sharedPref.getString("email", "undefined");
            String password = sharedPref.getString("password", "undefined");
            signIn(email, password);
        }
    }

    private void connectViews() {
        loginNextButton = findViewById(R.id.loginNextButton);
        loginEmailEditText = findViewById(R.id.loginEmailEditText);
        loginPasswordEditText = findViewById(R.id.loginPasswordEditText);
        loginRememberMe = findViewById(R.id.loginRememberMe);
        setUpListeners();
    }

    private void setUpListeners() {
        loginNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()) {
                    String email = loginEmailEditText.getText().toString();
                    String password = loginPasswordEditText.getText().toString();
                    signIn(email, password);
                }
            }
        });
    }

    private void signIn(String email, String password) {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (loginRememberMe.isChecked()) {
            FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    Log.d(TAG, "authState:changed");
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        String userId = firebaseUser.getUid();
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("userId", userId);
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.commit();
                    }
                }
            };
            mAuth.addAuthStateListener(authListener);
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            Intent mainActivityIntent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(mainActivityIntent);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateInputs() {
        boolean validation = true;
        if (loginEmailEditText.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            validation = false;
        }
        else if (loginPasswordEditText.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            validation = false;
        }
        else if (loginPasswordEditText.getText().toString().length() < 5) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 5 characters!", Toast.LENGTH_SHORT).show();
            validation = false;
        }

        return validation;
    }
}
