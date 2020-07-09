package com.ui.attracker.model;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ui.attracker.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
                bitmap = generateQR(event);
                saveBitmapToInternalStorage(event, bitmap, context);
            } else
                bitmap = loadBitmapFromStorage(event, context);

            EventsList.addEvent(new EventsList.Event(bitmap, event));
        }

    }

    public void addEvent(String name, Context context) {
        this.events.add(name);

        Bitmap bitmap = generateQR(key + "/" + name);
        saveBitmapToInternalStorage(name, bitmap, context);
        EventsList.addEvent(new EventsList.Event(bitmap, name));
    }

    public List<String> addAttendee(String eventName, String username) {
        if (!events.contains(eventName))
            throw new AssertionError("Assertion failed");

        for (EventsList.Event event : EventsList.getEventsList())
            if (event.getEventName().equals(eventName)) {
                event.addAttendee(username);
                return event.getAttendees();
            }
        return null;
    }
}
