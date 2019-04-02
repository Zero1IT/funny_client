package by.funnynose.app.events.Support;

import android.util.Log;

import by.funnynose.app.constants.Session;
import by.funnynose.app.events.Event;
import by.funnynose.app.events.EventsData;

import java.util.ArrayList;

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
