package com.emergence.pantherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    //define components
    //all the components start with the 'login' prefix
    Button connectButton;
    private TextView createTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize components
        connectButton = findViewById(R.id.login_connect);
        createTextView = findViewById(R.id.login_sign_up);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start new Signup screen and finish the current
                Intent mainPageIntent = new Intent(LoginActivity.this,
                        MainActivity.class);
                startActivity(mainPageIntent);
            }
        });

        createTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(LoginActivity.this,
                        SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });
    }
}
