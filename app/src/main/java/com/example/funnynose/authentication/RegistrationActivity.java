package com.example.funnynose.authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.funnynose.MainActivity;
import com.example.funnynose.Utilities;
import com.example.funnynose.constants.Permission;
import com.example.funnynose.R;
import com.example.funnynose.network.AsyncServerResponse;
import com.example.funnynose.network.SocketAPI;
import com.example.funnynose.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import io.socket.emitter.Emitter;

public class RegistrationActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;
    private Fragment[] mFragments;
    private int mFragmentIndex;

    private ActionBar mActionBar;
    private ProgressBar mProgressView;

    private JSONObject mRegistrationUserData;

    private AsyncServerResponse mAsyncServerResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Регистрация");

        mActionBar = getSupportActionBar();
        mProgressView = findViewById(R.id.progress);

        mRegistrationUserData = new JSONObject();

        initFragments();
        initAsyncServerResponse();
    }

    private void initFragments() {
        mFragmentManager = getSupportFragmentManager();
        mFragments = new Fragment[] {
                new FirstRegistrationFragment(),
                new SecondRegistrationFragment(),
                new ThirdRegistrationFragment() };
        Fragment fragment = mFragmentManager.findFragmentById(R.id.registration_frame);
        if (fragment == null) {
            mFragmentManager.beginTransaction().add(R.id.registration_frame, mFragments[0]).commit();
            mFragmentIndex = 0;
        }
    }

    private void initAsyncServerResponse() {
        mAsyncServerResponse = new AsyncServerResponse(new AsyncServerResponse.AsyncTask() {
            @Override
            public void call() {
                showProgress(false);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                User.userDataFromJson(mRegistrationUserData);
                User.setUserAppData(getApplicationContext());
                finish();
            }
        });

        mAsyncServerResponse.setFailResponse(new AsyncServerResponse.AsyncTask() {
            @Override
            public void call() {
                showProgress(false);
                Utilities.showSnackbar(getCurrentFocus(), "Ошибка соединения");
            }
        });

        mAsyncServerResponse.setFailSuccessful(new AsyncServerResponse.AsyncTask() {
            @Override
            public void call() {
                showProgress(false);
                Utilities.showSnackbar(getCurrentFocus(), "Ошибка регистрации", true);
            }
        });
    }

    public void nextFragment(){
        if (mFragmentIndex < 2) {
            mFragmentIndex++;
            mFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.registration_frame, mFragments[mFragmentIndex]).commit();
            if (mFragmentIndex > 0) {
                if (mActionBar != null) {
                    mActionBar.setHomeButtonEnabled(true);
                    mActionBar.setDisplayHomeAsUpEnabled(true);
                }
            }
        } else {
            showProgress(true);

            try {
                mRegistrationUserData.put("status", "");
                mRegistrationUserData.put("permission", Permission.LOSER);
                mRegistrationUserData.put("hospitalsPerYear", 0);
                mRegistrationUserData.put("trainingsPerYear", 0);
                mRegistrationUserData.put("othersPerYear", 0);
            } catch (JSONException e) {
                Log.d("DEBUG", e.getMessage());
            }

            SocketAPI.getSocket().emit("registration", mRegistrationUserData)
                    .once("registration", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            JSONObject jsonObject = (JSONObject) args[0];
                            if (jsonObject.optBoolean("registration")) {
                                mAsyncServerResponse.setSuccessful(true);
                                try {
                                    mRegistrationUserData.put("id_", jsonObject.optLong("id_"));
                                    mRegistrationUserData.put("lastChangeDate",
                                            jsonObject.optLong("lastChangeDate"));
                                } catch (JSONException e) {
                                    Log.d("DEBUG", e.getMessage());
                                }
                            } else {
                                mAsyncServerResponse.setSuccessful(false);
                            }
                            mAsyncServerResponse.setResponse(true);
                        }
                    });
            mAsyncServerResponse.start(getApplicationContext());
        }
    }

    public void previousFragment(){
        if (mFragmentIndex > 0) {
            mFragmentIndex--;
            mFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.registration_frame, mFragments[mFragmentIndex]).commit();
            if (mFragmentIndex == 0) {
                if (mActionBar != null) {
                    mActionBar.setHomeButtonEnabled(false);
                    mActionBar.setDisplayHomeAsUpEnabled(false);
                }
            }
        } else {
            exitFromRegistration();
        }
    }

    public void putFirstFragmentData(String email, String phone, String password) {
        try {
            mRegistrationUserData.put("email", email);
            mRegistrationUserData.put("phone", phone);
            mRegistrationUserData.put("password", password);
        } catch (JSONException e) {
            Log.d("DEBUG", e.getMessage());
        }
    }

    public void putSecondFragmentData(String nickname, String name, String surname) {
        try {
            mRegistrationUserData.put("nickname", nickname);
            mRegistrationUserData.put("name", name);
            mRegistrationUserData.put("surname", surname);
        } catch (JSONException e) {
            Log.d("DEBUG", e.getMessage());
        }
    }

    public void putThirdFragmentData(String city, Date birthdayDate, Date firstParticipationDate) {
        try {
            mRegistrationUserData.put("city", city);
            mRegistrationUserData.put("birthday", birthdayDate.getTime());
            mRegistrationUserData.put("firstParticipation", firstParticipationDate.getTime());
            mRegistrationUserData.put("lastParticipation", firstParticipationDate.getTime());
        } catch (JSONException e) {
            Log.d("DEBUG", e.getMessage());
        }
    }

    public void exitFromRegistration() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Прервать регистрацию");
        alertDialog.setMessage("Вы точно хотите прервать регистрацию?");
        alertDialog.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
                startActivity(intent);
                finish();
            }
        });
        alertDialog.setNegativeButton("Отмена", null);
        alertDialog.show();
    }

    public void showProgress(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
                mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                mProgressView.animate().setDuration(shortAnimTime).
                        setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        previousFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                previousFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
