package com.ui.attracker;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.ui.attracker.model.EventsList;


public class ViewEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_event);
        ListView listView = findViewById(R.id.viewEventListView);

        listView.addHeaderView(getLayoutInflater().inflate(R.layout.activity_view_event_header, null, false));
        listView.setHeaderDividersEnabled(false);
        listView.setDividerHeight(0);


        int eventNumber = getIntent().getIntExtra("EVENT_NUMBER", -1);
        TextView eventNameOpenedTextView = findViewById(R.id.eventNameOpenedTextView);

        ImageView qrImageOpened = findViewById(R.id.qrImageOpened);
        qrImageOpened.setAdjustViewBounds(true);

        Button shareBtn = findViewById(R.id.shareBtn);


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

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item, R.id.text1);
            listView.setAdapter(adapter);
            APIRequests.retrieveAttendees(event.getEventName(), adapter);

            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Tap", Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}