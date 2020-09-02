package com.emergence.pantherapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModifyPictureActivity extends AppCompatActivity {

    private String TAG = "ModifyPictureActivity";

    private ImageView image;

    private TextView textView;
    private EditText field1, field2, field3, field4;
    private Button saveButton, sendByMail, saveToGallery, sendOther;
    private List<String> watermarkArray;
    private String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_picture);

        Intent intent = getIntent();
        final Uri imageUri = Uri.fromFile(new File(intent.getStringExtra("USER_IMAGE")));
        path = intent.getStringExtra("USER_IMAGE");

        image = findViewById(R.id.picture);
        textView = findViewById(R.id.textView);
        field1 = findViewById(R.id.editText);
        field2 = findViewById(R.id.editText2);
        field3 = findViewById(R.id.editText3);
        field4 = findViewById(R.id.editText4);
        saveButton = findViewById(R.id.saveFieldsButton);
        sendByMail = findViewById(R.id.sendByMail);
        saveToGallery = findViewById(R.id.saveToGallery);
        sendOther = findViewById(R.id.sendOther);

        watermarkArray = new ArrayList<>();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

        sendByMail.setVisibility(View.VISIBLE);
        saveToGallery.setVisibility(View.VISIBLE);
        sendOther.setVisibility(View.VISIBLE);

        image.setImageBitmap(bitmap); //Control the result

        saveBitmap(this, bitmap, Bitmap.CompressFormat.JPEG,".jpg", path);
    }

    private void saveFields(EditText editText) {

        if (!editText.getText().toString().equals("")){
            watermarkArray.add(editText.getText().toString());
        }

    }

    private void saveImage() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void saveBitmap(@NonNull final Context context, @NonNull final Bitmap bitmap,
                            @NonNull final Bitmap.CompressFormat format, @NonNull final String mimeType,
                            @NonNull final String displayName) throws IOException
    {
        final String relativeLocation = Environment.DIRECTORY_PICTURES;

        final ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, relativeLocation);



        final ContentResolver resolver = context.getContentResolver();

        OutputStream stream = null;
        Uri uri = null;

        try
        {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, contentValues);

            if (uri == null)
            {
                throw new IOException("Failed to create new MediaStore record.");
            }

            stream = resolver.openOutputStream(uri);

            if (stream == null)
            {
                throw new IOException("Failed to get output stream.");
            }

            if (!bitmap.compress(format, 95, stream))
            {
                throw new IOException("Failed to save bitmap.");
            }
        }
        catch (IOException e)
        {
            if (uri != null)
            {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null);
            }

            throw e;
        }
        finally
        {
            if (stream != null)
            {
                stream.close();
            }
        }
    }

}
