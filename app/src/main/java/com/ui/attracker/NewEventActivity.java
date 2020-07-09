package com.ui.attracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.ui.attracker.model.EventsList;

public class NewEventActivity extends AppCompatActivity {

    private static final String TAG = "NewEventActivity";
    private static final String INVALID_EVENT_NAME_MESSAGE = "Enter a valid event name";
    public static final int QR_SIZE = 1000;
    public static final int MAX_EVENT_NAME_SIZE = 30;

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
                    APIRequests.addEvent(eventName, getApplicationContext());
                    EventListActivity.eventAdapter.notifyDataSetChanged();

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
        return name.matches("^[a-zA-Z0-9\\s]+$") && name.length() < MAX_EVENT_NAME_SIZE && !APIRequests.user.getEvents().contains(name);
    }
}