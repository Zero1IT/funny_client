package by.funnynose.app.chat.holders;

import android.view.View;
import android.widget.TextView;

import com.example.funnynose.R;
import by.funnynose.app.chat.Message;

public class ReceivedMessageHolder extends MessageHolder {

    private TextView mNameView;

    public ReceivedMessageHolder(View itemView) {
        super(itemView);
        mNameView = itemView.findViewById(R.id.message_name);
    }

    @Override
    public void bind(Message msg) {
        super.bind(msg);
        mNameView.setText(msg.nickname);
    }

    public void hideName() {
        mNameView.setVisibility(View.GONE);
    }

    @Override
    public void setTotalRoundedRectangle() {
        mTextView.setBackgroundResource(R.drawable.total_rounded_rectangle_received);
    }
}