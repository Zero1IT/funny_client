package com.example.funnynose.users.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.funnynose.R;
import com.example.funnynose.users.UserProfile;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.funnynose.Utilities.dateFormat;

public class UserHolder extends RecyclerView.ViewHolder {

    private TextView user_name;
    private TextView user_city;
    private TextView user_last_participation;
    private ImageView user_avatarka;

    public UserHolder(@NonNull View itemView) {
        super(itemView);
        user_name = itemView.findViewById(R.id.user_name);
        user_city = itemView.findViewById(R.id.user_city);
        user_last_participation = itemView.findViewById(R.id.user_last_participation);
        user_avatarka = itemView.findViewById(R.id.user_avatarka);
    }

    public void bind(UserProfile user) {
        user_name.setText(user.nickname);
        user_city.setText(user.city);
        user_last_participation.setText(dateFormat.format(new Date(user.lastParticipation)));
    }
}
