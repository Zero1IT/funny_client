package com.example.funnynose.events;

import com.example.funnynose.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Event {

    public static final int ICON_ANOTHER = R.mipmap.another_finish;
    public static final int ICON_HOSPITAL = R.mipmap.hospital_finish;
    public static final int ICON_TRANING = R.mipmap.traning_finish;

    private static final Locale sLocale = new Locale("ru", "RU");
    private static final SimpleDateFormat sDate = new SimpleDateFormat("dd-MM-yyyy (kk:mm)", sLocale);

    private int mIcon;

    private List<String> mUsersName;
    private String mTitle;
    private String mDate;
    private boolean mFinished;

    public Event() {

    }

    public String getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = sDate.format(date);
    }

    public boolean isFinished() {
        return mFinished;
    }

    public void setFinished(boolean finished) {
        mFinished = finished;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public List<String> getUsersName() {
        return mUsersName;
    }

    public void setUsersName(List<String> usersName) {
        mUsersName = usersName;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int icon) {
        mIcon = icon;
    }
}
