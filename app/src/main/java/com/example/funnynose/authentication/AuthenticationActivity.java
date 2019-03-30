package com.example.funnynose.authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.funnynose.MainActivity;
import com.example.funnynose.R;
import com.example.funnynose.Utilities;
import com.example.funnynose.network.AsyncServerResponse;
import com.example.funnynose.network.SocketAPI;
import com.example.funnynose.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.socket.emitter.Emitter;

public class AuthenticationActivity extends AppCompatActivity {

    private EditText mPhoneView;
    private EditText mPasswordView;
    private ProgressBar mProgressView;

    private JSONObject mJsonResponse;

    private AsyncServerResponse mAsyncServerResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setTitle("Регистрация");
        }

        mProgressView = findViewById(R.id.progress);

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

        initAsyncServerResponse();

    }

    private void initAsyncServerResponse() {
        mAsyncServerResponse = new AsyncServerResponse(5000, new AsyncServerResponse.AsyncTask() {
            @Override
            public void call() {
                showProgress(false);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                User.userDataFromJson(mJsonResponse);
                User.setUserAppData(getApplicationContext());
                finish();
            }
        });

        mAsyncServerResponse.setFailResponse(new AsyncServerResponse.AsyncTask() {
            @Override
            public void call() {
                showProgress(false);
                Utilities.showSnackbar(getCurrentFocus(), "Ошибка соединения");
            }
        });

        mAsyncServerResponse.setFailSuccessful(new AsyncServerResponse.AsyncTask() {
            @Override
            public void call() {
                showProgress(false);
                Utilities.showSnackbar(getCurrentFocus(), "Неправильный номер телефона или пароль", true);
            }
        });
    }

    public void signIn() {
        String phone = mPhoneView.getText().toString().replace(" ", "").replace("-", "");
        String password = mPasswordView.getText().toString().trim();
        if (password.length() > 0 && !password.contains(" ")) {
            password = hashFunction(password);
            if (phone.matches("[+]375\\d{9}") && password.length() > 0) {
                showProgress(true);
                JSONObject obj = new JSONObject();
                try {
                    obj.put("phone", phone);
                    obj.put("password", password);
                } catch (JSONException e) {
                    Log.d("DEBUG", e.getMessage());
                }
                SocketAPI.getSocket().emit("authentication", obj)
                    .once("authentication", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            mJsonResponse = (JSONObject) args[0];
                            mAsyncServerResponse.setSuccessful((boolean) mJsonResponse.remove("auth"));
                            mAsyncServerResponse.setResponse(true);
                        }
                    });
                mAsyncServerResponse.start(getApplicationContext());
            } else {
                Utilities.showSnackbar(getCurrentFocus(), "Неправильный номер телефона или пароль", true);
            }
        } else {
            Utilities.showSnackbar(getCurrentFocus(), "Неправильный номер телефона или пароль", true);
        }
    }

    public void showProgress(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            mProgressView.animate().setDuration(shortAnimTime).
                setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                    }
                });
            }
        });
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
