package com.example.funnynose.adapters;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.funnynose.R;
import com.example.funnynose.events.Event;

import java.util.List;

import androidx.annotation.NonNull;

public class ScrollEventsAdapter extends BaseQuickAdapter<Event, BaseViewHolder> {

    public ScrollEventsAdapter(int resLay, List<Event> data) {
        super(resLay, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Event item) {
        helper.setText(R.id.event_date, item.getStringDate());
        helper.setText(R.id.event_title, item.getTitle());
        if (item.isFinished()) {
            helper.setImageResource(R.id.event_finish, item.getIcon());
            helper.itemView.findViewById(R.id.event_finish).setVisibility(View.VISIBLE);
        } else {
            helper.itemView.findViewById(R.id.event_finish).setVisibility(View.INVISIBLE);
        }
    }
}
