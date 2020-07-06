package com.ui.attracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.WriterException;
import com.ui.attracker.model.EventsList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class NewEventActivity extends AppCompatActivity {

    private static final String TAG = "NewEventActivity";
    private static final String INVALID_EVENT_NAME_MESSAGE = "Enter a valid event name";
    private static final int QR_SIZE = 720;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);


        final Intent viewEvent = new Intent(this, ViewEventActivity.class);
        final EditText eventNameEditText = findViewById(R.id.eventNameEditText);
        final Button addEventBtn = findViewById(R.id.addEventBtn);


        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eventName = eventNameEditText.getText().toString();
                if (checkNameValidity(eventName)) {
                    Bitmap bitmap = generateQR(LoginActivity.getUsername(getApplicationContext()) + "/" + eventName);
                    saveToInternalStorage(eventName + ".png", bitmap);
                    EventsList.addEvent(new EventsList.Event(bitmap, eventName));
                    EventListActivity.notifyDataChange();

                    viewEvent.putExtra("EVENT_NUMBER", EventsList.getEventsList().size()-1);
                    startActivity(viewEvent);
                    finish();
                }
                else
                    Snackbar.make(view, INVALID_EVENT_NAME_MESSAGE, Snackbar.LENGTH_LONG).show();
            }
        });

    }



    private boolean checkNameValidity(String name) {
        return name.length() > 0;
    }

    private Bitmap generateQR(String message) {
        QRGEncoder qrgEncoder = new QRGEncoder(message, null, QRGContents.Type.TEXT, QR_SIZE);
        Bitmap bitmap;
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
        } catch (WriterException e) {
            bitmap = Bitmap.createBitmap(QR_SIZE, QR_SIZE, Bitmap.Config.RGB_565);
            Log.v(TAG, e.toString());
        }
        return bitmap;
    }


    private void saveToInternalStorage(String name, Bitmap bitmapImage){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("images", Context.MODE_PRIVATE);
        File myPath = new File(directory, name);

        try (FileOutputStream outputStream = new FileOutputStream(myPath)) {
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }
    }
}