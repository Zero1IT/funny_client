package com.example.funnynose.users;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UserActivity extends AppCompatActivity {

    private static final String KEY_USER = "user";


    public static Intent newIntent(Context context, UserProfile user) {
        Intent intent = new Intent(context, UserActivity.class);
        if (user != null) {
            intent.putExtra(KEY_USER, user);
        }
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
