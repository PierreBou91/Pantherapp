package com.emergence.pantherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.emergence.pantherapp.model.User;

public class MainActivity extends AppCompatActivity {

    // define components
    // components ids start with "mainpage" prefix
    Button importDocButton;
    public static User CURRENT_USER_SESSION;

    private TextView hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initalize components
        importDocButton = (Button) findViewById(R.id.mainpage_importdoc);
        hello = findViewById(R.id.mainpage_hello);

        hello.setText("Hello " + CURRENT_USER_SESSION.getUsername());

        // set actions
        importDocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanDocIntent = new Intent(MainActivity.this,
                        ImportDocActivity.class);
                startActivity(scanDocIntent);
            }
        });
    }
}
