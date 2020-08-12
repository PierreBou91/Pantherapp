package com.emergence.pantherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {
    // duration of splash (in miliseconds)
    private final int SPLASH_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // new Handler to start the Welcome activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // start new SignUp screen and finish the current
                Intent loginIntent = new Intent(SplashActivity.this,
                        MainMenuActivity.class);
                startActivity(loginIntent);
                finish();
            }
        }, SPLASH_DURATION);
    }
}
