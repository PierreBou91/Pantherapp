package com.emergence.pantherapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainMenuActivity extends AppCompatActivity {

    private Button selectFile, signIn;
    private String TAG = "MainMenuActivity";

    static final int REQUEST_IMAGE_CAPTURE = 1; //used as codes for startActivityForResult
    static final int PICK_IMAGE = 2;

    private String currentPhotoPath; //used to store the absolute path of the created file

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        selectFile = findViewById(R.id.menu_select_file);
        signIn = findViewById(R.id.menu_signin_signup_button);

        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button successfully clicked");
                showDialogToSelectAFile();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(MainMenuActivity.this,
                        LoginActivity.class);
                startActivity(loginIntent);
            }
        });

    }

    private void showDialogToSelectAFile() {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);

        dialog.setTitle("Select a file or take a picture")
                .setMessage("Choose one of the options")
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                }).setPositiveButton("Take a picture", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openCameraToTakePictureIntent();
            }
        }).setNegativeButton("Select a file from your gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openGalleryIntent();
            }
        }).show();
    }

    private void openCameraToTakePictureIntent() {
        Log.d(TAG, "Method for Intent Camera started");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.emergence.pantherapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void openGalleryIntent() {
        Log.d(TAG, "Method for Intent Gallery started");
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        if (galleryIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.emergence.pantherapp.fileprovider", photoFile);
                galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(galleryIntent, PICK_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            Log.d(TAG, currentPhotoPath);

            Intent intent = new Intent(this, ModifyPictureActivity.class);
            intent.putExtra("USER_IMAGE", currentPhotoPath);
            startActivity(intent);

        } else if (resultCode == Activity.RESULT_OK && requestCode == 2) {
            Log.d(TAG, currentPhotoPath);

            if (data != null) {
                Uri uri = Uri.parse(data.getDataString());
                Log.d("DATA", uri.toString());

                InputStream input = null;
                OutputStream output = null;

                try {
                    ContentResolver content = this.getContentResolver();

                    input = content.openInputStream(uri);

                    output = new FileOutputStream(currentPhotoPath);

                    byte[] buffer = new byte[1000];
                    int bytesRead = 0;

                    assert input != null;

                    while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0)
                    {
                        output.write(buffer, 0, buffer.length);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(this, ModifyPictureActivity.class);
                intent.putExtra("USER_IMAGE", /*uri.toString()*/ currentPhotoPath);
                startActivity(intent);
            }
        }
    }
}
