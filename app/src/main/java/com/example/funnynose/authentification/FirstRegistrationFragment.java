package com.example.funnynose.authentification;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funnynose.R;
import com.example.funnynose.Session;
import com.example.funnynose.SocketAPI;
import com.example.funnynose.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import io.socket.emitter.Emitter;

public class FirstRegistrationFragment extends Fragment {
    private EditText mEmailView;
    private EditText mPhoneView;
    private EditText mPasswordView;
    private EditText mRepeatPasswordView;

    private boolean phoneExistence;
    private boolean emailExistence;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.first_registration_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEmailView = view.findViewById(R.id.email);
        mPhoneView = view.findViewById(R.id.phone);
        mPhoneView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        mPasswordView = view.findViewById(R.id.password);
        mRepeatPasswordView = view.findViewById(R.id.repeat_password);
        Button mContinueButton = view.findViewById(R.id.continue_button);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueRegistration();
            }
        });

        TextView mOpenReg = view.findViewById(R.id.open_login_button);
        mOpenReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegistrationActivity) getActivity()).exitFromRegistration();
            }
        });

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            String phoneNumber = tMgr.getLine1Number();
            if (phoneNumber != null) {
                mPhoneView.setText(phoneNumber);
            }
        }
    }


    private void continueRegistration() {
        boolean cancel = false;
        View v = new View(getContext());

        phoneExistence = true;
        emailExistence = true;

        String password, rPassword, phone, email;

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

        if (!email.contains("@")) {
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
            v.requestFocus();
        } else {

            checkPhoneOnUiThread(phone);
            checkEmailOnUiThread(email);
            if (!phoneExistence && !emailExistence) {
                ((RegistrationActivity) getActivity()).nextFragment();
                ((RegistrationActivity) getActivity()).putFirstFragmentData(email, phone, AuthenticationActivity.hashFunction(password));
            } else {
                Toast.makeText(Session.context, "Пользователь с такими данными уже существует!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkPhoneOnUiThread(final String phone) {
        new Thread() {
            public void run() {
                checkPhone(phone);
            }
        }.start();
    }

    private void checkEmailOnUiThread(final String email) {
        new Thread() {
            public void run() {
                checkEmail(email);
            }
        }.start();
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
                Log.d("MAIN", "phone  " + phoneExistence);
            }
        });
    }

    private void checkEmail(final String email) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("email", email);
        } catch (JSONException e) {
            Log.d("DEBUG", "" + e.getMessage());
        }
        SocketAPI.getSocket().emit("registration/email_existence", obj)
                .once("registration/email_existence", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                emailExistence = (boolean) args[0];
                Log.d("MAIN", "email  " + emailExistence);
            }
        });
    }
}

