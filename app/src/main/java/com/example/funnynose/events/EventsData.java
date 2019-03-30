package com.example.funnynose.events;

import com.example.funnynose.network.SocketAPI;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

import io.socket.emitter.Emitter;

class EventsData extends Observable {
    private static final EventsData ourInstance = new EventsData();

    private static final String FIRST_LOAD_EVENTS = "events_check";

    private final List<Event> mEventsAnother = new ArrayList<>();
    private final List<Event> mEventsHospital = new ArrayList<>();
    private final List<Event> mEventsTraining = new ArrayList<>();

    static EventsData getInstance() {
        return ourInstance;
    }

    private EventsData() {
        FORDEBUG();
    }
    private void onceEventsCheck() {
        SocketAPI.getSocket().emit(FIRST_LOAD_EVENTS, "User.getPhone/getName/getId")
            .once(FIRST_LOAD_EVENTS, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    // TODO: release later
                }
            });
    }

    List<Event> getEventsAnother() {
        return mEventsAnother;
    }

    List<Event> getEventsTraining() {
        return mEventsTraining;
    }

    List<Event> getEventsHospital() {
        return mEventsHospital;
    }

    private void FORDEBUG() {
        for (int i = 0; i < 150; i++) {
            Event e = new Event();
            e.setId(i);
            e.setDate(new Date());
            e.setTitle(UUID.randomUUID().toString());
            e.setFinished(i % 2 == 0);
            e.setIcon(Event.ICON_ANOTHER);
            mEventsAnother.add(e);
        }
        for (int i = 0; i < 150; i++) {
            Event e = new Event();
            e.setId(i);
            e.setDate(new Date());
            e.setTitle(UUID.randomUUID().toString());
            e.setFinished(i % 2 == 0);
            e.setIcon(Event.ICON_HOSPITAL);
            mEventsHospital.add(e);
        }
        for (int i = 0; i < 150; i++) {
            Event e = new Event();
            e.setId(i);
            e.setDate(new Date());
            e.setTitle(UUID.randomUUID().toString());
            e.setFinished(i % 2 == 0);
            e.setIcon(Event.ICON_TRAINING);
            mEventsTraining.add(e);
        }
    }
}
