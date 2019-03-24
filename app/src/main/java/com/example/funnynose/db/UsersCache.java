package com.example.funnynose.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.example.funnynose.users.UserProfile;

import java.util.ArrayList;

public class UsersCache {

    private static final String getUsersSqlQuery = "SELECT * FROM " +
            DatabaseHelper.TABLE_USERS +
            " ORDER BY " + DatabaseHelper.KEY_USER_NICKNAME;

    private static final String getLastMessageKeySqlQuery = "SELECT " + DatabaseHelper.KEY_ID +
            " FROM " + DatabaseHelper.TABLE_USERS +
            " WHERE " + DatabaseHelper.KEY_ID + " = (SELECT MAX(" +
            DatabaseHelper.KEY_ID + ") FROM " + DatabaseHelper.TABLE_USERS + ")";

    private static final String addUserSqlQuery = "INSERT OR REPLACE INTO " + DatabaseHelper.TABLE_USERS +" (" +
            DatabaseHelper.KEY_ID + ", " + DatabaseHelper.KEY_USER_NICKNAME + ", " +
            DatabaseHelper.KEY_USER_CITY + ", " + DatabaseHelper.KEY_USER_LAST_PARTICIPATION +
            ", " + DatabaseHelper.KEY_USER_LAST_CHANGE + ") VALUES (?, ?, ?, ?, ?)";

    private Cursor cursor;

    public synchronized ArrayList<UserProfile> getUsers() {
        cursor = Database.getDatabase().rawQuery(getUsersSqlQuery, null);

        int indexKey = cursor.getColumnIndex(DatabaseHelper.KEY_ID);
        int indexNickname = cursor.getColumnIndex(DatabaseHelper.KEY_USER_NICKNAME);
        int indexCity = cursor.getColumnIndex(DatabaseHelper.KEY_USER_CITY);
        int indexLastPatricipation = cursor.getColumnIndex(DatabaseHelper.KEY_USER_LAST_PARTICIPATION);
        int indexLastChange = cursor.getColumnIndex(DatabaseHelper.KEY_USER_LAST_CHANGE);

        ArrayList<UserProfile> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                list.add(new UserProfile(cursor.getLong(indexKey), cursor.getString(indexNickname), cursor.getString(indexCity),
                        cursor.getLong(indexLastPatricipation), cursor.getLong(indexLastChange)));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public synchronized long getLastUserKey() {
        cursor = Database.getDatabase().rawQuery(getLastMessageKeySqlQuery, null);
        int indexKey = cursor.getColumnIndex(DatabaseHelper.KEY_ID);
        if (cursor.moveToFirst()) {
            return cursor.getLong(indexKey);
        }
        return 0;
    }

    public synchronized void addUser(UserProfile user) {
        SQLiteStatement sqLiteStatement = Database.getDatabase().compileStatement(addUserSqlQuery);
        sqLiteStatement.bindLong(1, user.index);
        sqLiteStatement.bindString(2, user.nickname);
        sqLiteStatement.bindString(3, user.city);
        sqLiteStatement.bindLong(4, user.lastParticipation);
        sqLiteStatement.bindLong(5, user.lastChange);
        sqLiteStatement.execute();
    }
}
