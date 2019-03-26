package com.example.funnynose.users.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.funnynose.R;
import com.example.funnynose.users.UserProfile;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.funnynose.Utilities.DATE_FORMAT;

public class UserHolder extends RecyclerView.ViewHolder {

    TextView mHeaderView;
    private TextView mNameView;
    private TextView mCityView;
    private TextView mLastParticipationView;
    private TextView mTextParticipationView;
    private ImageView mAvatarka;

    public UserHolder(@NonNull View itemView) {
        super(itemView);
        mHeaderView = itemView.findViewById(R.id.header);
        mNameView = itemView.findViewById(R.id.name);
        mCityView = itemView.findViewById(R.id.city);
        mLastParticipationView = itemView.findViewById(R.id.last_participation);
        mTextParticipationView = itemView.findViewById(R.id.text_participation);
        mAvatarka = itemView.findViewById(R.id.avatarka);
    }

    public void bind(UserProfile user) {
        mNameView.setText(user.nickname);
        mCityView.setText(user.city);
        mLastParticipationView.setText(DATE_FORMAT.format(user.lastParticipation));

        mHeaderView.setVisibility(View.GONE);
        mCityView.setVisibility(View.VISIBLE);
        mLastParticipationView.setVisibility(View.VISIBLE);
        mTextParticipationView.setVisibility(View.VISIBLE);
    }

    public void hideCity() {
        mCityView.setVisibility(View.GONE);
    }

    public void hideParticipation() {
        mLastParticipationView.setVisibility(View.GONE);
        mTextParticipationView.setVisibility(View.GONE);
    }
}