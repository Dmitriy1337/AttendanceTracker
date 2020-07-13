package com.ui.attracker.model;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir("images", Context.MODE_PRIVATE);

        ArrayList<String> fileNames = new ArrayList<>();
        for (final File fileEntry : Objects.requireNonNull(directory.listFiles())) {
            String name = fileEntry.getName().substring(0, fileEntry.getName().length() - 4);
            fileNames.add(name);
        }

        if (events == null)
            events = new ArrayList<>();
        for (String event : events) {
            Bitmap bitmap;
            if (!fileNames.contains(event)) {
                bitmap = generateQR(key + "/" + event);
                saveBitmapToInternalStorage(event, bitmap, context);
            } else
                bitmap = loadBitmapFromStorage(event, context);

            EventsList.addEvent(new EventsList.Event(bitmap, event));
        }

    }

    public void addEvent(String eventName, Context context) {
        this.events.add(eventName);

        Bitmap bitmap = generateQR(key + "/" + eventName);
        saveBitmapToInternalStorage(eventName, bitmap, context);
        EventsList.addEvent(new EventsList.Event(bitmap, eventName));
    }
}
