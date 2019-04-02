package com.example.funnynose.events.Support;

import android.util.Log;

import com.example.funnynose.constants.Session;
import com.example.funnynose.events.Event;
import com.example.funnynose.events.EventsData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EventsTrainingData extends EventsData {

    private static final EventsTrainingData ourInstance = new EventsTrainingData();
    public static EventsData getInstance() {
        return ourInstance;
    }

    private static final String SERVER_LOAD_LISTENER = "event_training_listen";
    private static final String SERVER_CHECK_EVENT = "check_training_events";
    private static final String SERVER_EVENT_COUNT = "training_count";

    private EventsTrainingData() {
        super(new ArrayList<Event>(), SERVER_EVENT_COUNT);
        Log.d(Session.TAG, "training-create");
        loadServerEvents(SERVER_CHECK_EVENT, SERVER_LOAD_LISTENER);
    }

    @Override
    protected int eventIcon() {
        return Event.ICON_TRAINING;
    }

    @Override
    protected int eventType() {
        return EVENT_TRAINING_TYPE;
    }
}
