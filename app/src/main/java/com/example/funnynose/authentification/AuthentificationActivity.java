package com.example.funnynose.authentification;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import io.socket.emitter.Emitter;

public class AuthentificationActivity extends AppCompatActivity {

    private AutoCompleteTextView mPhoneView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);
        getSupportActionBar().setTitle("Авторизация");

        mPhoneView = findViewById(R.id.phone);
        mPhoneView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mPasswordView = findViewById(R.id.password);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String phoneNumber = tMgr.getLine1Number();
            if (phoneNumber != null) {
                mPhoneView.setText(phoneNumber);
            }
        }

        Button mPhoneSignInButton = findViewById(R.id.phone_sign_in_button);
        mPhoneSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = mPhoneView.getText().toString().replace(" ", "").replace("-", "");
                String password = mPasswordView.getText().toString();
                if (password.length() > 0 && !password.contains(" ")) {
                    password = md5(password);
                    if (phone.matches("[+]375\\d{9}") && password.length() > 0) {
                        // запрос на проверку
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("phone", phone);
                            obj.put("password", password);
                        } catch (JSONException e) {
                            Log.d("DEBUG", "" + e.getMessage());
                        }
                        SocketAPI.currentSocket().emit("authentication", obj)
                                .once("authentication", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                if ((boolean) args[0]) {
                                    Intent intent = new Intent(Session.context, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                }
                Toast.makeText(Session.context, "Неправильный номер телефона или пароль", Toast.LENGTH_SHORT).show();
            }
        });

        Button mOpenReg = findViewById(R.id.open_reg_button);
        mOpenReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Session.context, FirstRegistrationActivity.class);
                startActivity(intent);
            }
        });
    }



    public static String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
