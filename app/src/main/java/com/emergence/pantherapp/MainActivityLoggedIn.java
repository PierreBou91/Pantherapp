package com.emergence.pantherapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.emergence.pantherapp.model.User;

public class MainActivityLoggedIn extends AppCompatActivity {

    // define components
    // components ids start with "mainpage" prefix
    public static User CURRENT_USER_SESSION;

    private Button importDocButton, scanDocButton;

    private PackageManager manager; // Package manager is useful to know the features of the app
                                    // This might be worth it performance wise to query this package
                                    // manager at launch (splash screen) and not every time the main
                                    // activity is created

    private TextView hello;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize components
        hello = findViewById(R.id.mainpage_hello);
        importDocButton = (Button) findViewById(R.id.mainpage_importdoc);
        scanDocButton = findViewById(R.id.mainpage_scandoc);
        // as explained earlier, this package manager will be initialized (i.e. will get ALL the
        // features of the device) at every onCreate of main so I suggest we move it to the splash
        // screen before issuing the final version
        manager = this.getPackageManager();

        // set actions
        hello.setText("Hello " + CURRENT_USER_SESSION.getUsername());

        importDocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanDocIntent = new Intent(MainActivityLoggedIn.this,
                        ImportDocActivity.class);
                startActivity(scanDocIntent);
            }
        });

        // check first if the device has a camera to avoid null pointer crash
        if (manager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

            scanDocButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCameraToTakePictureIntent();
                }
            });
        } else {
            // disable the button in case the device has no camera
            scanDocButton.setEnabled(false);
            scanDocButton.setText("No camera found");
        }

    }

    private void openCameraToTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

}
