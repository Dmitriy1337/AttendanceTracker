package com.ui.attracker.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.ui.attracker.InternalStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.ui.attracker.InternalStorage.loadBitmapFromStorage;
import static com.ui.attracker.InternalStorage.saveBitmapToInternalStorage;
import static com.ui.attracker.QRGenerator.generateQR;

public class User
{
    private String username;
    private List<String> events;
    private String key;

    private User() {}

    public User(String username, String key) {
        this.username = username;
        this.key = key;
        events = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public List<String> getEvents() {
        return events;
    }

    public String getKey() {
        return key;
    }


    public void updateEvents(Context context) {
        File directory = InternalStorage.getBitmapDirectory(context);

        ArrayList<String> fileNames = new ArrayList<>();
        for (final File fileEntry : directory.listFiles()) {
            String name = fileEntry.getName().substring(0, fileEntry.getName().length() - 4);
            fileNames.add(name);
        }

        if (events == null)
            events = new ArrayList<>();
        for (String event : events) {
            String eventName = event.substring(0, event.indexOf('/'));
            String eventKey = event.substring(event.indexOf('/') + 1);

            Bitmap bitmap;
            if (!fileNames.contains(eventName)) {
                bitmap = generateQR(key + "/" + eventKey);
                saveBitmapToInternalStorage(eventName + "\\" + eventKey, bitmap, context);
            } else
                bitmap = loadBitmapFromStorage(eventName + "\\" + eventKey, context);

            EventsList.addEvent(new Event(bitmap, eventName, eventKey));
        }

    }

    public void addEvent(String eventName, String eventKey, Context context) {
        this.events.add(eventName + "/" + eventKey);

        Bitmap bitmap = generateQR(key + "/" + eventKey);
        saveBitmapToInternalStorage(eventName + "\\" + eventKey, bitmap, context);
        EventsList.addEvent(new Event(bitmap, eventName, eventKey));
    }
}
