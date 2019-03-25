package com.example.funnynose.chat.holders;

import android.view.View;
import android.widget.TextView;

import com.example.funnynose.R;
import com.example.funnynose.chat.Message;


public class ReceivedMessageHolder extends MessageHolder {

    private TextView nameText;

    public ReceivedMessageHolder(View itemView) {
        super(itemView);
        nameText = itemView.findViewById(R.id.message_name);
    }

    @Override
    public void bind(Message msg) {
        super.bind(msg);
        nameText.setText(msg.nickname);
    }

    public void hideName() {
        nameText.setVisibility(View.GONE);
    }

    @Override
    public void setTotalRoundedRectangle() {
        messageText.setBackgroundResource(R.drawable.total_rounded_rectangle_received);
    }
}