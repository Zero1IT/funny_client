package by.funnynose.app.authentication;

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
import by.funnynose.app.network.AsyncServerResponse;
import by.funnynose.app.network.SocketAPI;

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

    private AsyncServerResponse mAsyncServerResponse;

    private String mPassword, mPhone, mEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.first_registration_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAsyncServerResponse = new AsyncServerResponse(new AsyncServerResponse.AsyncTask() {
            @Override
            public void call() {
                mParent.showProgress(false);
                mParent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mParent.nextFragment();
                        mParent.putFirstFragmentData(mEmail, mPhone, AuthenticationActivity.hashFunction(mPassword));
                    }
                });
            }
        });

        mAsyncServerResponse.setFailSuccessful(failSuc);
        mAsyncServerResponse.setFailResponse(failResp);

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

        mPassword = mPasswordView.getText().toString();
        rPassword = mRepeatPasswordView.getText().toString();
        mPhone = mPhoneView.getText().toString().replace(" ", "").replace("-", "");
        mEmail = mEmailView.getText().toString().trim();


        if (!rPassword.equals(mPassword)) {
            cancel = true;
            mRepeatPasswordView.setError("Пароли не совпадают");
            v = mRepeatPasswordView;
        }

        if (mPassword.length() < 6) {
            cancel = true;
            mPasswordView.setError("Пароль должен содержать не менее 6 символов");
            v = mPasswordView;

        } else if (mPassword.length() > 25) {
            cancel = true;
            mPasswordView.setError("Пароль должен содержать не более 25 символов");
            v = mPasswordView;
        } else if (mPassword.contains(" ")) {
            cancel = true;
            mPasswordView.setError("Пароль не должен содержать пробелы");
            v = mPasswordView;
        }

        if (!mPhone.matches("[+]375\\d{9}")) {
            cancel = true;
            mPhoneView.setError("Номер телефона неправильный. Правильно: +375 XX XXX-XX-XX");
            v = mPhoneView;
        }

        if (!mEmail.contains("@") || !mEmail.contains(".")) {
            cancel = true;
            mEmailView.setError("Неправильный Email");
            v = mEmailView;
        }

        if (mEmail.length() < 6) {
            cancel = true;
            mEmailView.setError("Email должен содержать не менее 6 символов");
            v = mEmailView;
        } else if (mEmail.length() > 25) {
            cancel = true;
            mEmailView.setError("Email должен содержать не более 25 символов");
            v = mEmailView;
        }

        if (cancel) {
            v.requestFocus();
        } else {
            mParent.showProgress(true);
            changeButtonState();
            checkEmailPhone(mEmail, mPhone);
            mAsyncServerResponse.start(getContext());
        }
    }

    private void checkEmailPhone(final String email, final String phone) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("mEmail", email);
            obj.put("mPhone", phone);
        } catch (JSONException e) {
            Log.d("DEBUG", e.getMessage());
        }
        SocketAPI.getSocket().emit("email_phone_existence", obj)
            .once("email_phone_existence", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mAsyncServerResponse.setSuccessful(!(boolean) args[0]);
                    mAsyncServerResponse.setResponse(true);
                }
            });
    }
}