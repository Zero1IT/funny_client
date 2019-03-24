package com.example.funnynose.users.holders;

import android.view.View;
import android.widget.TextView;

import com.example.funnynose.R;
import com.example.funnynose.users.UserProfile;


public class UserHolderWithLetter extends UserHolder {

    private TextView user_letter;

    public UserHolderWithLetter(View itemView) {
        super(itemView);
        user_letter = itemView.findViewById(R.id.user_letter);
    }

    @Override
    public void bind(UserProfile user) {
        super.bind(user);
        user_letter.setText(user.nickname.substring(0, 1));
    }
}