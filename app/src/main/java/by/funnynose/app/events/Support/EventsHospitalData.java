package by.funnynose.app.events.Support;

import android.util.Log;

import by.funnynose.app.constants.Session;
import by.funnynose.app.events.Event;
import by.funnynose.app.events.EventsData;

import java.util.ArrayList;

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
