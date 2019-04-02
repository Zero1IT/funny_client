package com.example.funnynose.events;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.funnynose.R;
import com.example.funnynose.events.Support.EventsHospitalData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EventHospitalFragment extends EventFragment {

    private static final String LAYOUT_NAME = "Госпиталь";
    private static final String SERVER_MORE_LOAD = "more_hospital_event";

    private long mDataCountMax = -1;

    public static EventHospitalFragment newInstance() {

        Bundle args = new Bundle();

        EventHospitalFragment fragment = new EventHospitalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public EventHospitalFragment() {
        super(R.layout.event_item_hospital, EventsHospitalData.getInstance().getData());
        initObservable(EventsHospitalData.getInstance());
        EventsHospitalData.getInstance().getCountEvent(new EventsData.AsyncCountGetter() {
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
        return EventsData.EVENT_HOSPITAL_TYPE;
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
