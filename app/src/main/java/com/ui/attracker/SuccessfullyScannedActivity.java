package com.ui.attracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class SuccessfullyScannedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successfully_scanned);

        TextView scannedValueTextView = findViewById(R.id.scannedValueTextView);
        scannedValueTextView.setText(getIntent().getStringExtra("BARCODE_VALUE"));
    }
}