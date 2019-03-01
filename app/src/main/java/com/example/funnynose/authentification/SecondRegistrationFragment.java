package com.example.funnynose.authentification;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.funnynose.R;
import com.example.funnynose.SocketAPI;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.socket.emitter.Emitter;

public class SecondRegistrationFragment extends Fragment {

    private EditText mNicknameView;
    private EditText mNameView;
    private EditText mSurnameView;

    private boolean nicknameExistence;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.second_registration_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
            checkNickname(nickname);
            if (!nicknameExistence) {
                RegistrationActivity parent = (RegistrationActivity) getActivity();
                if (parent != null) {
                    parent.nextFragment();
                    parent.putSecondFragmentData(nickname, name, surname);
                }
            } else {
                Toast.makeText(getContext(), "Пользователь с таким псевдонимом уже существует!", Toast.LENGTH_SHORT).show();
            }
        }
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
                nicknameExistence = (boolean) args[0];
            }
        });
    }

}
