package com.ui.attracker;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import com.ui.attracker.model.Event;
import com.ui.attracker.model.EventsList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.util.ArrayList;


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

        final Button shareAttendeesBtn = findViewById(R.id.shareAttendeesBtn);
        Button shareImageBtn = findViewById(R.id.shareImageBtn);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        qrImageOpened.setMaxWidth(dpToPx(pxToDp(widthPixels) - 48));


        if (eventNumber >= 0) {
            final Event event = EventsList.getEventsList().get(eventNumber);
            if (event == null)
                return;
            eventNameOpenedTextView.setText(event.getEventName());
            qrImageOpened.setImageBitmap(event.getImage());

            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item, R.id.text1);
            listView.setAdapter(adapter);
            APIRequests.retrieveAttendees(event.getEventKey(), adapter);

            shareAttendeesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareAttendees(event, adapter, getApplicationContext());
                }
            });

            shareImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareImage(event, getApplicationContext());
                }
            });
        }
    }

    public void shareImage(Event event, Context context) {
        InternalStorage.saveBitmapToInternalStorage(event.getEventName(), event.getImage(), context);

        File file = new File(InternalStorage.getBitmapDirectory(context), event.getEventName() + ".png");
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.ui.attracker.fileprovider", file);

        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setType("application/png")
                .setStream(uri)
                .setChooserTitle("Choose a sender")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        this.startActivity(intent);
    }

    public void shareAttendees(Event event, ArrayAdapter<String> adapter, Context context) {
        ArrayList<String> attendees = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++)
            attendees.add(adapter.getItem(i));

        String eventName = event.getEventName();
        Workbook workbook = createAttendeesTable(eventName, attendees);
        InternalStorage.saveWorkbookToInternalStorage(eventName, workbook, context);

        File file = new File(InternalStorage.getWorkbookDirectory(context), event.getEventName() + ".xls");
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.ui.attracker.fileprovider", file);

        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setType("application/xls")
                .setStream(uri)
                .setChooserTitle("Choose a sender")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        this.startActivity(intent);
    }

    public Workbook createAttendeesTable(String eventName, ArrayList<String> attendees) {

        String date = "-";
        if (eventName.length() >= 11) {
            String dateSubstring = eventName.substring(eventName.length() - 10);
            if (dateSubstring.matches("^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d$")) {
                date = dateSubstring;
                eventName = eventName.substring(0, eventName.length() - 11);
            }
        }

        Workbook workbook = new HSSFWorkbook();
        Cell cell;
        Sheet sheet;

        sheet = workbook.createSheet("Attendees");

        Row row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("Event:");
        cell = row.createCell(1);
        cell.setCellValue(eventName);

        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("Date:");
        cell = row.createCell(1);
        cell.setCellValue(date);

        sheet.createRow(2);
        row = sheet.createRow(3);
        row.createCell(0).setCellValue("Attendees:");

        int index = 4;
        for (String attendee : attendees) {
            row = sheet.createRow(index++);
            cell = row.createCell(0);
            cell.setCellValue(attendee);
            cell = row.createCell(1);
            cell.setCellValue("1");
        }

        sheet.setColumnWidth(0, (15 * 500));
        sheet.setColumnWidth(1, (15 * 500));

        return workbook;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}