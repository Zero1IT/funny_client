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
import com.example.funnynose.constants.Permission;
import com.example.funnynose.R;
import com.example.funnynose.network.SocketAPI;
import com.example.funnynose.constants.User;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import io.socket.emitter.Emitter;

public class RegistrationActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;
    private Fragment[] mFragments;
    private int fragmentIndex;
    private ActionBar mActionBar;
    private ProgressBar mProgressView;

    private JSONObject registrationUserData;

    private boolean successfulRegistration;
    private boolean responseRegistration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setTitle("Регистрация");
        }

        mProgressView = findViewById(R.id.progress);

        mFragmentManager = getSupportFragmentManager();
        mFragments = new Fragment[] {
                new FirstRegistrationFragment(),
                new SecondRegistrationFragment(),
                new ThirdRegistrationFragment() };
        Fragment fragment = mFragmentManager.findFragmentById(R.id.registration_frame);
        if (fragment == null) {
            mFragmentManager.beginTransaction().add(R.id.registration_frame, mFragments[0]).commit();
            fragmentIndex = 0;
        }

        registrationUserData = new JSONObject();
        try {
            registrationUserData.put("status", "");
            registrationUserData.put("permission", Permission.LOSER);
            registrationUserData.put("hospitalsPerYear", 0);
            registrationUserData.put("trainingsPerYear", 0);
            registrationUserData.put("othersPerYear", 0);
        } catch (JSONException e) {
            Log.d("DEBUG", "" + e.getMessage());
        }
    }

    public void nextFragment(){
        if (fragmentIndex < 2) {
            fragmentIndex++;
            mFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.registration_frame, mFragments[fragmentIndex]).commit();
            if (fragmentIndex > 0) {
                if (mActionBar != null) {
                    mActionBar.setHomeButtonEnabled(true);
                    mActionBar.setDisplayHomeAsUpEnabled(true);
                }
            }
        } else {
            responseRegistration = false;
            successfulRegistration = false;
            showProgress(true);
            SocketAPI.getSocket().emit("registration", registrationUserData)
                    .once("registration", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            successfulRegistration = (boolean) args[0];
                            responseRegistration = true;
                        }
                    });
            finishRegistrationInThread();
        }
    }

    // TODO: можно как-то наверное сделать с этим потоком тоже самое
    // TODO: (вынести в класс или тот класс или интерфейс как-то припахать сюда), что и с потоками в фрагментах,
    // TODO: потому что он по структуре такой же
    private void finishRegistrationInThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                long end = System.currentTimeMillis() + 5000;
                while (!responseRegistration && System.currentTimeMillis() < end) {
                    try{
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Log.d("DEBUG", e.getMessage());
                    }
                }
                if (successfulRegistration) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    User.userDataFromJson(registrationUserData);
                    User.setUserAppData(getApplicationContext());
                    finish();
                } else if (!responseRegistration) {
                    if (getCurrentFocus() != null) {
                        Snackbar.make(getCurrentFocus(), "Ошибка соединения",
                                Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    if (getCurrentFocus() != null) {
                        Snackbar.make(getCurrentFocus(),
                                "Пользователь с такими данными уже существует!",
                                Snackbar.LENGTH_SHORT).setAction("OK", CommonRegistrationFragment.snackOkButton).show();
                    }
                }
                showProgress(false);
            }
        }).start();
    }


    public void previousFragment(){
        if (fragmentIndex > 0) {
            fragmentIndex--;
            mFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.registration_frame, mFragments[fragmentIndex]).commit();
            if (fragmentIndex == 0) {
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
            registrationUserData.put("email", email);
            registrationUserData.put("phone", phone);
            registrationUserData.put("password", password);
        } catch (JSONException e) {
            Log.d("DEBUG", e.getMessage());
        }
    }

    public void putSecondFragmentData(String nickname, String name, String surname) {
        try {
            registrationUserData.put("nickname", nickname);
            registrationUserData.put("name", name);
            registrationUserData.put("surname", surname);
        } catch (JSONException e) {
            Log.d("DEBUG", e.getMessage());
        }
    }

    public void putThirdFragmentData(String city, Date birthdayDate, Date firstParticipationDate) {
        try {
            registrationUserData.put("city", city);
            registrationUserData.put("birthday", birthdayDate.getTime());
            registrationUserData.put("firstParticipation", firstParticipationDate.getTime());
            registrationUserData.put("lastParticipation", firstParticipationDate.getTime());
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
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                mProgressView.animate().setDuration(shortAnimTime).
                        setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
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
