package com.ui.attracker;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ui.attracker.model.EventsList;

public class ViewEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);


        int eventNumber = getIntent().getIntExtra("EVENT_NUMBER", -1);
        TextView eventNameOpenedTextView = findViewById(R.id.eventNameOpenedTextView);
        ImageView qrImageOpened = findViewById(R.id.qrImageOpened);
        qrImageOpened.setAdjustViewBounds(true);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        qrImageOpened.setMaxWidth(dpToPx(pxToDp(widthPixels) - 48));


        if (eventNumber >= 0) {
            EventsList.Event event = EventsList.getEventsList().get(eventNumber);
            if (event == null)
                return;
            eventNameOpenedTextView.setText(event.getEventName());
            qrImageOpened.setImageBitmap(event.getImage());
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}