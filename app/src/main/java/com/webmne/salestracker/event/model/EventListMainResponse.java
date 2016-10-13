package com.webmne.salestracker.event.model;

import com.webmne.salestracker.api.model.Response;

import java.util.ArrayList;

/**
 * Created by vatsaldesai on 12-10-2016.
 */

public class EventListMainResponse extends Response {

    private ArrayList<Event> Events;

    public ArrayList<Event> getEvents() {
        return Events;
    }

    public void setEvents(ArrayList<Event> events) {
        Events = events;
    }
}
