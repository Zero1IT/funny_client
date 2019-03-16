package com.example.funnynose.events;

import com.example.funnynose.network.SocketAPI;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.socket.emitter.Emitter;

class EventsData {
    private static final EventsData ourInstance = new EventsData();

    private static final String FIRST_LOAD_ANOTHER = "another_events_check";
    private static final String FIRST_LOAD_HOSPITAL = "hospital_events_check";
    private static final String FIRST_LOAD_TRAINING = "training_events_check";
    private static final String LISTEN_ANOTHER_EVENT = "another_events_listen";
    private static final String LISTEN_HOSPITAL_EVENT = "hospital_events_listen";
    private static final String LISTEN_TRAINING_EVENT = "training_events_listen";

    private final List<Event> mEventsAnother = new ArrayList<>();
    private final List<Event> mEventsHospital = new ArrayList<>();
    private final List<Event> mEventsTraining = new ArrayList<>();

    static EventsData getInstance() {
        return ourInstance;
    }

    public List<Event> getEventsAnother() {
        return mEventsAnother;
    }

    private EventsData() {
        //firstLoadEvents(FIRST_LOAD_ANOTHER);
        //firstLoadEvents(FIRST_LOAD_HOSPITAL);
        //firstLoadEvents(FIRST_LOAD_TRAINING);
        FORDEBUG();
    }

    private void FORDEBUG() {
        for (int i = 0; i < 150; i++) {
            Event e = new Event();
            e.setDate(new Date());
            e.setTitle(UUID.randomUUID().toString());
            e.setFinished(i % 2 == 0);
            e.setIcon(Event.ICON_ANOTHER);
            mEventsAnother.add(e);
        }
        for (int i = 0; i < 150; i++) {
            Event e = new Event();
            e.setDate(new Date());
            e.setTitle(UUID.randomUUID().toString());
            e.setFinished(i % 2 == 0);
            e.setIcon(Event.ICON_HOSPITAL);
            mEventsHospital.add(e);
        }
        for (int i = 0; i < 150; i++) {
            Event e = new Event();
            e.setDate(new Date());
            e.setTitle(UUID.randomUUID().toString());
            e.setFinished(i % 2 == 0);
            e.setIcon(Event.ICON_TRANING);
            mEventsTraining.add(e);
        }
    }

    private void firstLoadEvents(String event) {
        SocketAPI.getSocket().once(event, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                synchronized (mEventsAnother) {
                    //TODO: release later
                }
            }
        });
    }

    public List<Event> getEventsTraining() {
        return mEventsTraining;
    }

    public List<Event> getEventsHospital() {
        return mEventsHospital;
    }
}
