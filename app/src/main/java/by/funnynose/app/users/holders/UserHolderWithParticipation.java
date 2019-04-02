package by.funnynose.app.users.holders;

import android.view.View;

import by.funnynose.app.users.UserProfile;

import androidx.annotation.NonNull;
import by.funnynose.app.Utilities;

public class UserHolderWithParticipation extends UserHolder {

    public UserHolderWithParticipation(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(UserProfile user) {
        super.bind(user);
        mHeaderView.setText(Utilities.DATE_FORMAT.format(user.lastParticipation));
        mHeaderView.setVisibility(View.VISIBLE);
    }
}
