package com.example.funnynose.authentication;

import android.content.Context;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;

public abstract class CommonRegistrationFragment extends Fragment {

    boolean response, existence;

    RegistrationActivity mParent;
    Context mContext;
    Button mContinueButton;

    protected void changeButtonState() {
        if (mContinueButton.isClickable()) {
            mContinueButton.setClickable(false);
            mContinueButton.setEnabled(false);
        } else {
            mContinueButton.setClickable(true);
            mContinueButton.setEnabled(true);
        }
    }

    void checkInThread(){
        checkInAnyThread();
        new Thread(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                while (!response) {
                    if (System.currentTimeMillis() - start > 10000) {
                        if (mParent.getCurrentFocus() != null) {
                            mParent.showProgress(false);
                            Snackbar.make(mParent.getCurrentFocus(), "Ошибка соединения",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                        changeButtonState();
                        return;
                    }
                }
                if (!existence) {
                    mParent.showProgress(false);
                    mParent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stepToNextFragment();
                        }
                    });
                } else {
                    response = false;
                    mParent.showProgress(false);
                    if (mParent.getCurrentFocus() != null) {
                        Snackbar.make(mParent.getCurrentFocus(),
                                "Пользователь с такими данными уже существует!",
                                Snackbar.LENGTH_SHORT).setAction("OK", null).show();
                    }
                    changeButtonState();
                }
            }
        }).start();
    }

    protected void checkInAnyThread() {}

    protected void stepToNextFragment() {}
}
