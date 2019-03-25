package com.example.funnynose.users.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.funnynose.R;
import com.example.funnynose.users.UserProfile;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.funnynose.Utilities.dateFormat;

public class UserHolder extends RecyclerView.ViewHolder {

    TextView header;
    private TextView name;
    private TextView city;
    private TextView lastParticipation;
    private TextView textParticipation;
    private ImageView avatarka;

    public UserHolder(@NonNull View itemView) {
        super(itemView);
        header = itemView.findViewById(R.id.header);
        name = itemView.findViewById(R.id.name);
        city = itemView.findViewById(R.id.city);
        lastParticipation = itemView.findViewById(R.id.last_participation);
        textParticipation = itemView.findViewById(R.id.text_participation);
        avatarka = itemView.findViewById(R.id.avatarka);
    }

    public void bind(UserProfile user) {
        name.setText(user.nickname);
        city.setText(user.city);
        lastParticipation.setText(dateFormat.format(user.lastParticipation));

        header.setVisibility(View.GONE);
        city.setVisibility(View.VISIBLE);
        lastParticipation.setVisibility(View.VISIBLE);
        textParticipation.setVisibility(View.VISIBLE);
    }

    public void hideCity() {
        city.setVisibility(View.GONE);
    }

    public void hideParticipation() {
        lastParticipation.setVisibility(View.GONE);
        textParticipation.setVisibility(View.GONE);
    }
}