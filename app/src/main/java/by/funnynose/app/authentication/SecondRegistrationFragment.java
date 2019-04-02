package by.funnynose.app.authentication;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.funnynose.R;
import by.funnynose.app.network.AsyncServerResponse;
import by.funnynose.app.network.SocketAPI;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.socket.emitter.Emitter;

public class SecondRegistrationFragment extends CommonRegistrationFragment {
    
    private EditText mNicknameView;
    private EditText mNameView;
    private EditText mSurnameView;

    private AsyncServerResponse mAsyncServerResponse;

    private String mNickname, mName, mSurname;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.second_registration_fragment, container, false);
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
                        mParent.putSecondFragmentData(mNickname, mName, mSurname);
                    }
                });
            }
        });

        mAsyncServerResponse.setFailResponse(failResp);
        mAsyncServerResponse.setFailSuccessful(failSuc);

        mParent = (RegistrationActivity) getActivity();
        mContext = getContext();
        mNicknameView = view.findViewById(R.id.nickname);
        mNameView = view.findViewById(R.id.name);
        mSurnameView = view.findViewById(R.id.surname);
        mContinueButton = view.findViewById(R.id.continue_button);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueRegistration();
            }
        });
    }

    private void continueRegistration() {
        boolean cancel = false;
        View v = new View(mContext);

        mNickname = mNicknameView.getText().toString().trim();
        mName = mNameView.getText().toString().trim();
        mSurname = mSurnameView.getText().toString().trim();

        if (mSurname.length() < 2) {
            cancel = true;
            mSurnameView.setError("Фамилия должна содержать не менее 2 символов");
            v = mSurnameView;
        } else if (mSurname.length() > 25) {
            cancel = true;
            mSurnameView.setError("Фамилия должна содержать не более 25 символов");
            v = mSurnameView;
        } else if (mSurname.contains(" ")) {
            cancel = true;
            mSurnameView.setError("Фамилия не должна содержать пробелы");
            v = mSurnameView;
        }

        if (mName.length() < 2) {
            cancel = true;
            mNameView.setError("Имя должно содержать не менее 2 символов");
            v = mNameView;
        } else if (mName.length() > 25) {
            cancel = true;
            mNameView.setError("Имя должно содержать не более 25 символов");
            v = mNameView;
        }  else if (mName.contains(" ")) {
            cancel = true;
            mNameView.setError("Имя не должно содержать пробелы");
            v = mNameView;
        }

        if (mNickname.length() < 2) {
            cancel = true;
            mNicknameView.setError("Псевдоним должен содержать не менее 2 символов");
            v = mNicknameView;
        } else if (mNickname.length() > 25) {
            cancel = true;
            mNicknameView.setError("Псевдоним должен содержать не более 25 символов");
            v = mNicknameView;
        }

        if (cancel) {
            v.requestFocus();
        } else {
            mParent.showProgress(true);
            changeButtonState();
            checkNickname(mNickname);
            mAsyncServerResponse.start(getContext());
        }
    }

    private void checkNickname(final String nickname) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("mNickname", nickname);
        } catch (JSONException e) {
            Log.d("DEBUG", e.getMessage());
        }

        SocketAPI.getSocket().emit("nickname_existence", obj)
            .once("nickname_existence", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mAsyncServerResponse.setSuccessful(!(boolean) args[0]);
                    mAsyncServerResponse.setResponse(true);
                }
            });
    }

}
