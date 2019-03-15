package com.example.funnynose.chat.holders;

import android.view.View;
import android.widget.TextView;

import com.example.funnynose.chat.Message;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class AbstractMessageHolder extends RecyclerView.ViewHolder{
    private static final Locale locale = new Locale("ru", "RU");
    static final SimpleDateFormat sHoursMinutes = new SimpleDateFormat("HH:mm", locale);

    TextView messageText, timeText;

    AbstractMessageHolder(@NonNull View itemView) {
        super(itemView);
    }

    protected void bind(Message msg) {}
}
