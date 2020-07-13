package com.ui.attracker.model;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.ui.attracker.EventListActivity;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventsList
{
    private static ArrayList<Event> eventsList = new ArrayList<>();


    public static ArrayList<Event> getEventsList() {
        return eventsList;
    }

    public static void addEvent(Event event) {
        if (eventsList == null)
            eventsList = new ArrayList<>();
        eventsList.add(event);

        if (EventListActivity.eventAdapter != null)
            EventListActivity.eventAdapter.notifyDataSetChanged();
    }

    public static void discardEvents() {
        eventsList = new ArrayList<>();
    }



    public static class Event {
        Bitmap image;
        String eventName;

        public Event(Bitmap image, String eventName) {
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
