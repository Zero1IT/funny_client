package by.funnynose.app.events.Support;

import android.util.Log;

import by.funnynose.app.constants.Session;
import by.funnynose.app.events.Event;
import by.funnynose.app.events.EventsData;

import java.util.ArrayList;

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
