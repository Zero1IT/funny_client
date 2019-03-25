package com.example.funnynose.chat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.funnynose.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.funnynose.Utilities.dateFormat;

public class ChatFragment extends Fragment {

    private static final String KEY_CHAT_NAME = "chat_name";

    private LinearLayoutManager layoutManager;
    private RecyclerView mMessageList;
    private TextView mTextDate;
    private FloatingActionButton mButtonDown;

    private ArrayList<Message> messageList = new ArrayList<>();
    private MessageListAdapter adapter;

    private String chatName;
    private ChatUpdater chatUpdater;

    static Fragment newInstance(String chatName) {
        Fragment fragment = new ChatFragment();
        if (chatName != null) {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_CHAT_NAME, chatName);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            chatName = bundle.getString(KEY_CHAT_NAME);
        }
        return inflater.inflate(R.layout.chat_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessageList = view.findViewById(R.id.chat_message_list);

        layoutManager = new WrapContentLinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        mMessageList.setLayoutManager(layoutManager);
        adapter = new MessageListAdapter(messageList, this);
        mMessageList.setAdapter(adapter);
        addListenerToMessageList();

        mButtonDown = view.findViewById(R.id.chat_btn_down);
        mButtonDown.setVisibility(View.GONE);
        mButtonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    moveDown();
            }
        });

        mTextDate = view.findViewById(R.id.chat_day_month);

        chatUpdater = new ChatUpdater(getActivity(), adapter, mMessageList, messageList, chatName);
    }

    private void addListenerToMessageList() {
        mMessageList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                showButtonDown(dy);
            }
        });
    }

    void updateTextDate(int position) {
        if (position > 2) {
            mTextDate.setVisibility(View.VISIBLE);
            try {
                mTextDate.setText(dateFormat.format(messageList.get(layoutManager.
                        findFirstVisibleItemPosition()).time));
            } catch (IndexOutOfBoundsException e) {
                Log.d("DEBUG", e.getMessage());
            }

        } else {
            mTextDate.setVisibility(View.INVISIBLE);
        }
    }

    void refreshChat() {
        if (!chatUpdater.getIsLoading()) {
            if (layoutManager.findFirstVisibleItemPosition() < 25) {
                chatUpdater.refreshChat();
            }
        }
    }

    private void showButtonDown(int dy) {
        if (dy > 0) {
            if (layoutManager.findLastVisibleItemPosition() < adapter.getItemCount() - 1) {
                mButtonDown.show();
            } else {
                mButtonDown.hide();
            }
        } else {
            mButtonDown.hide();
        }
    }

    void addNewMessage(Message msg) {
        chatUpdater.addNewMessage(msg);
    }

    void moveDown() {
        mMessageList.scrollToPosition(messageList.size() - 1);
    }

    String getChatName() {
        return chatName;
    }

    class WrapContentLinearLayoutManager extends LinearLayoutManager {
        WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.d("DEBUG", "IndexOutOfBoundsException RecyclerView\t" + e.getMessage());
            }
        }
    }
}
