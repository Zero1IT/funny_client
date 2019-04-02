package com.example.funnynose.events.Support;

import android.util.Log;

import com.example.funnynose.constants.Session;
import com.example.funnynose.events.Event;
import com.example.funnynose.events.EventsData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EventsAnotherData extends EventsData {

    private static final EventsAnotherData ourInstance = new EventsAnotherData();
    public static EventsData getInstance() {
        return ourInstance;
    }

    private static final String SERVER_LOAD_LISTENER = "event_another_listen";
    private static final String SERVER_CHECK_EVENT = "check_another_events";
    private static final String SERVER_EVENT_COUNT = "another_count";

    private EventsAnotherData() {
        super(new ArrayList<Event>(), SERVER_EVENT_COUNT);
        Log.d(Session.TAG, "another-create");
        loadServerEvents(SERVER_CHECK_EVENT, SERVER_LOAD_LISTENER);
    }

    @Override
    protected int eventIcon() {
        return Event.ICON_ANOTHER;
    }

    @Override
    protected int eventType() {
        return EVENT_ANOTHER_TYPE;
    }
}
