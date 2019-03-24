package com.example.funnynose.chat;


import android.util.Log;

import com.example.funnynose.db.ChatCache;
import com.example.funnynose.network.AsyncServerResponse;
import com.example.funnynose.network.SocketAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.socket.emitter.Emitter;

import java.util.ArrayList;


class ChatUpdater {

    private String chatName;

    private RecyclerView mMessageList;
    private MessageListAdapter adapter;

    private FragmentActivity activity;

    private ArrayList<Message> messageArray;

    private ChatCache chatCache;

    private AsyncServerResponse responseLastMessages;
    private AsyncServerResponse responseRefreshMessages;

    private Object object;

    private boolean isLoading;

    ChatUpdater(FragmentActivity activity, MessageListAdapter adapter,
                RecyclerView mMessageList, ArrayList<Message> messageArray, String chatName) {
        this.activity = activity;
        this.adapter = adapter;
        this.mMessageList = mMessageList;
        this.messageArray = messageArray;
        this.chatName = chatName;
        chatCache = new ChatCache(chatName);
        isLoading = false;
        initResponseRefreshMessages();
        initChat();
    }

    private void initChat() {
        if (messageArray.size() == 0) {
            if (SocketAPI.isOnline()) {
                initResponseLastMessages();

                JSONObject obj = new JSONObject();
                try {
                    obj.put("messageKey", chatCache.getLastMessageKey());
                } catch (JSONException e) {
                    Log.d("DEBUG", e.getMessage());
                }

                SocketAPI.getSocket().emit("compare_last_messages_" + chatName,obj)
                        .once("compare_last_messages_" + chatName, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                object = args[0];
                                if (((JSONArray) object).length() == 0) {
                                    responseLastMessages.setSuccessful(false);
                                } else {
                                    responseLastMessages.setSuccessful(true);
                                }
                                responseLastMessages.setResponse(true);
                            }
                        });
                responseLastMessages.start(activity);

            } else {
                messageArray.addAll(chatCache.getMessagesFromTo());
                notifyAllMessagesAndMoveDown();
            }

            SocketAPI.getSocket().on("new_message_" + chatName, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    newMessageCall((JSONObject) args[0]);
                }
            });
        }
    }

    private void initResponseLastMessages() {

        responseLastMessages = new AsyncServerResponse(2000, new AsyncServerResponse.AsyncTask() {
            @Override
            public void call() {
                responseLastMessagesCall();
            }
        });

        responseLastMessages.setFailResponse(new AsyncServerResponse.AsyncTask() {
            @Override
            public void call() {
                messageArray.addAll(chatCache.getMessagesFromTo());
                notifyAllMessagesAndMoveDown();
            }
        });

        responseLastMessages.setFailSuccessful(new AsyncServerResponse.AsyncTask() {
            @Override
            public void call() {
                messageArray.addAll(chatCache.getMessagesFromTo());
                notifyAllMessagesAndMoveDown();
            }
        });
    }

    private void initResponseRefreshMessages() {
        responseRefreshMessages = new AsyncServerResponse(2000, new AsyncServerResponse.AsyncTask() {
            @Override
            public void call() {
                final JSONArray jsonArray = (JSONArray) object;
                JSONObject msgJson;

                for (int i = 0; i < jsonArray.length(); i++) {
                    msgJson = jsonArray.optJSONObject(i);
                    Message msg = new Message(msgJson.optString("messageText"), msgJson.optString("nickname"),
                            msgJson.optLong("messageTime"), msgJson.optLong("id_"));
                    messageArray.add(0, msg);
                    chatCache.addMessage(msg);
                }

                notifyTopMessages(jsonArray.length());
            }
        });
    }

    private void newMessageCall(JSONObject msgJson) {
        Message msg = new Message(msgJson.optString("message_text"), msgJson.optString("nickname"),
                msgJson.optLong("time"), msgJson.optLong("key"));
        messageArray.add(msg);
        chatCache.addMessage(msg);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemInserted(messageArray.size() - 1);

                if (messageArray.size() < 20) {
                    mMessageList.smoothScrollToPosition(messageArray.size() - 1);
                } else {
                    int lastVisible = 0;
                    if (mMessageList.getLayoutManager() != null) {
                        lastVisible = ((LinearLayoutManager) mMessageList.getLayoutManager())
                                .findLastVisibleItemPosition();
                    }
                    if (lastVisible + 15 > messageArray.size()) {
                        mMessageList.smoothScrollToPosition(messageArray.size() - 1);
                    }
                }

            }
        });
    }

    private void responseLastMessagesCall() {
        JSONArray jsonArray = (JSONArray) object;
        JSONObject msgJson;

        for (int i = jsonArray.length() - 1; i >= 0; i--) {
            msgJson = jsonArray.optJSONObject(i);
            Message msg = new Message(msgJson.optString("messageText"), msgJson.optString("nickname"),
                    msgJson.optLong("messageTime"), msgJson.optLong("id_"));
            messageArray.add(msg);
            chatCache.addMessage(msg);
        }

        if (jsonArray.length() < ChatCache.ONE_TIME_PACKAGE_SIZE) {
            long to = messageArray.get(0).key;
            long from = to - ChatCache.ONE_TIME_PACKAGE_SIZE;
            if (from < 0) {
                from = 0;
            }
            messageArray.addAll(0, chatCache.getMessagesFromTo(from, to));
        }

        notifyAllMessagesAndMoveDown();
    }


    void refreshChat() {
        if (isLoading) {
            return;
        }

        long lastMessageKey = getLastMessageKey();
        if (lastMessageKey == 1) {
            return;
        }
        isLoading = true;
        final ArrayList<Message> messages = chatCache.getMessagesFromTo(lastMessageKey);
        if (!SocketAPI.isOnline(activity)) {
            messageArray.addAll(0, messages);
            notifyTopMessages(messages.size());
            isLoading = false;
        } else {
            if (messages.size() > 0) {
                if (messages.size() - 1 == messages.get(messages.size() - 1).key - messages.get(0).key) {
                    messageArray.addAll(0, messages);
                    notifyTopMessages(messages.size());
                    isLoading = false;
                    return;
                }
            }
            boolean exitByBreak = addMessagesWithoutBreaks(messages, lastMessageKey);
            if (exitByBreak) {
                requestRefreshMessages(lastMessageKey);
            }
            isLoading = false;
        }
    }


    private void notifyTopMessages(final int size) {
        mMessageList.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemRangeInserted(0, size);
            }
        });
    }

    private void notifyAllMessagesAndMoveDown() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                mMessageList.scrollToPosition(messageArray.size() - 1);
            }
        });
    }

    boolean getIsLoading() {
        return isLoading;
    }

    private long getLastMessageKey() {
        long lastMessageKey = 0;
        if (messageArray.size() > 0) {
            lastMessageKey = messageArray.get(0).key;
            if (lastMessageKey == 1) {
                return lastMessageKey;
            }
        }
        return lastMessageKey;
    }

    private boolean addMessagesWithoutBreaks(ArrayList<Message> messages, long lastMessageKey) {
        int size = 0;
        boolean exitByBreak = true;
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message msg = messages.get(i);
            if (lastMessageKey - msg.key == 1) {
                messageArray.add(0, msg);
                lastMessageKey = msg.key;
                size++;
                exitByBreak = false;
            } else {
                exitByBreak = true;
                break;
            }
        }
        notifyTopMessages(size);
        return exitByBreak;
    }

    private void requestRefreshMessages(long lastMessageKey) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("to", lastMessageKey);
        } catch (JSONException e) {
            Log.d("DEBUG", e.getMessage());
        }
        SocketAPI.getSocket().emit("get_messages_from_to_" + chatName, obj)
                .once("get_messages_from_to_" + chatName, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        object = args[0];
                        responseRefreshMessages.setSuccessful(true);
                        responseRefreshMessages.setResponse(true);
                    }
                });
        responseRefreshMessages.start(activity);
    }

    void addNewMessage(Message msg) {
        messageArray.add(msg);
        chatCache.addMessage(msg);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemInserted(messageArray.size() - 1);
            }
        });
    }
}