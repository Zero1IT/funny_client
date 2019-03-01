package com.example.funnynose;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.util.Log;


import java.net.URL;
import java.net.URLConnection;

public class Session {

    public static final String TAG = "DEBUG";

    public static final String APP_PREFERENCES = "app_settings";
    public static final String APP_PREFERENCES_LOGIN = "login";
    public static final String APP_PREFERENCES_PHONE = "phone";
    public static final String APP_PREFERENCES_PASSWORD = "password";
    public static final String APP_PREFERENCES_STATUS = "status";

    private static Session mSession;
    //private static boolean mSessionActive;
    //private static boolean mOnline;

    private Session() {}

    public static Session currentSession() {
        if (mSession == null) {
            throw new NullPointerException("Session doesn't init");
        }
        return mSession;
    }

    public static void initSession() {
        if (mSession != null) {
            return;
        }
        mSession = new Session();
        //mSessionActive = true;
        //mOnline = isOnline();
        //checkOnline();
    }

    public static void closeSession() {
        if (mSession == null) {
            return;
        }
        mSession = null;
        //mSessionActive = false;
    }

    //public static UserProfile currentUser() {
    //    return mUser;
    //}

    //public static boolean isActive() {
    //    return mSessionActive;
    //}

    //public static boolean currentOnline() { return mOnline; }

    //private static void checkOnline() {
    //    new Handler().postDelayed(new Runnable() {
    //        @Override
    //        public void run() {
    //            mOnline = isOnline();
    //            checkOnline();
    //        }
    //    }, 100);
    //}

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            try {
                URLConnection connection = new URL("http://yandex.ru").openConnection();
                connection.setConnectTimeout(1000);
                connection.connect();
                return true;
            } catch (NetworkOnMainThreadException e) {
                return true;
            } catch (Exception e){
                return false;
            }
        }
        return false;
    }

    /*
    public static UserProfile getUserAppData() {
        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_PREFERENCES_LOGIN)) {
            String login = mSettings.getString(APP_PREFERENCES_LOGIN, "");
            String phone = mSettings.getString(APP_PREFERENCES_PHONE, "");
            String password = mSettings.getString(APP_PREFERENCES_PASSWORD, "");
            int status = mSettings.getInt(APP_PREFERENCES_STATUS, 1);

            return new UserProfile(login, phone, password, status);
        }
        return null;
    }

    public static void setUserAppData(UserProfile user) {
        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_PHONE, user.phone);
        editor.putString(APP_PREFERENCES_PASSWORD, user.password);
        editor.putString(APP_PREFERENCES_LOGIN, user.login);
        editor.putInt(APP_PREFERENCES_STATUS, user.status);
        editor.apply();
    }

    public static void removeUserAppData() {
        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.remove(APP_PREFERENCES_LOGIN);
        editor.remove(APP_PREFERENCES_PHONE);
        editor.remove(APP_PREFERENCES_PASSWORD);
        editor.remove(APP_PREFERENCES_STATUS);
        editor.apply();
    }
    */
}