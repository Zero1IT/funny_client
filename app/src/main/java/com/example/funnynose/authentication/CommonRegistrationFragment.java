package com.example.funnynose.authentication;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.funnynose.network.AsyncServerResponse;
import com.google.android.material.snackbar.Snackbar;

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
            if (mParent.getCurrentFocus() != null) {
                Snackbar.make(mParent.getCurrentFocus(),
                        "Пользователь с такими данными уже существует!",
                        Snackbar.LENGTH_SHORT).setAction("OK", snackOkButton).show();
            }
        }
    };

    AsyncServerResponse.AsyncTask failResp = new AsyncServerResponse.AsyncTask() {
        @Override
        public void call() {
            mParent.showProgress(false);
            changeButtonState();
            if (mParent.getCurrentFocus() != null) {
                Snackbar.make(mParent.getCurrentFocus(), "Ошибка соединения",
                        Snackbar.LENGTH_SHORT).show();
            }
        }
    };

    static View.OnClickListener snackOkButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {}
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
