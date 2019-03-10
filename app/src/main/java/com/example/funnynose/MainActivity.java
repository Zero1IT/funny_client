package com.example.funnynose;

import android.os.Bundle;

import com.example.funnynose.events.EventListFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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

    private Fragment mFragmentToOpen;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    //TODO; возможно можно сделать лучше
    Fragment[] mFragments = new Fragment[] {
            EventListFragment.newInstance(), ChatFragment.newInstance()
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        mDrawerLayout.addDrawerListener(mDrawerListener);
        toggle.syncState();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_frame_layout);
        if (fragment == null) {
            fragment = mFragments[0];
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

        if (id == R.id.nav_camera) {
            mToolbar.setTitle("Event");
            mFragmentToOpen = mFragments[0];
        } else if (id == R.id.nav_gallery) {
            mToolbar.setTitle("Chat");
            mFragmentToOpen = mFragments[1];
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
