package com.example.funnynose;

import org.json.JSONArray;

public class User {
    static String phone;

    private User() {}

    static void createUser(String phone) {
        User.phone = phone;
    }

    static void initUser(JSONArray array) {
        // TODO: инициализация необходимых полей после регистрации \ аутентификации
    }
}
