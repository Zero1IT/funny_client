package com.example.funnynose.users;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.funnynose.R;
import com.example.funnynose.db.UsersCache;
import com.example.funnynose.network.AsyncServerResponse;
import com.example.funnynose.network.SocketAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.socket.emitter.Emitter;

public class UsersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mUserList;
    private UserListAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<UserProfile> userList = new ArrayList<>();

    private Object object;

    private UsersCache usersCache;

    private AsyncServerResponse responseLastUsers;
    private AsyncServerResponse responseRefreshUsers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.users_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserList = view.findViewById(R.id.user_list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mUserList.setLayoutManager(layoutManager);
        adapter = new UserListAdapter(getActivity(), userList);
        mUserList.setAdapter(adapter);

        mSwipeRefreshLayout = view.findViewById(R.id.user_list_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);

        mUserList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        usersCache = new UsersCache();
        initUsers();
    }

    private void initUsers() {
        if (userList.size() == 0) {
            new Runnable() {
                @Override
                public void run() {
                    userList.addAll(usersCache.getUsers());
                    mUserList.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }.run();
            if (SocketAPI.isOnline()) {
                initResponseLastUsers();
                JSONObject obj = new JSONObject();
                try {
                    obj.put("id_", usersCache.getLastUserKey());
                } catch (JSONException e) {
                    Log.d("DEBUG", e.getMessage());
                }

                SocketAPI.getSocket().emit("compare_last_users",obj)
                        .once("compare_last_users", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                object = args[0];
                                if (((JSONArray) object).length() == 0) {
                                    responseLastUsers.setSuccessful(false);
                                } else {
                                    responseLastUsers.setSuccessful(true);
                                }
                                responseLastUsers.setResponse(true);
                            }
                        });
                responseLastUsers.start(getActivity());
            }
        }
    }

    private void initResponseLastUsers() {
        responseLastUsers = new AsyncServerResponse(new AsyncServerResponse.AsyncTask() {
            @Override
            public void call() {
                responseLastUsersCall();
            }
        });
    }

    private void responseLastUsersCall() {
        JSONArray jsonArray = (JSONArray) object;
        JSONObject userJson;
        final ArrayList<UserProfile> tempList = new ArrayList<>(userList);
        for (int i = jsonArray.length() - 1; i >= 0; i--) {
            userJson = jsonArray.optJSONObject(i);
            UserProfile user = new UserProfile(userJson.optLong("id_"),
                    userJson.optString("nickname"), userJson.optString("cityName"),
                    userJson.optLong("lastParticipation"), userJson.optLong("lastChangeDate"));
            tempList.add(user);
            usersCache.addUser(user);
        }

        Collections.sort(tempList, new Comparator<UserProfile>() {
            public int compare(UserProfile o1, UserProfile o2) {
                return o1.nickname.compareTo(o2.nickname);
            }
        });

        userList = tempList; // баг возможен здесь
        mUserList.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
