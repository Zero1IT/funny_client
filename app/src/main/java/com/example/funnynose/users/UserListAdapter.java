package com.example.funnynose.users;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.funnynose.R;
import com.example.funnynose.users.holders.UserHolder;
import com.example.funnynose.users.holders.UserHolderWithLetter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class UserListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_USER_WITH_LETTER = 1;
    private static final int VIEW_TYPE_USER = 2;

    private ArrayList<UserProfile> userList;
    //public static boolean[] checkImage;

    private FragmentActivity activity;

    UserListAdapter(FragmentActivity activity, ArrayList<UserProfile> userList) {
        this.activity = activity;
        this.userList = userList;
        //if (checkImage == null) {
        //    checkImage = new boolean[userList.size()];
        //}
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        final RecyclerView.ViewHolder holder;

        if (viewType == VIEW_TYPE_USER_WITH_LETTER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_with_letter, parent, false);
            holder = new UserHolderWithLetter(view);
        } else if (viewType == VIEW_TYPE_USER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false);
            holder = new UserHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false);
            holder = new UserHolder(view);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = UserActivity.newIntent(activity, userList.get(position));
                    activity.startActivity(intent);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserProfile otherUser = userList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_USER_WITH_LETTER:
                ((UserHolderWithLetter)holder).bind(otherUser);
                /*
                UserImageLoader.setPlug(((UserHolderWithLetter)holder).list_user_avatarka);
                if (!Session.imageSignatureCache.imageSignatureMap.containsKey(otherUser.phone) && Session.currentOnline() && !checkImage[position]) {
                    UserImageLoader.setNewAndCacheAsync(((UserHolderWithLetter)holder).list_user_avatarka, otherUser.phone);
                    checkImage[position] = true;
                } else if (Session.imageSignatureCache.imageSignatureMap.containsKey(otherUser.phone)) {
                    UserImageLoader.setIfExists(((UserHolderWithLetter)holder).list_user_avatarka, otherUser.phone);
                }
                */
                break;
            case VIEW_TYPE_USER:
                ((UserHolder)holder).bind(otherUser);
                /*
                UserImageLoader.setPlug(((UserHolder)holder).list_user_avatarka);
                if (!Session.imageSignatureCache.imageSignatureMap.containsKey(otherUser.phone) && Session.currentOnline() && !checkImage[position]) {
                    UserImageLoader.setNewAndCacheAsync(((UserHolder)holder).list_user_avatarka, otherUser.phone);
                    checkImage[position] = true;
                } else if (Session.imageSignatureCache.imageSignatureMap.containsKey(otherUser.phone)) {
                    UserImageLoader.setIfExists(((UserHolder)holder).list_user_avatarka, otherUser.phone);
                }
                */
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_USER_WITH_LETTER;
        } else if (!userList.get(position).nickname.substring(0, 1).equals(userList.get(position - 1).nickname.substring(0, 1))) {
            return VIEW_TYPE_USER_WITH_LETTER;
        } else {
            return VIEW_TYPE_USER;
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    /*
    public void resetCheckImage(boolean bool) {
        checkImage = new boolean[userList.size()];
        Arrays.fill(checkImage, bool);
    }
    */
}