package by.funnynose.app.adapters;

import by.funnynose.app.events.EventAnotherFragment;
import by.funnynose.app.events.EventCalendarFragment;
import by.funnynose.app.events.EventHospitalFragment;
import by.funnynose.app.events.EventTrainingFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerEventsAdapter extends FragmentPagerAdapter {

    private String[] titles;

    public PagerEventsAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        titles = new String[] {"Календарь мероприятий", "Посещение госпиталей", "Посещение тренингов", "Другое"};
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = EventCalendarFragment.newInstance();
                break;
            case 1:
                fragment = EventHospitalFragment.newInstance();
                break;
            case 2:
                fragment = EventTrainingFragment.newInstance();
                break;
            case 3:
                fragment = EventAnotherFragment.newInstance();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return titles.length;
    }
}
