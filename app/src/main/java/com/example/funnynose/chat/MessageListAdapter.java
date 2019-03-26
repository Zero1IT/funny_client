package com.example.funnynose.chat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.funnynose.R;
import com.example.funnynose.chat.holders.MessageHolderWithDivider;
import com.example.funnynose.chat.holders.ReceivedMessageHolder;
import com.example.funnynose.chat.holders.MessageHolder;
import com.example.funnynose.User;
import com.example.funnynose.chat.holders.ReceivedMessageHolderWithDivider;
import com.example.funnynose.db.ChatCache;

import java.util.ArrayList;

import static com.example.funnynose.Utilities.DATE_FORMAT;


public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 0;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 1;
    private static final int VIEW_TYPE_MESSAGE_SENT_WITH_DIVIDER = 2;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED_WITH_DIVIDER = 3;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED_NO_NAME = 4;
    private static final int VIEW_TYPE_MESSAGE_SENT_NOT_FIRST = 5;

    private int mLastItemPosition = -1;

    private ArrayList<Message> mMessageList;
    private ChatFragment mChatFragment;

    MessageListAdapter(ArrayList<Message> messageList, ChatFragment fragment) {
        mMessageList = messageList;
        mChatFragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new MessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_SENT_WITH_DIVIDER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new MessageHolderWithDivider(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED_WITH_DIVIDER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolderWithDivider(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED_NO_NAME) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new MessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((MessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_SENT_WITH_DIVIDER:
                ((MessageHolderWithDivider) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED_WITH_DIVIDER:
                ((ReceivedMessageHolderWithDivider) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED_NO_NAME:
                ((ReceivedMessageHolder) holder).bind(message);
                ((ReceivedMessageHolder) holder).hideName();
                ((ReceivedMessageHolder) holder).setTotalRoundedRectangle();
                break;
            case VIEW_TYPE_MESSAGE_SENT_NOT_FIRST:
                ((MessageHolder) holder).bind(message);
                ((MessageHolder) holder).setTotalRoundedRectangle();
                break;
        }

        if (position < mLastItemPosition) { // скролл вверх
            mChatFragment.refreshChat();
            mChatFragment.showButtonDown(-1);
        } else {
            if (position - mLastItemPosition < ChatCache.ONE_TIME_PACKAGE_SIZE - 10) {
                mChatFragment.showButtonDown(position);
            } else {
                mChatFragment.showButtonDown(-1);
            }
        }
        mChatFragment.updateTextDate(position);

        mLastItemPosition = position;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);
        Message previousMessage = null;
        if (position > 0) {
            previousMessage = mMessageList.get(position - 1);
        }

        if (message.nickname.equals(User.mStringData.get("nickname"))) {
            if (position == 0) {
                return VIEW_TYPE_MESSAGE_SENT_WITH_DIVIDER;
            } else if (!DATE_FORMAT.format(message.time)
                    .equals(DATE_FORMAT.format(previousMessage.time))) {
                return VIEW_TYPE_MESSAGE_SENT_WITH_DIVIDER;
            } else if (message.nickname.equals(previousMessage.nickname)) {
                return VIEW_TYPE_MESSAGE_SENT_NOT_FIRST;
            } else {
                return VIEW_TYPE_MESSAGE_SENT;
            }
        } else {
            if (position == 0) {
                return VIEW_TYPE_MESSAGE_RECEIVED_WITH_DIVIDER;
            } else if (!DATE_FORMAT.format(message.time)
                    .equals(DATE_FORMAT.format(previousMessage.time))) {
                return VIEW_TYPE_MESSAGE_RECEIVED_WITH_DIVIDER;
            } else {
                if (!message.nickname.equals(previousMessage.nickname)) {
                    return VIEW_TYPE_MESSAGE_RECEIVED;
                } else {
                    return VIEW_TYPE_MESSAGE_RECEIVED_NO_NAME;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}