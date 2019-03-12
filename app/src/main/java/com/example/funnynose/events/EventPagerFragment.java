package com.example.funnynose.events;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.funnynose.R;
import com.example.funnynose.adapters.PagerEventsAdapter;
import com.example.funnynose.constants.Session;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class EventPagerFragment extends Fragment {

    public static EventPagerFragment newInstance() {

        Bundle args = new Bundle();
        EventPagerFragment fragment = new EventPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SmartTabLayout tabLayout = view.findViewById(R.id.tab_events);
        ViewPager viewPager = view.findViewById(R.id.pager_events);
        viewPager.setAdapter(new PagerEventsAdapter(getChildFragmentManager()));
        tabLayout.setViewPager(viewPager);
    }
}
