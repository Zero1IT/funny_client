package com.example.funnynose.authentication;

import android.Manifest;
import android.content.Context;
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

import com.example.funnynose.R;
import com.example.funnynose.SocketAPI;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import io.socket.emitter.Emitter;

public class FirstRegistrationFragment extends Fragment {
    private EditText mEmailView;
    private EditText mPhoneView;
    private EditText mPasswordView;
    private EditText mRepeatPasswordView;
    private RegistrationActivity mParent;

    private boolean phoneExistence;
    private boolean emailExistence;
    private boolean responsePhone;
    private boolean responseEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.first_registration_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mParent = (RegistrationActivity) getActivity();
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
                mParent.exitFromRegistration();
            }
        });

        Context context = getContext();

        if (context == null) {
            throw new NullPointerException("Context is null");
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager) mParent.getSystemService(Context.TELEPHONY_SERVICE);
            String phoneNumber = tMgr.getLine1Number(); //TODO: узнать почему - исправить // написано не рекомендуется - исправлять не требуется
            if (phoneNumber != null) {
                mPhoneView.setText(phoneNumber);
            }
        }
    }


    private void continueRegistration() {
        boolean cancel = false;
        View v = new View(getContext());

        emailExistence = true;
        phoneExistence = true;

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
            v.requestFocus();
        } else {
            mParent.showProgress(true);
            checkInThread(email, phone, password);
        }
    }

    private void checkInThread(final String email, final String phone, final String password){
        new Thread(new Runnable() {
            public void run() {
                responseEmail = false;
                responsePhone = false;
                checkEmail(email);
                checkPhone(phone);
                int counter = 0;
                while (!Thread.currentThread().isInterrupted() && counter < 40) {
                    // проверяем переменную на ответ от сервера
                    if (responseEmail && responsePhone) {
                        if (!phoneExistence && !emailExistence) {
                            mParent.showProgress(false);
                            mParent.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mParent.nextFragment();
                                    mParent.putFirstFragmentData(email, phone, AuthenticationActivity.hashFunction(password));
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
                                    public void onClick(View v) {}
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
                        responseEmail = true;
                        emailExistence = (boolean) args[0];
                    }
                });
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
                responsePhone = true;
                phoneExistence = (boolean) args[0];
            }
        });
    }

}

