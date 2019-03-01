package com.example.funnynose.authentication;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.funnynose.R;
import com.example.funnynose.SocketAPI;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.socket.emitter.Emitter;

public class SecondRegistrationFragment extends Fragment {

    private RegistrationActivity mParent;
    private EditText mNicknameView;
    private EditText mNameView;
    private EditText mSurnameView;

    private boolean nicknameExistence;
    private boolean responseNickname;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.second_registration_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mParent = (RegistrationActivity) getActivity();
        mNicknameView = view.findViewById(R.id.nickname);
        mNameView = view.findViewById(R.id.name);
        mSurnameView = view.findViewById(R.id.surname);

        Button mContinueButton = view.findViewById(R.id.continue_button);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueRegistration();
            }
        });
    }

    private void continueRegistration() {
        boolean cancel = false;
        View v = new View(getContext());

        String nickname, name, surname;

        nickname = mNicknameView.getText().toString().trim();
        name = mNameView.getText().toString().trim();
        surname = mSurnameView.getText().toString().trim();

        if (surname.length() < 2) {
            cancel = true;
            mSurnameView.setError("Фамилия должна содержать не менее 2 символов");
            v = mSurnameView;
        } else if (surname.length() > 25) {
            cancel = true;
            mSurnameView.setError("Фамилия должна содержать не более 25 символов");
            v = mSurnameView;
        }

        if (name.length() < 2) {
            cancel = true;
            mNameView.setError("Имя должно содержать не менее 2 символов");
            v = mNameView;
        } else if (name.length() > 25) {
            cancel = true;
            mNameView.setError("Имя должно содержать не более 25 символов");
            v = mNameView;
        }

        if (nickname.length() < 2) {
            cancel = true;
            mNicknameView.setError("Псевдоним должен содержать не менее 2 символов");
            v = mNicknameView;
        } else if (nickname.length() > 25) {
            cancel = true;
            mNicknameView.setError("Псевдоним должен содержать не более 25 символов");
            v = mNicknameView;
        }

        if (cancel) {
            v.requestFocus();
        } else {
            mParent.showProgress(true);
            checkInThread(nickname, name, surname);
        }
    }

    private void checkInThread(final String nickname, final String name, final String surname){
        new Thread(new Runnable() {
            public void run() {
                responseNickname = false;
                checkNickname(nickname);
                int counter = 0;
                while (!Thread.currentThread().isInterrupted() && counter < 40) {
                    // проверяем переменную на ответ от сервера
                    if (responseNickname) {
                        if (!nicknameExistence) {
                            mParent.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mParent.nextFragment();
                                    mParent.putSecondFragmentData(nickname, name, surname);
                                    mParent.showProgress(false);
                                }
                            });
                            return;
                        } else {
                            mParent.showProgress(false);
                            if (mParent.getCurrentFocus() != null) {
                                Snackbar.make(mParent.getCurrentFocus(),
                                        "Пользователь с такими данными уже существует!",
                                        Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }).show();
                            }
                            return;
                        }
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    counter++;
                }
                if (counter == 40) { // 50 * 40 = 2000 ms = 2 секунды
                    mParent.showProgress(false);
                    if (mParent.getCurrentFocus() != null) {
                        Snackbar.make(mParent.getCurrentFocus(),
                                "Ошибка соединения!",
                                Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
                    }
                }
            }
        }).start();
    }

    private void checkNickname(final String nickname) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("nickname", nickname);
        } catch (JSONException e) {
            Log.d("DEBUG", "" + e.getMessage());
        }
        SocketAPI.getSocket().emit("registration/nickname_existence", obj)
                .once("registration/nickname_existence", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                responseNickname = true;
                nicknameExistence = (boolean) args[0];
            }
        });
    }

}
