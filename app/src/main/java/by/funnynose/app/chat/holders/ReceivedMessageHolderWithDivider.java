package by.funnynose.app.chat.holders;

import android.view.View;

import by.funnynose.app.chat.Message;

import by.funnynose.app.Utilities;

public class ReceivedMessageHolderWithDivider extends ReceivedMessageHolder {

    public ReceivedMessageHolderWithDivider(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(Message msg) {
        super.bind(msg);
        setDividerText(Utilities.DATE_FORMAT.format(msg.time));
    }
}
