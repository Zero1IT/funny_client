package com.example.funnynose.users.holders;

import android.view.View;

import com.example.funnynose.users.UserProfile;


public class UserHolderWithLetter extends UserHolder {

    public UserHolderWithLetter(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(UserProfile user) {
        super.bind(user);
        mHeaderView.setText(user.nickname.substring(0, 1));
        mHeaderView.setVisibility(View.VISIBLE);
    }
}