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
        final Intent scanIntent = new Intent(this, ScanActivity.class);
        final Intent eventListIntent = new Intent(this, EventListActivity.class);
        final Intent logoutIntent = new Intent(this, LogoutActivity.class);

        Button scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(scanIntent);
            }
        });

        Button eventListBtn = findViewById(R.id.eventListBtn);
        eventListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(eventListIntent);
            }
        });

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(logoutIntent);
            }
        });
    }
}