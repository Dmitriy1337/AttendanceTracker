package com.ui.attracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ui.attracker.model.Event;
import com.ui.attracker.model.EventsList;


public class EventListActivity extends AppCompatActivity{

    public static ArrayAdapter<Event> eventAdapter = null;

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


        final GridView eventsGrid = findViewById(R.id.eventsGridView);
        eventsGrid.setNumColumns(2);
        eventsGrid.setVerticalSpacing(0);
        eventsGrid.setHorizontalSpacing(0);

        eventAdapter = (eventAdapter != null) ? eventAdapter : new ArrayAdapter<Event>(this, 0, EventsList.getEventsList()) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                Event currentEvent = getItem(position);

                CardViewViewHolder viewHolder;
                if (convertView == null) {
                    convertView = getLayoutInflater()
                            .inflate(R.layout.event_item, null, false);
                    viewHolder = new CardViewViewHolder();
                    viewHolder.qrImage = convertView.findViewById(R.id.qrImage);
                    viewHolder.eventNameTextView = convertView.findViewById(R.id.eventNameTextView);
                    convertView.setTag(viewHolder);
                } else
                    viewHolder = (CardViewViewHolder) convertView.getTag();

                if (currentEvent == null)
                        return convertView;

                ImageView qrImage = viewHolder.qrImage;
                TextView eventNameTextView = viewHolder.eventNameTextView;
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


    static class CardViewViewHolder {
        ImageView qrImage;
        TextView eventNameTextView;
    }
}