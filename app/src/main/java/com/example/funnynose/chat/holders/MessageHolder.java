package com.example.funnynose.chat.holders;

import android.view.View;
import android.widget.TextView;

import com.example.funnynose.R;
import com.example.funnynose.chat.Message;

import androidx.recyclerview.widget.RecyclerView;

import static com.example.funnynose.Utilities.sHoursMinutes;

public class MessageHolder extends RecyclerView.ViewHolder {

    private TextView messageText;
    private TextView timeText;

    public MessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        timeText = itemView.findViewById(R.id.text_message_time);
    }

    public void bind(Message msg) {
        messageText.setText(msg.text);
        timeText.setText(sHoursMinutes.format(msg.time));
    }
}