package com.example.funnynose.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.funnynose.R;
import com.example.funnynose.events.Event;

import java.util.List;

public class ScrollEventsAdapter extends BaseQuickAdapter<Event, BaseViewHolder> {

    public ScrollEventsAdapter(int resLay, List<Event> data) {
        super(resLay, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Event item) {
//        helper.bind(item);
        helper.setText(R.id.event_date, item.getDate());
    }
}
