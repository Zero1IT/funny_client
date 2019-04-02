package by.funnynose.app.authentication;

import android.content.Context;
import android.widget.Button;

import by.funnynose.app.Utilities;
import by.funnynose.app.network.AsyncServerResponse;

import androidx.fragment.app.Fragment;

abstract class CommonRegistrationFragment extends Fragment {

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

    void changeButtonState() {
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
