package com.ui.attracker.model;

import android.graphics.Bitmap;

public class Event {
    Bitmap image;
    String eventName;
    String eventKey;

    public Event(Bitmap image, String eventName, String eventKey) {
        this.image = image;
        this.eventName = eventName;
        this.eventKey = eventKey;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventKey() {
        return eventKey;
    }
}
