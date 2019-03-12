package com.example.funnynose.events;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Event {

    private static final Locale sLocale = new Locale("ru", "RU");
    private static final SimpleDateFormat sDate = new SimpleDateFormat("dd-MM-yyyy (kk:mm)", sLocale);

    private List<String> mUserNames;
    private String mDate;

    public Event() {

    }

    public String getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = sDate.format(date);
    }
}
