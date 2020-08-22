package com.ui.attracker.model;

import com.ui.attracker.EventListActivity;

import java.util.ArrayList;

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

}
