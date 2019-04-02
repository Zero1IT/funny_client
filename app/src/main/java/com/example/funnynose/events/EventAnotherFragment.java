package com.example.funnynose.events;

import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.example.funnynose.R;
import com.example.funnynose.events.Support.EventsAnotherData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EventAnotherFragment extends EventFragment {

    private static final String LAYOUT_NAME = "Другое";
    private static final String SERVER_MORE_LOAD = "more_another_event";

    private long mDataCountMax = -1;

    public static EventAnotherFragment newInstance() {

        Bundle args = new Bundle();

        EventAnotherFragment fragment = new EventAnotherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public EventAnotherFragment() {
        super(R.layout.event_item_another, EventsAnotherData.getInstance().getData());
        initObservable(EventsAnotherData.getInstance());
        EventsAnotherData.getInstance().getCountEvent(new EventsData.AsyncCountGetter() {
            @Override
            public void result(long count) {
                mDataCountMax = count;
            }
        });
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected int getTypeEvent() {
        return EventsData.EVENT_ANOTHER_TYPE;
    }

    @Override
    protected long getDataCountMax() {
        return mDataCountMax;
    }

    @Override
    public String getNameLayout() {
        return LAYOUT_NAME;
    }

    @Override
    protected String getServerListenLoadMore() {
        return SERVER_MORE_LOAD;
    }
}
