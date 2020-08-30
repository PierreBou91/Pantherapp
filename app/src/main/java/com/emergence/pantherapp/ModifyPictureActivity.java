package com.emergence.pantherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ModifyPictureActivity extends AppCompatActivity {

    private String TAG = "ModifyPictureActivity";

    private ImageView image;

    private TextView textView;
    private EditText field1, field2, field3, field4;
    private Button saveButton;
    private List<String> watermarkArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_picture);

        Intent intent = getIntent();
        final Uri imageUri = Uri.fromFile(new File(intent.getStringExtra("USER_IMAGE")));

        image = findViewById(R.id.picture);
        textView = findViewById(R.id.textView);
        field1 = findViewById(R.id.editText);
        field2 = findViewById(R.id.editText2);
        field3 = findViewById(R.id.editText3);
        field4 = findViewById(R.id.editText4);
        saveButton = findViewById(R.id.saveFieldsButton);
        watermarkArray = new ArrayList<>();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFields(field1);
                saveFields(field2);
                saveFields(field3);
                saveFields(field4);
                try {
                    modifyThePic(imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void modifyThePic(Uri imageUri) throws IOException {

        InputStream input = this.getContentResolver().openInputStream(imageUri); //used to draw bitmap
        Bitmap bitmap = BitmapFactory.decodeStream(input)
                .copy(Bitmap.Config.ARGB_8888,true); //creating bitmap, the "copy" part is to allow modifications

        Canvas canvas = new Canvas(bitmap); //modifications done through Canvas

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(300);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setAlpha(100);

        int xPos = (canvas.getWidth() / 4); //"math" to kind of center the text
        int yPos = (int) ((canvas.getHeight() / 4) - ((paint.descent() + paint.ascent()) / 2)) ;

        for (int i=0; i < watermarkArray.size(); i++) {
            canvas.drawText(watermarkArray.get(i), xPos, yPos, paint); //Actually drawing the text
            yPos = (int) (yPos + paint.getTextSize()); //offset the placement of the next field vertically
        }

        textView.setVisibility(View.GONE);
        field1.setVisibility(View.GONE);
        field2.setVisibility(View.GONE);
        field3.setVisibility(View.GONE);
        field4.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);

        image.setImageBitmap(bitmap); //Control the result

    }

    private void saveFields(EditText editText) {

        if (!editText.getText().toString().equals("")){
            watermarkArray.add(editText.getText().toString());
        }

    }

}
