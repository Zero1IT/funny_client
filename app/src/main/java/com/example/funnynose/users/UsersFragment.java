package com.example.funnynose.users;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.socket.emitter.Emitter;

public class UsersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    static final int SORT_BY_ABC = 0;
    static final int SORT_BY_CITY = 1;
    private static final int SORT_BY_PARTICIPATION_INCREASE = 2;
    private static final int SORT_BY_PARTICIPATION_DECREASE = 3;
    static final int SORT_BY_PARTICIPATION = 100;

    private int currentSortType = SORT_BY_ABC;

    private RecyclerView mUserList;
    private UserListAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FragmentActivity activity;

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
        activity = getActivity();

        mUserList = view.findViewById(R.id.user_list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        mUserList.setLayoutManager(layoutManager);
        adapter = new UserListAdapter(activity, userList, this);
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
                responseLastUsers.start(activity);
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

        sortUsers(SORT_BY_ABC, tempList);

        userList.clear();
        userList.addAll(tempList);
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

    private void sortUsers(int sortType, ArrayList<UserProfile> list) {
        if (sortType == SORT_BY_ABC) {
            Collections.sort(list, new Comparator<UserProfile>() {
                public int compare(UserProfile o1, UserProfile o2) {
                    return o1.nickname.compareTo(o2.nickname);
                }
            });
            currentSortType = SORT_BY_ABC;
        } else if (sortType == SORT_BY_CITY) {
            Collections.sort(list, new Comparator<UserProfile>() {
                public int compare(UserProfile o1, UserProfile o2) {
                    return o1.city.compareTo(o2.city);
                }
            });
            currentSortType = SORT_BY_CITY;
        } else if (sortType == SORT_BY_PARTICIPATION_INCREASE) {
            sortByParticipation(list, true);
            currentSortType = SORT_BY_PARTICIPATION;
        } else if (sortType == SORT_BY_PARTICIPATION_DECREASE) {
            sortByParticipation(list, false);
            currentSortType = SORT_BY_PARTICIPATION;
        }
    }

    private void sortByParticipation(ArrayList<UserProfile> list, boolean increase) {
        if (increase) {
            Collections.sort(list, new Comparator<UserProfile>() {
                public int compare(UserProfile o1, UserProfile o2) {
                    return o1.lastParticipation.compareTo(o2.lastParticipation);
                }
            });
        } else {
            Collections.sort(list, new Comparator<UserProfile>() {
                public int compare(UserProfile o1, UserProfile o2) {
                    return o2.lastParticipation.compareTo(o1.lastParticipation);
                }
            });
        }
    }

    public void openChooseSortTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Сортировать по")
                .setItems(new String[]{"Алфавиту", "Городу", "Дате участия (возрастание)",
                        "Дате участия (убывание)"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sortUsers(which, userList);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
        builder.show();
    }

    int getCurrentSortType() {
        return currentSortType;
    }
}