package com.example.funnynose;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.funnynose.constants.Session;
import com.example.funnynose.network.SocketAPI;
import com.google.gson.Gson;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class User {
    private static final User sUser = new User();

    private static final String APP_PREFERENCES = "app_settings";
    private static final String USER_STRING_DATA = "user_string_data";
    private static final String USER_NUMERIC_DATA = "user_numeric_data";

    // для строковых данных
    public static Map<String, String> mStringData = new HashMap<>();
    // для числовых
    public static Map<String, Long> mNumericData = new HashMap<>();

    @Contract(pure = true)
    public static User getInstance() {
        return sUser;
    }

    private User() {}

    static boolean tryConnectUser(Context context) {

        //TODO: дополнить
        if (SocketAPI.isOnline(context)) {
            SocketAPI.getSocket().emit("event_check", "Release later"); //TODO: идентифицировать вошедшего юзера
        } else {
            return false;
        }

        return true;
    }

    public static void userDataFromJson(@NotNull JSONObject array) {
        Iterator<String> keys =  array.keys();
        try {
            while (keys.hasNext()) {
                String key = keys.next();
                if (array.get(key).getClass() == String.class) {
                    mStringData.put(key, array.getString(key));
                } else {
                    mNumericData.put(key, array.getLong(key));
                }
            }
        } catch (JSONException e) {
            Log.d(Session.TAG, e.getMessage());
        }
    }

    public static boolean getUserAppData(@NotNull Context context) {
        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(USER_STRING_DATA) && mSettings.contains(USER_NUMERIC_DATA)) {
            String jsonUserStringData = mSettings.getString(USER_STRING_DATA, "");
            String jsonUserNumericData = mSettings.getString(USER_NUMERIC_DATA, "");

            try {
                userDataFromJson(new JSONObject(jsonUserStringData));
                userDataFromJson(new JSONObject(jsonUserNumericData));
            } catch (JSONException e) {
                Log.d("DEBUG", e.getMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    public static void setUserAppData(@NotNull Context context) {
        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(USER_STRING_DATA, new Gson().toJson(mStringData)); // GSON библиотека только для этих двух строк используется,
        editor.putString(USER_NUMERIC_DATA, new Gson().toJson(mNumericData)); // потому что стандартно если в json кодировать будут ошибки при раскодировке
        editor.apply();
    }

    public static void removeUserAppData(@NotNull Context context) {
        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.remove(USER_STRING_DATA);
        editor.remove(USER_NUMERIC_DATA);
        editor.apply();
    }
}
