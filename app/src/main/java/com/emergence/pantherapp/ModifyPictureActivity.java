package com.emergence.pantherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ModifyPictureActivity extends AppCompatActivity {

    private String TAG = "ModifyPictureActivity";
//    private ImageView image;

    private TextView textView;
    private EditText field1, field2, field3, field4;
    private Button saveButton;
    private int numberOfFields;
    private String[] watermarkArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_picture);

        Intent intent = getIntent();
        Uri imageUri = Uri.parse(intent.getStringExtra("USER_IMAGE"));

//        image = findViewById(R.id.picture);
//        image.setImageURI(imageUri);

        textView = findViewById(R.id.textView);
        field1 = findViewById(R.id.editText);
        field2 = findViewById(R.id.editText2);
        field3 = findViewById(R.id.editText3);
        field4 = findViewById(R.id.editText4);
        saveButton = findViewById(R.id.saveFieldsButton);
        numberOfFields = 0;
        watermarkArray = null;

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFields(field1);
                saveFields(field2);
                saveFields(field3);
                saveFields(field4);


            }
        });
    }

    private void saveFields(EditText editText) {

        if (!editText.getText().toString().equals("")){
            watermarkArray[numberOfFields] = editText.getText().toString();
            numberOfFields++;
        }
    }
}
