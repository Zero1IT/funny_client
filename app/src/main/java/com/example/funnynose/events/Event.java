package com.example.funnynose.events;

import android.util.Log;

import com.example.funnynose.R;
import com.example.funnynose.constants.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public final class Event {

    static final int ICON_ANOTHER = R.mipmap.another_finish;
    static final int ICON_HOSPITAL = R.mipmap.hospital_finish;
    static final int ICON_TRAINING = R.mipmap.traning_finish;

    private static final Locale sLocale = new Locale("ru", "RU");
    private static final SimpleDateFormat sDate = new SimpleDateFormat("dd-MM-yyyy (kk:mm)", sLocale);

    private long mId;
    private int mIcon;
    private List<String> mUsersName;
    private Date mDate;
    private int mDurationEvent;
    private String mTitle;
    private boolean mFinished;

    Event() {

    }

    Event(JSONObject data) throws JSONException {
        eventFromJson(data);
    }

    private void eventFromJson(JSONObject data) throws JSONException {
        Iterator<String> keys = data.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            switch (key) {
                case "id":
                    setId(data.getLong(key));
                    break;
                case "title":
                    setTitle(data.getString(key));
                    break;
                case "date":
                    setDate(java.sql.Date.valueOf(data.getString(key)));
                    break;
                case "finish":
                    setFinished(data.getBoolean(key));
                    break;
                case "users":
                    usersFromJson(data.getJSONObject(key));
                    break;
                case "duration":
                    setDurationEvent(data.getInt(key));
                    break;
                default:
                    Log.e(Session.TAG, "event error load");
            }
        }
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    private void usersFromJson(JSONObject users) {
        Iterator<String> keys = users.keys();
        while (keys.hasNext()) {
            mUsersName.add(keys.next());
        }
    }

    public Date getDate() {
        return mDate;
    }

    public String getStringDate() {
        return sDate.format(mDate);
    }

    public void setDate(Date date) {
        mDate = date;
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

    public int getDurationEvent() {
        return mDurationEvent;
    }

    public void setDurationEvent(int durationEvent) {
        mDurationEvent = durationEvent;
    }
}
