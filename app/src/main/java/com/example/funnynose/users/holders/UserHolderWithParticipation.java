package com.example.funnynose.users.holders;

import android.view.View;

import com.example.funnynose.users.UserProfile;

import androidx.annotation.NonNull;

import static com.example.funnynose.Utilities.DATE_FORMAT;

public class UserHolderWithParticipation extends UserHolder {

    public UserHolderWithParticipation(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(UserProfile user) {
        super.bind(user);
        mHeaderView.setText(DATE_FORMAT.format(user.lastParticipation));
        mHeaderView.setVisibility(View.VISIBLE);
    }
}
