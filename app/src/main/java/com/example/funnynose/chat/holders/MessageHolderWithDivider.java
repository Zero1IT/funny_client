package com.example.funnynose.chat.holders;

import android.view.View;

import com.example.funnynose.chat.Message;

import static com.example.funnynose.Utilities.DATE_FORMAT;

public class MessageHolderWithDivider extends MessageHolder {

    public MessageHolderWithDivider(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(Message msg) {
        super.bind(msg);
        setDividerText(DATE_FORMAT.format(msg.time));
    }
}
