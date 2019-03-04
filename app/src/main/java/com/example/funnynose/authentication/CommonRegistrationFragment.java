package com.example.funnynose.authentication;

import android.content.Context;
import android.widget.Button;

import com.example.funnynose.Utilities;
import com.example.funnynose.network.AsyncServerResponse;

import androidx.fragment.app.Fragment;

public abstract class CommonRegistrationFragment extends Fragment {

    RegistrationActivity mParent;
    Context mContext;
    Button mContinueButton;

    AsyncServerResponse.AsyncTask failSuc = new AsyncServerResponse.AsyncTask() {
        @Override
        public void call() {
            mParent.showProgress(false);
            changeButtonState();
            Utilities.showSnackbar(mParent.getCurrentFocus(), "Пользователь с такими данными уже существует!", true);
        }
    };

    AsyncServerResponse.AsyncTask failResp = new AsyncServerResponse.AsyncTask() {
        @Override
        public void call() {
            mParent.showProgress(false);
            changeButtonState();
            Utilities.showSnackbar(mParent.getCurrentFocus(), "Ошибка соединения");
        }
    };

    protected void changeButtonState() {
        mParent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mContinueButton.isClickable()) {
                    mContinueButton.setClickable(false);
                } else {
                    mContinueButton.setClickable(true);
                }
            }
        });
    }
}
