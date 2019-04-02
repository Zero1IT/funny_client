package by.funnynose.app.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.funnynose.R;
import by.funnynose.app.adapters.ScrollEventsAdapter;
import by.funnynose.app.constants.Session;
import by.funnynose.app.events.Support.LoadMoreHolder;
import by.funnynose.app.network.SocketAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.socket.emitter.Emitter;

public abstract class EventFragment extends Fragment implements Observer {

    private static final String SERVER_LOAD_MORE = "load_more_data_event";
    private static final int DELAY = 1000;
    private int mTypeLayout;
    private RecyclerView mRecyclerView;
    private Observable mObservable;
    private FloatingActionButton mCreateEventButton;
    private ScrollEventsAdapter mAdapter;
    private List<Event> mEvents;

    private EventFragment(int typeLayout) { mTypeLayout = typeLayout; }

    EventFragment(int typeLayout, List<Event> data) {
        this(typeLayout);
        mEvents = data;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.events_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCreateEventButton = view.findViewById(R.id.create_event);
        mCreateEventButton.setOnClickListener(createEventClick);
        initAdapter(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == CreateEventActivity.IDENTITY) {
            if (data != null) {
                Event event = (Event) data.getSerializableExtra(CreateEventActivity.NAME_KEY);
                mAdapter.addData(0, event);
            }
        }
    }

    @Override
    public void update(Observable o, final Object arg) {
        if (arg instanceof Event) {
            if (getActivity() != null)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addData(0, (Event) arg);
                    }
                });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mObservable.deleteObserver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mObservable.addObserver(this);
        if (getContext() != null && !SocketAPI.isOnline(getContext())) {
            mCreateEventButton.setVisibility(View.GONE);
        } else {
            mCreateEventButton.setVisibility(View.VISIBLE);
        }
    }

    void initObservable(Observable observable) {
        mObservable = observable;
        mObservable.addObserver(this);
    }

    private void initAdapter(@NonNull View view) {
        mRecyclerView = view.findViewById(R.id.scroll_view);
        mAdapter = new ScrollEventsAdapter(mTypeLayout, mEvents);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mAdapter.setOnLoadMoreListener(loadMore, mRecyclerView);
        mAdapter.setOnItemClickListener(itemClickListen);
        mAdapter.setLoadMoreView(new LoadMoreHolder());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    private final View.OnClickListener createEventClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), CreateEventActivity.class);
            intent.putExtra(CreateEventActivity.NAME_KEY, getNameLayout());
            startActivityForResult(intent, CreateEventActivity.IDENTITY);
        }
    };

    private final BaseQuickAdapter.OnItemClickListener itemClickListen
            = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            if (getView() != null)
                Snackbar.make(getView(), position + ". " + mEvents.get(position).getTitle(),
                        Snackbar.LENGTH_SHORT).show();
        }
    };

    private final BaseQuickAdapter.RequestLoadMoreListener loadMore =
            new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mEvents.size() >= getDataCountMax()) {
                        mAdapter.loadMoreEnd();
                    } else {
                        if (SocketAPI.isOnline()) {
                            loadMoreData();
                        } else {
                            if (getView() != null)
                                Snackbar.make(getView(), "Ошибка загрузки", Snackbar.LENGTH_SHORT).show();
                            mAdapter.loadMoreFail();
                        }
                    }
                }
            }, DELAY);
        }
    };

    private void loadMoreData() {
        mAdapter.loadMoreComplete();
        JSONObject args = new JSONObject();
        try {
            args.put("id", mEvents.get(mEvents.size() - 1).getId());
            args.put("type", getTypeEvent());
        } catch (JSONException e) {
            Log.e(Session.TAG, e.getMessage());
        }
        SocketAPI.getSocket().emit(SERVER_LOAD_MORE, args)
                .once(getServerListenLoadMore(), new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray eventArray = (JSONArray) args[0];
                List<Event> moreEvents = new ArrayList<>();
                try {
                    for (int i = 0; i < eventArray.length(); i++) {
                        moreEvents.add(new Event((JSONObject) eventArray.get(i)));
                    }
                } catch (JSONException e) {
                    Log.e(Session.TAG, e.getMessage());
                }
                synchronized (mAdapter.getData()) {
                    mAdapter.addData(moreEvents);
                }
            }
        });
    }

    protected abstract int getTypeEvent();
    protected abstract long getDataCountMax();
    protected abstract String getServerListenLoadMore();
    public abstract String getNameLayout();
}
