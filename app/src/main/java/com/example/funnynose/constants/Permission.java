package com.example.funnynose.constants;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Permission {
    public static final int LOSER = 0;
    public static final int MODERATOR = 1;
    public static final int ADMIN = 2;
    public static final int DEVELOPER = 3;

    @NotNull
    @Contract(pure = true)
    public static String toString(int p) {
        switch (p) {
            case LOSER:
                return "Пользователь";
            case MODERATOR:
                return "Модератор";
            case ADMIN:
                return "Администратор";
            case DEVELOPER:
                return "Разработчик";
            default:
                return "Пользователь";
        }
    }
}