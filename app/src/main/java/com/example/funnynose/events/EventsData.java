package com.example.funnynose.events;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class EventsData {
    private static final EventsData ourInstance = new EventsData();

    private List<Event> mEvents;

    static EventsData getInstance() {
        return ourInstance;
    }

    public List<Event> getEvents() {
        return mEvents;
    }

    private EventsData() {
        mEvents = new ArrayList<>();
        Event e = new Event();
        e.setDate(new Date());
        for (int i = 0; i < 150; i++) {
            mEvents.add(e);
        }
    }
}
