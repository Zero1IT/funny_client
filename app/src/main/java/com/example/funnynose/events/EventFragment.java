package com.example.funnynose.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.funnynose.R;
import com.example.funnynose.adapters.ScrollEventsAdapter;
import com.example.funnynose.constants.Session;
import com.example.funnynose.network.SocketAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
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

    private static final int DELAY = 1000;
    private int mTypeLayout;
    private RecyclerView mRecyclerView;
    private boolean mErrorLoadData;
    private long mCurrentCountItem;
    private FloatingActionButton mCreateEventButton;
    boolean mLoadingMore;
    boolean mLoadedNewEvent;
    ScrollEventsAdapter mAdapter;
    List<Event> mEvents;
    List<Event> mNewAddedEvents;

    private EventFragment() {}

    EventFragment(int typeLayout, List<Event> data) {
        mTypeLayout = typeLayout;
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
        mNewAddedEvents = new ArrayList<>();
        mCreateEventButton = view.findViewById(R.id.create_event);
        mCreateEventButton.setOnClickListener(createEventClick);
        mLoadingMore = false;
        mLoadedNewEvent = false;
        mErrorLoadData = false; // TODO: check in db later
        initAdapter(view);
        initServerListener();
        EventsData.getInstance().addObserver(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode - 1 == Activity.RESULT_OK) {
            if (requestCode == CreateEventActivity.IDENTITY) {
                Event e = new Event();
                e.setIcon(getNameLayout().equals("Другое") ? Event.ICON_ANOTHER : (getNameLayout().equals("Госпиталь") ? Event.ICON_HOSPITAL : Event.ICON_TRAINING));
                e.setFinished(true);
                e.setDate(new Date());
                e.setTitle("From activity");
                e.setId(-1);
                e.setDurationEvent(228);
                Log.d(Session.TAG, mAdapter.getItemCount() + "");
                Log.d(Session.TAG, mEvents.size() + "");
                mAdapter.addData(0, e);
                Log.d(Session.TAG, mAdapter.getItemCount() + "");
                Log.d(Session.TAG, mEvents.size() + "");
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o != null) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(Session.TAG, "Current = " + mCurrentCountItem);
                        Log.d(Session.TAG, "size = " + mEvents.size());
                        if (mCurrentCountItem < mEvents.size()) {
                            mAdapter.notifyItemInserted(0);
                            mRecyclerView.scrollToPosition(0);
                            mCurrentCountItem = mEvents.size();
                        }
                    }
                });
            }
        }
    }

    private void initAdapter(@NonNull View view) {
        mRecyclerView = view.findViewById(R.id.scroll_view);
        mAdapter = new ScrollEventsAdapter(mTypeLayout, mEvents);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mAdapter.setOnLoadMoreListener(loadMore, mRecyclerView);
        mAdapter.setOnItemClickListener(itemClickListen);
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
                        if (!mErrorLoadData) {
                            Log.d(Session.TAG, "current = " + mEvents.size() + " : max = " + getDataCountMax());
                            mLoadingMore = true;
                            loadMoreData();
                            mCurrentCountItem = mEvents.size(); // TODO: даун, это ассинхронный метод, что ты творишь
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

    private void initServerListener() {
        SocketAPI.getSocket().emit(getServerListenerName(), "last loaded id event")
                .on(getServerListenerName(), new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {

                    }
                });

        // TODO: for debug, release client listener
        new Thread(new Runnable() {
            @Override
            public void run() {
                int pause = getNameLayout().equals("Другое") ? 3000 : (getNameLayout().equals("Госпиталь") ? 6000 : 9000);
                int count = 0;
                while (true) {
                    final Event event = new Event();
                    event.setTitle("Async load from server - test " + count);
                    event.setDate(new Date());
                    event.setFinished(true);
                    event.setIcon(getNameLayout().equals("Другое") ? Event.ICON_ANOTHER : (getNameLayout().equals("Госпиталь") ? Event.ICON_HOSPITAL : Event.ICON_TRAINING));
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mLoadingMore) {
                                    Log.d(Session.TAG, "more loading");
                                    mLoadedNewEvent = true;
                                    mNewAddedEvents.add(event);
                                } else {
                                    mAdapter.addData(0, event);
                                    Log.d(Session.TAG, "no more loading");
                                }
                            }
                        });
                    }
                    try {
                        Thread.sleep(pause);
                    } catch (InterruptedException e) {
                        Log.d(Session.TAG, e.getMessage());
                    }
                    if (count == 0) pause = 9000;
                    count++;
                }
            }
        }).start();
    }

    //TODO: refactoring and delete this
    protected boolean isLoadedNewEvent() {
        return mLoadedNewEvent;
    }

    protected abstract void loadMoreData();
    protected abstract int getDataCountMax();
    public abstract String getNameLayout();
    public abstract String getServerListenerName();
}
