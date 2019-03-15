package com.example.funnynose;

import android.os.Bundle;

import com.example.funnynose.events.EventPagerFragment;
import com.example.funnynose.chat.ChatActivityFragment;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

    private Fragment mFragmentToOpen;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private Fragment[] mFragments = new Fragment[] {
            EventPagerFragment.newInstance(), new ChatActivityFragment()
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

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_frame_layout);
        if (fragment == null) {
            fragment = mFragments[EVENTS];
            fragmentManager.beginTransaction().add(R.id.main_frame_layout, fragment).commit();
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_events) {
            mToolbar.setTitle("Event");
            mFragmentToOpen = mFragments[EVENTS];
        } else if (id == R.id.nav_chat) {
            mToolbar.setTitle("Chat");
            mFragmentToOpen = mFragments[CHAT];
        } else if (id == R.id.nav_users) {

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
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) { }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) { }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (mFragmentToOpen == null) {
                return;
            }
            if (mFragmentToOpen != fragmentManager.findFragmentById(R.id.main_frame_layout)) {
                fragmentManager.beginTransaction().replace(R.id.main_frame_layout, mFragmentToOpen).commit();
            }
        }

        @Override
        public void onDrawerStateChanged(int newState) { }
    };
}
