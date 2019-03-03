package com.example.funnynose.authentication;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;

public abstract class CommonRegistrationFragment extends Fragment {

    boolean response, existence;

    RegistrationActivity mParent;
    Context mContext;
    Button mContinueButton;

    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            long end = System.currentTimeMillis() + 5000;
            while (!response && System.currentTimeMillis() < end) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Log.d("DEBUG", e.getMessage());
                }
            }
            if (!existence) {
                mParent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stepToNextFragment();
                    }
                });
            } else if (!response) {
                if (mParent.getCurrentFocus() != null) {
                    Snackbar.make(mParent.getCurrentFocus(), "Ошибка соединения",
                            Snackbar.LENGTH_SHORT).show();
                }
            } else {
                if (mParent.getCurrentFocus() != null) {
                    Snackbar.make(mParent.getCurrentFocus(),
                            "Пользователь с такими данными уже существует!",
                            Snackbar.LENGTH_SHORT).setAction("OK", snackOkButton).show();
                }
            }
            mParent.showProgress(false);
            changeButtonState();
        }
    });

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

    void checkInThread(){
        changeButtonState();
        mParent.showProgress(true);
        response = false;
        existence = true;
        checkInAnyThread();
        thread.start();
    }

    protected void checkInAnyThread() {}

    // TODO: ещё есть возможность вернуться на прошлую страницу, это тоже надо учитывать
    // TODO: и при возвращении останавливать поток, то есть требуется сделать аналог этой функции только с previous фрагментом
    // TODO: thread.interrupt()
    protected void stepToNextFragment() {}
}
