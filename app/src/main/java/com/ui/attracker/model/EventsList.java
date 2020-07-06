package com.ui.attracker.model;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Objects;

public class EventsList
{
    private static ArrayList<Event> eventsList = null;

    public static void loadEvents(Context context) {
        if (eventsList != null)
            return;

        eventsList = new ArrayList<>();

        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir("images", Context.MODE_PRIVATE);

        for (final File fileEntry : Objects.requireNonNull(directory.listFiles())) {
            String name = fileEntry.getName().substring(0, fileEntry.getName().length() - 4);
            addEvent(new Event(Objects.requireNonNull(loadBitmapFromStorage(fileEntry)), name));
        }
    }

    public static ArrayList<Event> getEventsList() {
        return eventsList;
    }

    public static void addEvent(Event event) {
        eventsList.add(event);
    }

    public static void discardEvents() {
        eventsList = null;
    }

    private static Bitmap loadBitmapFromStorage(File file)
    {
        try (FileInputStream inpStream = new FileInputStream(file)) {
            return BitmapFactory.decodeStream(inpStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public static class Event {
        Bitmap image;
        String eventName;

        public Event(@NonNull Bitmap image, @NonNull String eventName) {
            this.image = image;
            this.eventName = eventName;
        }

        public Bitmap getImage() {
            return image;
        }

        public String getEventName() {
            return eventName;
        }
    }
}
