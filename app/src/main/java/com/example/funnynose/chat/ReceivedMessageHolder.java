package com.example.funnynose.chat;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.funnynose.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {

    private static final Locale locale = new Locale("ru", "RU");
    private static final SimpleDateFormat sHoursMinutes = new SimpleDateFormat("HH:mm", locale);

    private TextView messageText, timeText, nameText;

    ReceivedMessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        timeText = itemView.findViewById(R.id.text_message_time);
        nameText = itemView.findViewById(R.id.text_message_name);
    }

    void bind(Message message) {
        messageText.setText(message.text);
        timeText.setText(sHoursMinutes.format(message.time));
        nameText.setText(message.nickname);
    }
}