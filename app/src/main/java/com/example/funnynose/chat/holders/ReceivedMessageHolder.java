package com.example.funnynose.chat.holders;

import android.view.View;
import android.widget.TextView;

import com.example.funnynose.R;
import com.example.funnynose.chat.Message;

public class ReceivedMessageHolder extends AbstractMessageHolder {

    private TextView nameText;

    public ReceivedMessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        timeText = itemView.findViewById(R.id.text_message_time);
        nameText = itemView.findViewById(R.id.text_message_name);
    }

    @Override
    public void bind(Message msg) {
        messageText.setText(msg.text);
        timeText.setText(sHoursMinutes.format(msg.time));
        nameText.setText(msg.nickname);
    }
}