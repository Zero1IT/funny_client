package com.example.funnynose;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.funnynose.events.EventPagerFragment;
import com.example.funnynose.chat.DoubleChatFragment;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.funnynose.users.UserActivity;
import com.example.funnynose.users.UserProfile;
import com.example.funnynose.users.UsersFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int EVENTS = 0;
    private static final int CHAT = 1;
    private static final int USERS = 2;

    private Fragment mFragmentToOpen;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private MenuItem mChatChangeCity;
    private MenuItem mUsersSortType;

    private Fragment[] mFragments = new Fragment[] {
            EventPagerFragment.newInstance(), new DoubleChatFragment(),
            new UsersFragment()
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        mDrawerLayout.addDrawerListener(mDrawerListener);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_frame_layout);
        if (fragment == null) {
            fragment = mFragments[EVENTS];
            fragmentManager.beginTransaction().add(R.id.main_frame_layout, fragment).commit();
            navigationView.setCheckedItem(R.id.nav_events);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);

        TextView nickname = findViewById(R.id.nickname);
        TextView surnameAndName = findViewById(R.id.surname_and_name);
        ImageView userImage = findViewById(R.id.user_image);

        nickname.setText(User.mStringData.get("nickname"));
        String temp = User.mStringData.get("surname") + " " + User.mStringData.get("name");
        surnameAndName.setText(temp);

        findViewById(R.id.drawer_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long id = User.mNumericData.get("id_");
                Long lastParticipation = User.mNumericData.get("lastParticipation");
                Long lastChangeDate = User.mNumericData.get("lastChangeDate");

                if (id != null && lastParticipation != null && lastChangeDate != null) {
                    Intent intent = UserActivity.newIntent(getApplicationContext(),
                            new UserProfile(id, User.mStringData.get("nickname"),
                                    User.mStringData.get("city"), lastParticipation,
                                    lastChangeDate));
                    startActivity(intent);
                }
            }
        });


        mChatChangeCity = menu.findItem(R.id.chat_change_city);
        mChatChangeCity.setVisible(false);
        mUsersSortType = menu.findItem(R.id.users_sort_type);
        mUsersSortType.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.chat_change_city) {
            if (mFragmentToOpen.getClass() == DoubleChatFragment.class) {
                ((DoubleChatFragment) mFragmentToOpen).openChooseCityDialog();
            }
            return true;
        }
        else if (id == R.id.users_sort_type) {
            if (mFragmentToOpen.getClass() == UsersFragment.class) {
                ((UsersFragment) mFragmentToOpen).openChooseSortTypeDialog();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_events) {
            mChatChangeCity.setVisible(false);
            mUsersSortType.setVisible(false);

            mToolbar.setTitle("Мероприятия");
            mFragmentToOpen = mFragments[EVENTS];
        } else if (id == R.id.nav_chat) {
            mChatChangeCity.setVisible(true);
            mUsersSortType.setVisible(false);

            mToolbar.setTitle("Чат");
            mFragmentToOpen = mFragments[CHAT];
        } else if (id == R.id.nav_users) {
            mChatChangeCity.setVisible(false);
            mUsersSortType.setVisible(true);

            mToolbar.setTitle("Пользователи");
            mFragmentToOpen = mFragments[USERS];
        } else if (id == R.id.nav_settings) {

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private final DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            float slideX = drawerView.getWidth() * slideOffset;
            View view = findViewById(R.id.app_bar);
            view.setTranslationX(slideX);
        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) { }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (mFragmentToOpen != null) {
                if (mFragmentToOpen != fragmentManager.findFragmentById(R.id.main_frame_layout)) {
                    fragmentManager.beginTransaction().replace(R.id.main_frame_layout, mFragmentToOpen).commit();
                }
            }
        }

        @Override
        public void onDrawerStateChanged(int newState) { }

    };
}
