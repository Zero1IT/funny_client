package by.funnynose.app.chat.holders;

import android.view.View;
import android.widget.TextView;

import com.example.funnynose.R;
import by.funnynose.app.chat.Message;

import androidx.recyclerview.widget.RecyclerView;
import by.funnynose.app.Utilities;

public class MessageHolder extends RecyclerView.ViewHolder {

    private TextView mDividerView;
    TextView mTextView;
    private TextView mTimeView;

    public MessageHolder(View itemView) {
        super(itemView);
        mDividerView = itemView.findViewById(R.id.message_divider);
        mTextView = itemView.findViewById(R.id.message_text);
        mTimeView = itemView.findViewById(R.id.message_time);
    }

    public void bind(Message msg) {
        mDividerView.setVisibility(View.GONE);
        mTextView.setText(msg.text);
        mTimeView.setText(Utilities.HOURS_MINUTES.format(msg.time));
    }

    void setDividerText(String text) {
        if (text != null) {
            mDividerView.setText(text);
            mDividerView.setVisibility(View.VISIBLE);
        }
    }

    public void setTotalRoundedRectangle() {
        mTextView.setBackgroundResource(R.drawable.total_rounded_rectangle_sent);
    }
}