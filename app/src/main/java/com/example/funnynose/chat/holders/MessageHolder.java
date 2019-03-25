package com.example.funnynose.chat.holders;

import android.view.View;
import android.widget.TextView;

import com.example.funnynose.R;
import com.example.funnynose.chat.Message;

import androidx.recyclerview.widget.RecyclerView;

import static com.example.funnynose.Utilities.hoursMinutes;

public class MessageHolder extends RecyclerView.ViewHolder {

    TextView messageText;
    private TextView timeText;

    public MessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.message_text);
        timeText = itemView.findViewById(R.id.message_time);
    }

    public void bind(Message msg) {
        messageText.setText(msg.text);
        timeText.setText(hoursMinutes.format(msg.time));
    }

    public void setTotalRoundedRectangle() {
        messageText.setBackgroundResource(R.drawable.total_rounded_rectangle_sent);
    }
}