package com.example.funnynose.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.funnynose.R;
import com.example.funnynose.adapters.ScrollEventsAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class EventFragment extends Fragment {

    private static final int DELAY = 1000;
    private int mTypeLayout;
    private RecyclerView mRecyclerView;
    private ScrollEventsAdapter mAdapter;
    private List<Event> mEvents;
    private boolean mErrorLoadData;

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
        mErrorLoadData = false; // TODO: check in db later
        initAdapter(view);
    }

    private void initAdapter(@NonNull View view) {
        mRecyclerView = view.findViewById(R.id.scroll_view);
        mAdapter = new ScrollEventsAdapter(mTypeLayout, mEvents);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mAdapter.setOnLoadMoreListener(loadMore, mRecyclerView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (getView() != null)
                    Snackbar.make(getView(), position + ". " + mEvents.get(position).getTitle(),
                            Snackbar.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    private final BaseQuickAdapter.RequestLoadMoreListener loadMore =
            new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getDataCount() >= getDataCountMax()) {
                        mAdapter.loadMoreEnd();
                    } else {
                        if (!mErrorLoadData) {
                            mAdapter.addData(new ArrayList<>(mEvents));
                            setDataCount(mAdapter.getData().size());
                            mAdapter.loadMoreComplete();
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

    protected abstract int getDataCount();
    protected abstract int getDataCountMax();
    protected abstract void setDataCount(int count);
}
