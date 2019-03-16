package com.example.funnynose.events;

import android.os.Bundle;
import android.view.View;

import com.example.funnynose.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EventTrainingFragment extends EventFragment {

    private int mDataCount;
    private int mDataCountMax = 1000; // TODO: check in db later

    public static EventTrainingFragment newInstance() {

        Bundle args = new Bundle();

        EventTrainingFragment fragment = new EventTrainingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public EventTrainingFragment() {
        super(R.layout.event_item_training, EventsData.getInstance().getEventsTraining());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton button = view.findViewById(R.id.create_event);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(getView(), "Training", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getDataCount() {
        return mDataCount;
    }

    @Override
    protected int getDataCountMax() {
        return mDataCountMax;
    }

    @Override
    protected void setDataCount(int count) {
        mDataCount = count;
    }
}
