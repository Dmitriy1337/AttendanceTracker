package com.ui.attracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        final Intent scanIntent = new Intent(this,ScanActivity.class);
        final Intent eventListIntent = new Intent(this,EventListActivity.class);
        final Intent newEventIntent = new Intent(this,NewEventActivity.class);

        Button scan = findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(scanIntent);
            }
        });

        Button eventList = findViewById(R.id.eventList);
        eventList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(eventListIntent);
            }
        });

        Button newEvent = findViewById(R.id.newEvent);
        newEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(newEventIntent);
            }
        });


    }
}