package com.example.funnynose.authentication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.funnynose.R;
import com.example.funnynose.SocketAPI;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import io.socket.emitter.Emitter;

public class FirstRegistrationFragment extends CommonRegistrationFragment {
    private EditText mEmailView;
    private EditText mPhoneView;
    private EditText mPasswordView;
    private EditText mRepeatPasswordView;

    private String password, phone, email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.first_registration_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mParent = (RegistrationActivity) getActivity();
        mContext = getContext();

        mEmailView = view.findViewById(R.id.email);
        mPhoneView = view.findViewById(R.id.phone);
        mPhoneView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        mPasswordView = view.findViewById(R.id.password);
        mRepeatPasswordView = view.findViewById(R.id.repeat_password);
        mContinueButton = view.findViewById(R.id.continue_button);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeButtonState();
                continueRegistration();
            }
        });

        TextView mOpenReg = view.findViewById(R.id.open_login_button);
        mOpenReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParent.exitFromRegistration();
            }
        });

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager) mParent.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("HardwareIds") String phoneNumber = tMgr.getLine1Number();
            if (phoneNumber != null) {
                mPhoneView.setText(phoneNumber);
            }
        }
    }

    private void continueRegistration() {
        boolean cancel = false;
        View v = new View(mContext);

        String rPassword;

        password = mPasswordView.getText().toString();
        rPassword = mRepeatPasswordView.getText().toString();
        phone = mPhoneView.getText().toString().replace(" ", "").replace("-", "");
        email = mEmailView.getText().toString().trim();


        if (!rPassword.equals(password)) {
            cancel = true;
            mRepeatPasswordView.setError("Пароли не совпадают");
            v = mRepeatPasswordView;
        }

        if (password.length() < 6) {
            cancel = true;
            mPasswordView.setError("Пароль должен содержать не менее 6 символов");
            v = mPasswordView;

        } else if (password.length() > 25) {
            cancel = true;
            mPasswordView.setError("Пароль должен содержать не более 25 символов");
            v = mPasswordView;
        } else if (password.contains(" ")) {
            cancel = true;
            mPasswordView.setError("Пароль не должен содержать пробелы");
            v = mPasswordView;
        }

        if (!phone.matches("[+]375\\d{9}")) {
            cancel = true;
            mPhoneView.setError("Номер телефона неправильный. Правильно: +375 XX XXX-XX-XX");
            v = mPhoneView;
        }

        if (!email.contains("@") || !email.contains(".")) {
            cancel = true;
            mEmailView.setError("Неправильный Email");
            v = mEmailView;
        }

        if (email.length() < 5) {
            cancel = true;
            mEmailView.setError("Email должен содержать не менее 5 символов");
            v = mEmailView;
        } else if (email.length() > 25) {
            cancel = true;
            mEmailView.setError("Email должен содержать не более 25 символов");
            v = mEmailView;
        }

        if (cancel) {
            changeButtonState();
            v.requestFocus();
        } else {
            mParent.showProgress(true);
            checkInThread();
        }
    }

    @Override
    protected void checkInAnyThread() {
        checkEmailPhone(email, phone);
    }

    @Override
    protected void stepToNextFragment() {
        mParent.nextFragment();
        mParent.putFirstFragmentData(email, phone, AuthenticationActivity.hashFunction(password));
    }

    private void checkEmailPhone(final String email, final String phone) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("email", email);
            obj.put("phone", phone);
        } catch (JSONException e) {
            Log.d("DEBUG", "" + e.getMessage());
        }
        SocketAPI.getSocket().emit("email/phone_existence", obj)
            .once("email/phone_existence", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    response = true;
                    existence = (boolean) args[0];
                }
            });
    }

}

