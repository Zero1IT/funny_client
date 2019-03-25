package com.example.funnynose.chat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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

import static com.example.funnynose.Utilities.dateFormat;


public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 0;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 1;
    private static final int VIEW_TYPE_MESSAGE_SENT_WITH_DIVIDER = 2;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED_WITH_DIVIDER = 3;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED_NO_NAME = 4;
    private static final int VIEW_TYPE_MESSAGE_SENT_NOT_FIRST = 5;

    private int lastItemPosition = -1;

    private ArrayList<Message> messageList;
    private ChatFragment fragment;

    MessageListAdapter(ArrayList<Message> messageList, ChatFragment fragment) {
        this.messageList = messageList;
        this.fragment = fragment;
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
                    .inflate(R.layout.item_message_sent_with_divider, parent, false);
            return new MessageHolderWithDivider(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED_WITH_DIVIDER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received_with_divider, parent, false);
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
        Message message = messageList.get(position);

        if (position < lastItemPosition) { // скролл вверх
            fragment.refreshChat();
        }

        fragment.updateTextDate(position);

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
        lastItemPosition = position;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        Message previousMessage = null;
        if (position > 0) {
            previousMessage = messageList.get(position - 1);
        }

        if (message.nickname.equals(User.stringData.get("nickname"))) {
            if (position == 0) {
                return VIEW_TYPE_MESSAGE_SENT_WITH_DIVIDER;
            } else if (!dateFormat.format(message.time)
                    .equals(dateFormat.format(previousMessage.time))) {
                return VIEW_TYPE_MESSAGE_SENT_WITH_DIVIDER;
            } else if (message.nickname.equals(previousMessage.nickname)) {
                return VIEW_TYPE_MESSAGE_SENT_NOT_FIRST;
            } else {
                return VIEW_TYPE_MESSAGE_SENT;
            }
        } else {
            if (position == 0) {
                return VIEW_TYPE_MESSAGE_RECEIVED_WITH_DIVIDER;
            } else if (!dateFormat.format(message.time)
                    .equals(dateFormat.format(previousMessage.time))) {
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
        return messageList.size();
    }
}