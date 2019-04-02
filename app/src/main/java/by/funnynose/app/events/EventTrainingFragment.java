package by.funnynose.app.events;

import android.os.Bundle;
import android.view.View;

import com.example.funnynose.R;

import by.funnynose.app.events.Support.EventsTrainingData;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EventTrainingFragment extends EventFragment {

    private static final String LAYOUT_NAME = "Тренинги";
    private static final String SERVER_MORE_LOAD = "more_training_event";

    private long mDataCountMax = -1;

    public static EventTrainingFragment newInstance() {

        Bundle args = new Bundle();

        EventTrainingFragment fragment = new EventTrainingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public EventTrainingFragment() {
        super(R.layout.event_item_training, EventsTrainingData.getInstance().getData());
        initObservable(EventsTrainingData.getInstance());
        EventsTrainingData.getInstance().getCountEvent(new EventsData.AsyncCountGetter() {
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
        return EventsData.EVENT_TRAINING_TYPE;
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
