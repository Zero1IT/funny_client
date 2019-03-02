package com.example.funnynose;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class User {
    // хз как по-другому лучше, но создавать кучу полей бессмысленно, они ещё должны автообновляться
    // для строковых данных
    static Map<String, String> property = new HashMap<>();
    // для числовых
    static Map<String, Long> stats = new HashMap<>();

    private User() {}

    private static boolean init(String phone) {
        //TODO: на сервере реализовать функцию(1), которая будет возвращать массив объектов, не
        //TODO: вводимых при регистрации, функция должна быть локальной в модуле и не экспортируемой.
        //TODO: Далее, реализовать функцию(2), которая будет возвращать все данные пользователя,
        //TODO: используя при этом функцию(1), которая уже выполняет половину работы, функция(2)
        //TODO: должная вернуть 2 массива элементов, для строкового и численного Map

        // TODO: обращение к серверу, в результате должно придти 2 Массива, оба конвертируем так
        // TODO: JSONObject one = (JSONObject) args[0]
        // TODO: JSONObject two = (JSONObject) args[1]
        return true;
    }

    public static boolean init(JSONObject array) {
        Iterator<String> keys =  array.keys();
        try {
            while (keys.hasNext()) {
                String key = keys.next();
                property.put(key, array.getString(key));
            }
        } catch (JSONException e) {
            Log.d(Session.TAG, e.getMessage());
            return false;
        }
        Log.d(Session.TAG, property.size() + "");
        return true;
    }
}
