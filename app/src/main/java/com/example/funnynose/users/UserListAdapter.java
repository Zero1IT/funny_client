package com.example.funnynose.users;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.funnynose.R;
import com.example.funnynose.users.holders.UserHolder;
import com.example.funnynose.users.holders.UserHolderWithCity;
import com.example.funnynose.users.holders.UserHolderWithLetter;
import com.example.funnynose.users.holders.UserHolderWithParticipation;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.funnynose.Utilities.dateFormat;
import static com.example.funnynose.users.UsersFragment.SORT_BY_ABC;
import static com.example.funnynose.users.UsersFragment.SORT_BY_CITY;
import static com.example.funnynose.users.UsersFragment.SORT_BY_PARTICIPATION;

public class UserListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_USER = 0;
    private static final int VIEW_TYPE_USER_WITH_LETTER = 1;
    private static final int VIEW_TYPE_USER_WITH_CITY = 2;
    private static final int VIEW_TYPE_USER_WITH_PARTICIPATION = 3;

    private UsersFragment fragment;

    private ArrayList<UserProfile> userList;
    //public static boolean[] checkImage;

    private FragmentActivity activity;

    UserListAdapter(FragmentActivity activity, ArrayList<UserProfile> userList, UsersFragment fragment) {
        this.activity = activity;
        this.userList = userList;
        this.fragment = fragment;

        //if (checkImage == null) {
        //    checkImage = new boolean[userList.size()];
        //}
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);

        final RecyclerView.ViewHolder holder;

        if (viewType == VIEW_TYPE_USER_WITH_LETTER) {
            holder = new UserHolderWithLetter(view);
        } else if (viewType == VIEW_TYPE_USER_WITH_CITY) {
            holder = new UserHolderWithCity(view);
        } else if (viewType == VIEW_TYPE_USER_WITH_PARTICIPATION) {
            holder = new UserHolderWithParticipation(view);
        } else {
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
            case VIEW_TYPE_USER_WITH_CITY:
                ((UserHolderWithCity)holder).bind(otherUser);
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
            case VIEW_TYPE_USER_WITH_PARTICIPATION:
                ((UserHolderWithParticipation)holder).bind(otherUser);
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
        }

        if (fragment.getCurrentSortType() == SORT_BY_CITY) {
            ((UserHolder) holder).hideCity();
        } else if (fragment.getCurrentSortType() == SORT_BY_PARTICIPATION) {
            ((UserHolder) holder).hideParticipation();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (fragment.getCurrentSortType() == SORT_BY_ABC) {
            if (position == 0 || !userList.get(position).nickname.substring(0, 1)
                    .equals(userList.get(position - 1).nickname.substring(0, 1))) {
                return VIEW_TYPE_USER_WITH_LETTER;
            }
        } else if (fragment.getCurrentSortType() == SORT_BY_CITY) {
            if (position == 0 || !userList.get(position).city
                    .equals(userList.get(position - 1).city)) {
                return VIEW_TYPE_USER_WITH_CITY;
            }
        } else if (fragment.getCurrentSortType() == SORT_BY_PARTICIPATION) {
            if (position == 0 || !dateFormat.format(userList.get(position).lastParticipation)
                    .equals(dateFormat.format(userList.get(position - 1).lastParticipation))) {
                return VIEW_TYPE_USER_WITH_PARTICIPATION;
            }
        }
        return VIEW_TYPE_USER;
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