package com.example.funnynose.adapters;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.example.funnynose.R;
import com.example.funnynose.events.Event;

public class EventHolder extends BaseViewHolder {

    private TextView mTextDate;

    public EventHolder(View itemView) {
        super(itemView);
        mTextDate = itemView.findViewById(R.id.event_item);
    }

    public void bind(Event event) {
        mTextDate.setText(event.getDate());
    }


}
