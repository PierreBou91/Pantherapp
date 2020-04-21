package com.emergence.pantherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // define components
    // components ids start with "mainpage" prefix
    Button importDocButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initalize components
        importDocButton = (Button) findViewById(R.id.mainpage_importdoc);

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
