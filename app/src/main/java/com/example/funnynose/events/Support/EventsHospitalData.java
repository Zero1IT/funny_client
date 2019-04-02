package com.example.funnynose.events.Support;

import android.util.Log;

import com.example.funnynose.constants.Session;
import com.example.funnynose.events.Event;
import com.example.funnynose.events.EventsData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EventsHospitalData extends EventsData {

    private static final EventsHospitalData ourInstance = new EventsHospitalData();
    public static EventsData getInstance() {
        return ourInstance;
    }

    private static final String SERVER_LOAD_LISTENER = "event_hospital_listen";
    private static final String SERVER_CHECK_EVENT = "check_hospital_events";
    private static final String SERVER_EVENT_COUNT = "hospital_count";

    private EventsHospitalData() {
        super(new ArrayList<Event>(), SERVER_EVENT_COUNT);
        Log.d(Session.TAG, "hospital-create");
        loadServerEvents(SERVER_CHECK_EVENT, SERVER_LOAD_LISTENER);
    }

    @Override
    protected int eventIcon() {
        return Event.ICON_HOSPITAL;
    }

    @Override
    protected int eventType() {
        return EVENT_HOSPITAL_TYPE;
    }
}
