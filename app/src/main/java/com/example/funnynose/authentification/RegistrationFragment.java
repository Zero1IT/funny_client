package com.example.funnynose.authentification;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.funnynose.R;
import com.example.funnynose.Session;
import com.example.funnynose.SocketAPI;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import io.socket.emitter.Emitter;

public class RegistrationFragment extends Fragment {
    private AutoCompleteTextView mPhoneView;
    private EditText mPasswordView;
    private EditText mRepeatPasswordView;

    private boolean phoneExistence;

    public static Fragment newInstance() {
        return new RegistrationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPhoneView = view.findViewById(R.id.phone);
        mPasswordView = view.findViewById(R.id.password);
        mRepeatPasswordView = view.findViewById(R.id.repeat_password);
        Button mContinueButton = view.findViewById(R.id.continue_button);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueRegistration();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPhoneView.addTextChangedListener(new PhoneNumberFormattingTextWatcher("BY"));
        }
    }

    private void continueRegistration() {
        boolean cancel = false;
        View v = new View(getContext());

        String password, rPassword, phone, nickname;

        password = mPasswordView.getText().toString();
        rPassword = mRepeatPasswordView.getText().toString();
        phone = mPhoneView.getText().toString().replace(" ", "").replace("-", "");;


        if (!rPassword.equals(password)) {
            cancel = true;
            mRepeatPasswordView.setError("Пароли не совпадают");
            v = mRepeatPasswordView;
        }

        if (password.length() < 6) {
            cancel = true;
            mPasswordView.setError("Пароль должен содержать не менее 6 символов");
            v = mPasswordView;

        } else if (password.length() > 50) {
            cancel = true;
            mPasswordView.setError("Пароль должен содержать не более 50 символов");
            v = mPasswordView;
        } else if (password.contains(" ")) {
            cancel = true;
            mPasswordView.setError("Пароль не должен содержать пробелы");
            v = mPasswordView;
        }

        if (!phone.matches("[+]375\\d{9}")) {
            cancel = true;
            mPhoneView.setError("Неверный формат номера. +375 XX XXX-XX-XX");
            v = mPhoneView;
        }

        if (cancel) {
            v.requestFocus();
        } else {
            checkPhone(phone);
            // реализовать ответ о совпадении номеров, если таков есть
            RegistrationActivity parent = (RegistrationActivity)getActivity();
            if (parent != null) {
                parent.goNext();
            }
        }
    }

    private void checkPhone(final String phone) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("phone", phone);
        } catch (JSONException e) {
            Log.d("DEBUG", "" + e.getMessage());
        }
        SocketAPI.getSocket().emit("registration/phone_existence", obj)
                .once("registration/phone_existence", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                phoneExistence = (boolean) args[0];
            }
        });
    }
}

