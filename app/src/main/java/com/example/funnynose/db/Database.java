package com.example.funnynose.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Database {

    private static DatabaseHelper dbHelper;
    private static SQLiteDatabase database;

    private Database() {}

    public static void initDatabase(Context context) {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context);
        }
        if (database == null) {
            database = dbHelper.getWritableDatabase();
        }

    }

    public static SQLiteDatabase getDatabase() {
        return database;
    }

    public static DatabaseHelper getHelper() {
        return dbHelper;
    }


}
