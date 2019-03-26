package com.example.funnynose.chat;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object){
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    void addFragmentByIndex(Fragment fragment, String title, int index) {
        mFragmentList.add(index, fragment);
        mFragmentTitleList.add(index, title);
    }

    void addFragment(Fragment fragment, String title) {
        addFragmentByIndex(fragment, title, 0);
    }

    private void removeFragment(Fragment fragment) {
        mFragmentTitleList.remove(mFragmentList.indexOf(fragment));
        mFragmentList.remove(fragment);
        notifyDataSetChanged();
    }

    void replaceFirstFragment(Fragment fragment, String title) {
        removeFragment(mFragmentList.get(0));
        addFragment(fragment, title);
        notifyDataSetChanged();
    }
}