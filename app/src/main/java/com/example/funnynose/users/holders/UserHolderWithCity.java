package com.example.funnynose.users.holders;

import android.view.View;

import com.example.funnynose.users.UserProfile;

import androidx.annotation.NonNull;

public class UserHolderWithCity extends UserHolder {

    public UserHolderWithCity(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(UserProfile user) {
        super.bind(user);
        header.setText(user.city);
        header.setVisibility(View.VISIBLE);
    }
}
