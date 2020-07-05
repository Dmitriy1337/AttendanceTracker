package com.ui.attracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ui.attracker.model.EventsList;


public class EventListActivity extends AppCompatActivity {

    static ArrayAdapter<EventsList.Event> eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        final Intent viewEvent = new Intent(this, ViewEventActivity.class);
        final Intent addEvent = new Intent(this, NewEventActivity.class);


        final FloatingActionButton fab = findViewById(R.id.addEventFloatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(addEvent);
            }
        });


        // initialize EventsList.eventsList which stores all events
        EventsList.loadEvents(getApplicationContext());


        final GridView eventsGrid = findViewById(R.id.eventsGridView);
        eventsGrid.setNumColumns(2);


        eventAdapter = new ArrayAdapter<EventsList.Event>(this, 0, EventsList.getEventsList()) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                EventsList.Event currentEvent = getItem(position);
                if(convertView == null)
                    convertView = getLayoutInflater()
                            .inflate(R.layout.event_item, null, false);

                if (currentEvent == null)
                        return convertView;

                ImageView qrImage = convertView.findViewById(R.id.qrImage);
                TextView eventNameTextView = convertView.findViewById(R.id.eventNameTextView);
                qrImage.setImageBitmap(Bitmap.createScaledBitmap(currentEvent.getImage(), eventsGrid.getColumnWidth(), eventsGrid.getColumnWidth(), false));
                eventNameTextView.setText(currentEvent.getEventName());
                return convertView;
            }
        };
        eventsGrid.setAdapter(eventAdapter);


        eventsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewEvent.putExtra("EVENT_NUMBER", i);
                startActivity(viewEvent);
            }
        });
    }

    public static void refreshData() {
        eventAdapter.notifyDataSetChanged();
    }
}