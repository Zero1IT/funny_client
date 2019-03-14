package com.example.funnynose.chat.holders;


import android.view.View;

import com.example.funnynose.R;
import com.example.funnynose.chat.Message;

public class SentMessageHolder extends AbstractMessageHolder {

    public SentMessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        timeText = itemView.findViewById(R.id.text_message_time);
    }

    @Override
    public void bind(Message msg) {
        messageText.setText(msg.text);
        timeText.setText(sHoursMinutes.format(msg.time));
    }
}