package com.example.funnynose.chat;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.funnynose.R;
import com.example.funnynose.User;
import com.example.funnynose.network.SocketAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import io.socket.emitter.Emitter;

public class DoubleChatFragment extends Fragment implements TextView.OnEditorActionListener {

    private static final int LIMIT_OF_MESSAGE = 250;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    private FragmentActivity mFragmentActivity;

    private EditText mInputView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.double_chat_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFragmentActivity = getActivity();

        mViewPager = view.findViewById(R.id.chat_view_pager);
        setupViewPager(mViewPager);

        mTabLayout = view.findViewById(R.id.chat_tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setBackgroundColor(getResources().getColor(R.color.transparent));

        mInputView = view.findViewById(R.id.chat_input);
        mInputView.setImeOptions(EditorInfo.IME_ACTION_SEND);
        mInputView.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mInputView.setOnEditorActionListener(this);

        FloatingActionButton mSendButton = view.findViewById(R.id.chat_btn_send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        setTabOnLongClickListener();
    }

    private void sendMessage() {
        final String messageText = mInputView.getText().toString().trim();
        if (isCorrectMessageText(messageText)) {
            if (SocketAPI.isOnline()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("message_text", messageText);
                    obj.put("nickname", User.mStringData.get("nickname"));
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
            } else {
                showSnackbarNetworkError();
            }
        }
    }

    @TargetApi(21)
    private void showSnackbarNetworkError() {
        if (getView() != null) {
            Snackbar snackbar = Snackbar.make(mFragmentActivity.findViewById(R.id.layout_for_snack_bar),
                    "Нет соединения с интернетом", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) { }
            });
            snackbar.getView().setElevation(0);
            snackbar.show();
        }
    }

    private void newMessageTimeKeyCall(JSONObject jsonObject, String messageText) {
        getCurrentFragment().addNewMessage(new Message(messageText, User.mStringData.get("nickname"),
                jsonObject.optLong("time"), jsonObject.optLong("key")));

        mFragmentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mInputView.setText("");
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
        return (ChatFragment) mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
    }

    private void moveDown() {
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

        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        String chatName = SocketAPI.sChatNames[SocketAPI.sCities.indexOf(User.mStringData.get("city")) + 1];
        mViewPagerAdapter.addFragment(ChatFragment.newInstance(chatName), User.mStringData.get("city"));

        mViewPagerAdapter.addFragmentByIndex(ChatFragment.newInstance(SocketAPI.sChatNames[0]), "Общий", 1);
        viewPager.setAdapter(mViewPagerAdapter);
    }

    public void openChooseCityDialog() {
        final ArrayList<String> citiesWithoutCurrent = new ArrayList<>(SocketAPI.sCities);
        String deleteThisCity = (String) mViewPagerAdapter.getPageTitle(0);
        citiesWithoutCurrent.remove(deleteThisCity);

        AlertDialog.Builder builder = new AlertDialog.Builder(mFragmentActivity);
        builder.setTitle("Выберите город")
                .setItems(citiesWithoutCurrent.toArray(new String[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newChatName = SocketAPI.sChatNames[SocketAPI.sCities.indexOf(citiesWithoutCurrent.get(which)) + 1];
                        mViewPagerAdapter.replaceFirstFragment(ChatFragment.newInstance(newChatName), citiesWithoutCurrent.get(which));
                        setTabOnLongClickListener();
                    }
                });

        builder.show();
    }

    private void setTabOnLongClickListener() {
        LinearLayout tabStrip = (LinearLayout) mTabLayout.getChildAt(0);
        tabStrip.getChildAt(0).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openChooseCityDialog();
                return true;
            }
        });
    }
}
