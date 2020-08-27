package com.emergence.pantherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class ModifyPictureActivity extends AppCompatActivity {

    ImageView image;
    private String TAG = "ModifyPictureActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_picture);

        Intent intent = getIntent();
        String imageUri = intent.getStringExtra("USER_IMAGE");

        if (imageUri != null) {
            Log.d(TAG, imageUri);
        } else {
            Log.d(TAG, "imageUri was null");
        }

        image = findViewById(R.id.picture);

        image.setImageURI(Uri.parse(imageUri));

    }
}
