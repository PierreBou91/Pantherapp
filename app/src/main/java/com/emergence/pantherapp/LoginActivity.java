package com.emergence.pantherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    //define components
    //all the components start with the 'login' prefix
    Button login_ConnectButton;
    private TextView login_CreateTextView;
    private FirebaseAuth login_Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize components
        login_ConnectButton = findViewById(R.id.login_connect);
        login_CreateTextView = findViewById(R.id.login_sign_up);
        login_Auth = FirebaseAuth.getInstance();

        login_ConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start new Signup screen and finish the current
                Intent mainPageIntent = new Intent(LoginActivity.this,
                        MainActivity.class);
                startActivity(mainPageIntent);
            }
        });

        login_CreateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(LoginActivity.this,
                        SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = login_Auth.getCurrentUser();
//        updateUI(currentUser);
    }
}
