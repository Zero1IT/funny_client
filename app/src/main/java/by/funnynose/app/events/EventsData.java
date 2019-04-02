package by.funnynose.app.events;

import android.util.Log;

import by.funnynose.app.constants.Session;
import by.funnynose.app.network.SocketAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import io.socket.emitter.Emitter;

public abstract class EventsData extends Observable {

    public final static int EVENT_ANOTHER_TYPE = 0;
    public final static int EVENT_HOSPITAL_TYPE = 1;
    public final static int EVENT_TRAINING_TYPE = 2;

    private final static String CHECK_SERVER_EVENT = "check_event";
    private final static String CHECK_EVENT_COUNT = "check_event_count";

    private final List<Object> mNotSendEvents = new ArrayList<>();
    private List<Event> mEvents;
    private AsyncCountGetter mFunc;

    public EventsData(List<Event> events, String listenCount) {
        mEvents = events;
        initEventCount(listenCount);
    }

    public List<Event> getData() {
        return mEvents;
    }

    protected void loadServerEvents(String checker, final String listener) {
        long lastId = mEvents.size();
        JSONObject args = new JSONObject();
        try {
            args.put("id", lastId);
            args.put("type", eventType());
        } catch (JSONException e) {
            Log.e(Session.TAG, "id put exception");
        }
        SocketAPI.getSocket().emit(CHECK_SERVER_EVENT, args).on(checker, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args != null) {
                    JSONArray eventArray = (JSONArray) args[0];
                    try {
                        for (int i = 0; i < eventArray.length(); i++) {
                            unPackEvent((JSONObject) eventArray.get(i));
                        }
                    } catch (JSONException e) {
                        Log.d(Session.TAG, "first data load if fail");
                    }
                }
                listenEventsServer(listener);
            }
        });
    }

    private void listenEventsServer(String name) {
        SocketAPI.getSocket().on(name, new Emitter.Listener() {
            @Override
            public void call(Object ... args) {
                if (countObservers() == 0) {
                    mNotSendEvents.add(args[0]);
                } else {
                    setChanged();
                    unPackEvent((JSONObject) args[0]);
                }
            }
        });
    }

    private void initEventCount(String listen) {
        JSONObject args = new JSONObject();
        try {
            args.put("type", eventType());
        } catch (JSONException e) {
            Log.e(Session.TAG, e.getMessage());
        }
        SocketAPI.getSocket().emit(CHECK_EVENT_COUNT, args)
                .once(listen, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                try {
                    long countEvent = obj.getInt("count");
                    mFunc.result(countEvent);
                    Log.d(Session.TAG, countEvent + "");
                } catch (JSONException e) {
                    Log.e(Session.TAG, e.getMessage());
                }
            }
        });
    }

    private synchronized void unPackEvent(JSONObject obj) {
        Log.d(Session.TAG, "FROM SERVER = " + obj.toString());
        Event event;
        try {
            event = new Event(obj);
            event.setIcon(eventIcon());
        } catch (JSONException e) {
            Log.e(Session.TAG, e.getMessage());
            throw new ExceptionInInitializerError("Fatal error, fix it");
        }

        synchronized (mNotSendEvents) {
            if (countObservers() > 0) {
                setChanged();
                notifyObservers(event);
            } else {
                mNotSendEvents.add(event);
            }
        }

    }

    @Override
    public synchronized void addObserver(Observer o) {
        if (countObservers() > 0) return;
        super.addObserver(o);
        if (mNotSendEvents.size() > 0) {
            synchronized (mNotSendEvents) {
                for (Object i : mNotSendEvents) {
                    setChanged();
                    notifyObservers(i);
                }
                mNotSendEvents.clear();
            }
        }
    }

    void getCountEvent(AsyncCountGetter func) {
        mFunc = func;
    }

    protected abstract int eventIcon();
    protected abstract int eventType();

    interface AsyncCountGetter {
        void result(long count);
    }
}
