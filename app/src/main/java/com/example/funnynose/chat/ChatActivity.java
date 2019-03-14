package com.example.funnynose.chat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.funnynose.NavigationActivity;
import com.example.funnynose.R;
import com.example.funnynose.constants.User;
import com.example.funnynose.network.SocketAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import io.socket.emitter.Emitter;

public class ChatActivity extends NavigationActivity implements TextView.OnEditorActionListener {

    private static final int LIMIT_OF_MESSAGE = 250;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    public EditText mChatInput;
    public FloatingActionButton mSendButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.chat_view_pager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.chat_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        mChatInput = findViewById(R.id.chat_input);
        mChatInput.setImeOptions(EditorInfo.IME_ACTION_SEND);
        mChatInput.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mChatInput.setOnEditorActionListener(this);

        mSendButton = findViewById(R.id.chat_btn_send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        setTabOnLongClickListener();
    }

    public void sendMessage() {
        final String messageText = mChatInput.getText().toString().trim();
        if (isCorrectMessageText(messageText) && SocketAPI.isOnline(this)) {

            JSONObject obj = new JSONObject();
            try {
                obj.put("message_text", messageText);
                obj.put("nickname", User.stringData.get("nickname"));
            } catch (JSONException e) {
                Log.d("DEBUG", e.getMessage());
            }

            String chatName = getCurrentFragment().getChatName();

            SocketAPI.getSocket().emit("new_message_" + chatName, obj)
                    .once("new_message_" + chatName + "_time_key", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    newMessageTimeKeyCall((JSONObject) args[0], messageText);
                }
            });
        }
    }

    private void newMessageTimeKeyCall(JSONObject jsonObject, String messageText) {
        getCurrentFragment().addNewMessage(new Message(messageText, User.stringData.get("nickname"),
                jsonObject.optLong("time"), jsonObject.optLong("key")));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChatInput.setText("");
                moveDown();
            }
        });
    }


    private boolean isCorrectMessageText(String text) {
        if (text.length() <= LIMIT_OF_MESSAGE) {
            return !text.equals("");
        }
        return false;
    }

    private ChatFragment getCurrentFragment() {
        return (ChatFragment) adapter.getItem(viewPager.getCurrentItem());
    }

    public void moveDown() {
        getCurrentFragment().moveDown();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendMessage();
            return true;
        }
        return false;
    }

    private void setupViewPager(@NotNull ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ChatFragment.newInstance(SocketAPI.chatNames[0]), "Общий");

        String chatName = SocketAPI.chatNames[SocketAPI.cities.indexOf(User.stringData.get("city")) + 1];
        adapter.addFragment(ChatFragment.newInstance(chatName), User.stringData.get("city"));

        viewPager.setAdapter(adapter);
    }

    public void openChooseCityDialog() {
        final ArrayList<String> citiesWithoutCurrent = new ArrayList<>(SocketAPI.cities);
        String deleteThisCity = (String) adapter.getPageTitle(0);
        citiesWithoutCurrent.remove(deleteThisCity);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите город")
                .setItems(citiesWithoutCurrent.toArray(new String[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newChatName = SocketAPI.chatNames[SocketAPI.cities.indexOf(citiesWithoutCurrent.get(which)) + 1];
                        adapter.replaceFirstFragment(ChatFragment.newInstance(newChatName), citiesWithoutCurrent.get(which));
                        setTabOnLongClickListener();
                    }
                });

        builder.show();
    }

    public void setTabOnLongClickListener() {
        LinearLayout tabStrip = (LinearLayout) tabLayout.getChildAt(0);
        tabStrip.getChildAt(0).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openChooseCityDialog();
                return true;
            }
        });
    }

}
