package com.example.funnynose.chat.holders;

import android.view.View;
import android.widget.TextView;

import com.example.funnynose.R;
import com.example.funnynose.chat.Message;


public class ReceivedMessageHolder extends MessageHolder {

    private TextView nameText;

    public ReceivedMessageHolder(View itemView) {
        super(itemView);
        nameText = itemView.findViewById(R.id.text_message_name);
    }

    @Override
    public void bind(Message msg) {
        super.bind(msg);
        nameText.setText(msg.nickname);
    }
}