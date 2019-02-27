package com.example.funnynose.authentification;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.funnynose.MainActivity;
import com.example.funnynose.R;
import com.example.funnynose.Session;
import com.example.funnynose.SocketAPI;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import io.socket.emitter.Emitter;

public class FirstRegistrationActivity extends AppCompatActivity {
    private AutoCompleteTextView mPhoneView;
    private EditText mPasswordView;
    private EditText mRepeatPasswordView;

    private boolean phoneExistence;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_first_registration);
        getSupportActionBar().setTitle("Регистрация");

        mPhoneView = findViewById(R.id.phone);
        mPasswordView = findViewById(R.id.password);
        mRepeatPasswordView = findViewById(R.id.repeat_password);
        Button mContinueButton = findViewById(R.id.continue_button);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueRegistration();
            }
        });
        mPhoneView.addTextChangedListener(new PhoneNumberFormattingTextWatcher("BY"));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String phoneNumber = tMgr.getLine1Number();
            if (phoneNumber != null) {
                mPhoneView.setText(phoneNumber);
            }
        }
    }

    private void continueRegistration() {
        boolean cancel = false;
        View v = new View(this);

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

        if (cancel) {
            v.requestFocus();
        } else {
            checkPhone(phone);
            if (!phoneExistence) {
                //Intent intent = new Intent(Session.context, SecondRegistrationActivity.class);
                //startActivity(intent);
            } else {
                Toast.makeText(Session.context, "Пользователь с таким номером телефона уже существует", Toast.LENGTH_SHORT).show();
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
        SocketAPI.currentSocket().emit("registration/phone_existence", obj)
                .once("registration/phone_existence", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                phoneExistence = (boolean) args[0];
            }
        });
    }
}

