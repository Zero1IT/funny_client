package by.funnynose.app.chat;


import android.util.Log;

import by.funnynose.app.db.ChatCache;
import by.funnynose.app.network.AsyncServerResponse;
import by.funnynose.app.network.SocketAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.socket.emitter.Emitter;

import java.util.ArrayList;


class ChatUpdater {

    private String mChatName;

    private RecyclerView mRecyclerView;
    private MessageListAdapter mMessageListAdapter;

    private FragmentActivity mFragmentActivity;

    private ArrayList<Message> mMessageList;

    private ChatCache mChatCache;

    private AsyncServerResponse mResponseRefreshMessages;

    private Object mObject;

    private boolean mIsLoading = false;

    ChatUpdater(FragmentActivity activity, MessageListAdapter adapter,
            RecyclerView recyclerView, ArrayList<Message> messageList, String chatName) {
        mFragmentActivity = activity;
        mMessageListAdapter = adapter;
        mRecyclerView = recyclerView;
        mMessageList = messageList;
        mChatName = chatName;
        mChatCache = new ChatCache(chatName);
        initResponseRefreshMessages();
        initChat();
    }

    private void initChat() {
        if (mMessageList.size() == 0) {
            new Runnable(){
                @Override
                public void run() {
                    mMessageList.addAll(mChatCache.getMessagesFromTo());
                    notifyAllMessagesAndMoveDown();
                }
            }.run();
            if (SocketAPI.isOnline()) {
                final AsyncServerResponse responseLastMessages = new AsyncServerResponse(2000, new AsyncServerResponse.AsyncTask() {
                    @Override
                    public void call() {
                        responseLastMessagesCall();
                    }
                });

                JSONObject obj = new JSONObject();
                try {
                    obj.put("messageKey", mChatCache.getLastMessageKey());
                } catch (JSONException e) {
                    Log.d("DEBUG", e.getMessage());
                }

                SocketAPI.getSocket().emit("compare_last_messages_" + mChatName,obj)
                        .once("compare_last_messages_" + mChatName, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                mObject = args[0];
                                if (((JSONArray) mObject).length() == 0) {
                                    responseLastMessages.setSuccessful(false);
                                } else {
                                    responseLastMessages.setSuccessful(true);
                                }
                                responseLastMessages.setResponse(true);
                            }
                        });

                responseLastMessages.start(mFragmentActivity);
            }

            SocketAPI.getSocket().on("new_message_" + mChatName, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    newMessageCall((JSONObject) args[0]);
                }
            });
        }
    }

    private void initResponseRefreshMessages() {
        mResponseRefreshMessages = new AsyncServerResponse(2000, new AsyncServerResponse.AsyncTask() {
            @Override
            public void call() {
                final JSONArray jsonArray = (JSONArray) mObject;
                JSONObject msgJson;

                for (int i = 0; i < jsonArray.length(); i++) {
                    msgJson = jsonArray.optJSONObject(i);
                    Message msg = new Message(msgJson.optString("messageText"), msgJson.optString("nickname"),
                            msgJson.optLong("messageTime"), msgJson.optLong("id_"));
                    mMessageList.add(0, msg);
                    mChatCache.addMessage(msg);
                }
                notifyTopMessages(jsonArray.length());
                mIsLoading = false;
            }
        });
    }

    private void newMessageCall(JSONObject msgJson) {
        Message msg = new Message(msgJson.optString("message_text"), msgJson.optString("nickname"),
                msgJson.optLong("time"), msgJson.optLong("key"));
        mMessageList.add(msg);
        mChatCache.addMessage(msg);

        mFragmentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageListAdapter.notifyItemInserted(mMessageList.size() - 1);

                if (mMessageList.size() < 30) {
                    mRecyclerView.smoothScrollToPosition(mMessageList.size() - 1);
                } else {
                    int lastVisible = 0;
                    if (mRecyclerView.getLayoutManager() != null) {
                        lastVisible = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                                .findLastVisibleItemPosition();
                    }
                    if (lastVisible + 25 > mMessageList.size()) {
                        mRecyclerView.smoothScrollToPosition(mMessageList.size() - 1);
                    }
                }
            }
        });
    }

    private void responseLastMessagesCall() {
        JSONArray jsonArray = (JSONArray) mObject;
        JSONObject msgJson;
        ArrayList<Message> tempList = new ArrayList<>();

        for (int i = jsonArray.length() - 1; i >= 0; i--) {
            msgJson = jsonArray.optJSONObject(i);
            Message msg = new Message(msgJson.optString("messageText"), msgJson.optString("nickname"),
                    msgJson.optLong("messageTime"), msgJson.optLong("id_"));
            mChatCache.addMessage(msg);

            if (jsonArray.length() >= ChatCache.ONE_TIME_PACKAGE_SIZE) {
                tempList.add(msg);
            } else {
                mMessageList.add(msg);
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mMessageListAdapter.notifyItemInserted(mMessageList.size() - 1);
                    }
                });
            }
        }
        if (jsonArray.length() >= ChatCache.ONE_TIME_PACKAGE_SIZE) {
            mMessageList.clear();
            mMessageList.addAll(tempList);
            notifyAllMessagesAndMoveDown();
        }
    }

    void refreshChat() {
        if (mIsLoading) {
            return;
        }

        long lastMessageKey = getLastMessageKey();
        if (lastMessageKey == 1) {
            return;
        }
        mIsLoading = true;
        final ArrayList<Message> messages = mChatCache.getMessagesFromTo(lastMessageKey);
        if (!SocketAPI.isOnline(mFragmentActivity)) {
            mMessageList.addAll(0, messages);
            notifyTopMessages(messages.size());
            mIsLoading = false;
        } else {
            if (messages.size() > 0) {
                if (messages.size() - 1 == messages.get(messages.size() - 1).key - messages.get(0).key) {
                    mMessageList.addAll(0, messages);
                    notifyTopMessages(messages.size());
                    mIsLoading = false;
                    return;
                }
            }
            boolean exitByBreak = addMessagesWithoutBreaks(messages, lastMessageKey);
            if (exitByBreak) {
                requestRefreshMessages(lastMessageKey);
            }
        }
    }


    private void notifyTopMessages(final int size) {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mMessageListAdapter.notifyItemRangeInserted(0, size);
            }
        });
    }

    private void notifyAllMessagesAndMoveDown() {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mMessageListAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(mMessageList.size() - 1);
            }
        });
    }

    boolean getIsLoading() {
        return mIsLoading;
    }

    private long getLastMessageKey() {
        long lastMessageKey = 0;
        if (mMessageList.size() > 0) {
            lastMessageKey = mMessageList.get(0).key;
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
                mMessageList.add(0, msg);
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
        SocketAPI.getSocket().emit("get_messages_from_to_" + mChatName, obj)
                .once("get_messages_from_to_" + mChatName, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        mObject = args[0];
                        mResponseRefreshMessages.setSuccessful(true);
                        mResponseRefreshMessages.setResponse(true);
                    }
                });
        mResponseRefreshMessages.start(mFragmentActivity);
    }

    void addNewMessage(Message msg) {
        mMessageList.add(msg);
        mChatCache.addMessage(msg);
        mFragmentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageListAdapter.notifyItemInserted(mMessageList.size() - 1);
            }
        });
    }
}