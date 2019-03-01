package com.example.funnynose.authentification;



import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        mPhoneSignInButton.setOnClickListener(enterEvent);

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

    View.OnClickListener enterEvent = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
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
                                    if ((boolean) args[0]) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // TODO: Toast вылетает здесь из-за ассинхронности вызова.
                                        // TODO: а может ошибка была другой?
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //TODO: maybe отойдём от Toast)))?
                                                //Toast.makeText(getApplicationContext(),
                                                //        "Неправильный номер телефона или пароль",
                                                //        Toast.LENGTH_SHORT).show();
                                                Snackbar.make(view,
                                                        "Неправильный номер телефона или пароль",
                                                        Snackbar.LENGTH_LONG).setAction("Ошибка", null);
                                            }
                                        });
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Неправильный номер телефона или пароль", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Неправильный номер телефона или пароль", Toast.LENGTH_SHORT).show();
            }

        }
    };

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
