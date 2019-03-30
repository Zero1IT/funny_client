package com.example.funnynose.events;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.funnynose.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EventTrainingFragment extends EventFragment {

    private static final String LAYOUT_NAME = "Тренинги";
    private static final String SERVER_LISTENER = "training_event_listen";

    private int mDataCountMax; // TODO: check in db later

    public static EventTrainingFragment newInstance() {

        Bundle args = new Bundle();

        EventTrainingFragment fragment = new EventTrainingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public EventTrainingFragment() {
        super(R.layout.event_item_training, EventsData.getInstance().getEventsTraining());
        mDataCountMax = 1000;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void loadMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Event> list = new ArrayList<>();
                for (int i = mEvents.size(); i < mEvents.size() + 50; i++) {
                    Event e = new Event();
                    e.setId(i);
                    e.setIcon(Event.ICON_TRAINING);
                    e.setTitle("Training-event test server load");
                    e.setDate(new Date());
                    e.setFinished(i % 2 == 0);
                    list.add(e);
                }

                if (isLoadedNewEvent()) {
                    mAdapter.addData(0, mNewAddedEvents);
                    mLoadedNewEvent = false;
                    mNewAddedEvents.clear();
                }
                mAdapter.addData(list);
                mLoadingMore = false;
                mAdapter.loadMoreComplete();
            }
        }, 3000);
    }

    @Override
    protected int getDataCountMax() {
        return mDataCountMax;
    }

    @Override
    public String getNameLayout() {
        return LAYOUT_NAME;
    }

    @Override
    public String getServerListenerName() {
        return SERVER_LISTENER;
    }
}
