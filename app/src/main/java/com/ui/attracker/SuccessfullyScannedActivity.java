package com.ui.attracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class SuccessfullyScannedActivity extends AppCompatActivity {

    TextView messageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successfully_scanned);

        messageTextView = findViewById(R.id.scannedValueTextView);
        String content = getIntent().getStringExtra("BARCODE_VALUE");

        String userKey = content.substring(0, content.indexOf('/'));
        String eventKey = content.substring(content.indexOf('/') + 1);
        APIRequests.addAttendee(eventKey, userKey, this);
    }

    public void setMessage(String message) {
        this.messageTextView.setText(message);
    }
}