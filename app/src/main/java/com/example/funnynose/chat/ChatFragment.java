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

import static com.example.funnynose.Utilities.DATE_FORMAT;

public class ChatFragment extends Fragment {

    private static final String KEY_CHAT_NAME = "chat_name";

    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private TextView mDateView;
    private FloatingActionButton mDownButton;

    private ArrayList<Message> mMessageList = new ArrayList<>();
    private MessageListAdapter mMessageListAdapter;

    private String mChatName;
    private ChatUpdater mChatUpdater;

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
            mChatName = bundle.getString(KEY_CHAT_NAME);
        }
        return inflater.inflate(R.layout.chat_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.chat_message_list);

        mLayoutManager = new WrapContentLinearLayoutManager(getContext());
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMessageListAdapter = new MessageListAdapter(mMessageList, this);
        mRecyclerView.setAdapter(mMessageListAdapter);

        mDownButton = view.findViewById(R.id.chat_btn_down);
        mDownButton.setVisibility(View.GONE);
        mDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    moveDown();
            }
        });

        mDateView = view.findViewById(R.id.chat_day_month);

        mChatUpdater = new ChatUpdater(getActivity(), mMessageListAdapter, mRecyclerView, mMessageList, mChatName);
    }

    void updateTextDate(int position) {
        if (position > 2) {
            mDateView.setVisibility(View.VISIBLE);
            try {
                mDateView.setText(DATE_FORMAT.format(mMessageList.get(mLayoutManager.
                        findFirstVisibleItemPosition()).time));
            } catch (IndexOutOfBoundsException e) {
                Log.d("DEBUG", e.getMessage());
            }
        } else {
            mDateView.setVisibility(View.INVISIBLE);
        }
    }

    void refreshChat() {
        if (!mChatUpdater.getIsLoading()) {
            if (mLayoutManager.findFirstVisibleItemPosition() < 35) {
                mChatUpdater.refreshChat();
            }
        }
    }

    void showButtonDown(int position) {
        if (position != -1) {
            if (position < mMessageListAdapter.getItemCount() - 1) {
                mDownButton.show();
            } else {
                mDownButton.hide();
            }
        } else {
            mDownButton.hide();
        }
    }

    void addNewMessage(Message msg) {
        mChatUpdater.addNewMessage(msg);
    }

    void moveDown() {
        mRecyclerView.scrollToPosition(mMessageList.size() - 1);
    }

    String getChatName() {
        return mChatName;
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
