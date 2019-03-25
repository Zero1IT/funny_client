package com.example.funnynose.chat.holders;

import android.view.View;
import android.widget.TextView;

import com.example.funnynose.R;
import com.example.funnynose.chat.Message;

import static com.example.funnynose.Utilities.dateFormat;

public class MessageHolderWithDivider extends MessageHolder {

    private TextView dividerDate;

    public MessageHolderWithDivider(View itemView) {
        super(itemView);
        dividerDate = itemView.findViewById(R.id.message_divider_date);
    }

    @Override
    public void bind(Message msg) {
        super.bind(msg);
        dividerDate.setText(dateFormat.format(msg.time));
    }
}
