package com.example.funnynose.authentification;

import android.os.Bundle;

import com.example.funnynose.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class RegistrationActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;
    private Fragment mFirstFragment;
    private Fragment mSecondFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registratio);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("Регистрация");
        }

        mFragmentManager = getSupportFragmentManager();
        mFirstFragment = RegistrationFragment.newInstance();
        mSecondFragment = RegistrationContinueFragment.newInstance();
        Fragment fragment = mFragmentManager.findFragmentById(R.id.reg_fragment);
        if (fragment == null) {
            mFragmentManager.beginTransaction().add(R.id.reg_fragment, mFirstFragment).commit();
        }

    }

    public void goNext() {
        mFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.reg_fragment, mSecondFragment).commit();
    }

    public void goBack() {
        mFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.reg_fragment, mFirstFragment).commit();
    }
}
