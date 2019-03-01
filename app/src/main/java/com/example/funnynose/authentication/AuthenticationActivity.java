package com.example.funnynose.authentication;



import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.funnynose.MainActivity;
import com.example.funnynose.R;
import com.example.funnynose.SocketAPI;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import io.socket.emitter.Emitter;

public class AuthenticationActivity extends AppCompatActivity {

    private EditText mPhoneView;
    private EditText mPasswordView;

    private boolean successfulAuthentication;
    private boolean responseAuthentication;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setTitle("Авторизация");

        mPhoneView = findViewById(R.id.phone);
        mPhoneView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mPasswordView = findViewById(R.id.password);

        Button mPhoneSignInButton = findViewById(R.id.phone_sign_in_button);
        mPhoneSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        TextView mOpenReg = findViewById(R.id.open_reg_button);
        mOpenReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void signIn() {
        responseAuthentication = false;
        String phone = mPhoneView.getText().toString().replace(" ", "").replace("-", "");
        String password = mPasswordView.getText().toString().trim();
        if (password.length() > 0 && !password.contains(" ")) {
            password = hashFunction(password);
            if (phone.matches("[+]375\\d{9}") && password.length() > 0) {
                // запрос на проверку
                JSONObject obj = new JSONObject();
                try {
                    obj.put("phone", phone);
                    obj.put("password", password);
                } catch (JSONException e) {
                    Log.d("DEBUG", "" + e.getMessage());
                }
                SocketAPI.getSocket().emit("authentication", obj)
                        .once("authentication", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                responseAuthentication = true;
                                successfulAuthentication = (boolean) args[0];
                            }
                        });
                signInThread();
            } else {
                // TODO: заменить прямой вызов Snackbar каким-нибудь методом, чтобы было чище
                if (getCurrentFocus() != null) {
                    Snackbar.make(getCurrentFocus(),
                            "Неправильный номер телефона или пароль",
                            Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                }
            }
        } else {
            if (getCurrentFocus() != null) {
                Snackbar.make(getCurrentFocus(),
                        "Неправильный номер телефона или пароль",
                        Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
            }
        }
    }

    private void signInThread() {
        new Thread(new Runnable() {
            public void run() {
                int counter = 0;
                while (!Thread.currentThread().isInterrupted() && counter < 40) {
                    // проверяем переменную на ответ от сервера
                    if (responseAuthentication) {
                        if (successfulAuthentication) {
                            //showProgress(false);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        } else {
                            //showProgress(false);
                            if (getCurrentFocus() != null) {
                                Snackbar.make(getCurrentFocus(),
                                        "Неправильный номер телефона или пароль",
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
                if (counter == 40) {
                    //showProgress(false);
                    if (getCurrentFocus() != null) {
                        Snackbar.make(getCurrentFocus(),
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

    public static String hashFunction(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.replace(0, 1, "0").append(h);
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
