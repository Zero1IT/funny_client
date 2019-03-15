package com.example.funnynose.chat;


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

    public ViewPagerAdapter(FragmentManager fm) {
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
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(0, fragment);
        mFragmentTitleList.add(0, title);
    }

    private void removeFragment(Fragment fragment) {
        mFragmentTitleList.remove(mFragmentList.indexOf(fragment));
        mFragmentList.remove(fragment);
        notifyDataSetChanged();
    }

    public void replaceFirstFragment(Fragment fragment, String title) {
        removeFragment(mFragmentList.get(0));
        addFragment(fragment, title);
        notifyDataSetChanged();
    }



}