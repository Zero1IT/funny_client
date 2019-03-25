package com.example.funnynose.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.example.funnynose.chat.Message;

import java.util.ArrayList;


public class ChatCache {

    public static final int ONE_TIME_PACKAGE_SIZE = 40;

    private String chatName;
    private Cursor cursor;

    private int indexKey;

    public ChatCache(String chatName) {
        this.chatName = chatName;
    }

    public synchronized ArrayList<Message> getMessagesFromTo(long to) {
        cursor = Database.getDatabase().rawQuery("SELECT * FROM " +
                (DatabaseHelper.TABLE_CHAT + chatName) +
                " WHERE " + DatabaseHelper.KEY_ID + " < ? " +
                 " ORDER BY " + DatabaseHelper.KEY_ID +
                " DESC LIMIT ?", new String[] {String.valueOf(to),
                String.valueOf(ONE_TIME_PACKAGE_SIZE)});

        int indexText = cursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE_TEXT);
        int indexNickname = cursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE_NICKNAME);
        int indexTime = cursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE_TIME);
        indexKey = cursor.getColumnIndex(DatabaseHelper.KEY_ID);

        ArrayList<Message> list = new ArrayList<>();
        if (cursor.moveToLast()) {
            if (cursor.getPosition() > 0) {
                do {
                    list.add(new Message(cursor.getString(indexText), cursor.getString(indexNickname),
                            cursor.getLong(indexTime), cursor.getLong(indexKey)));
                } while (cursor.moveToPrevious());
            }
        }

        return list;
    }

    public synchronized ArrayList<Message> getMessagesFromTo() {
        return getMessagesFromTo((long) 1e6);
    }

    public synchronized long getLastMessageKey() {
        cursor = Database.getDatabase().rawQuery("SELECT " + DatabaseHelper.KEY_ID +
                " FROM " + (DatabaseHelper.TABLE_CHAT + chatName) +
                " WHERE " + DatabaseHelper.KEY_ID + " = (SELECT MAX(" +
                DatabaseHelper.KEY_ID + ") FROM " + (DatabaseHelper.TABLE_CHAT + chatName) + ")", null);

        indexKey = cursor.getColumnIndex(DatabaseHelper.KEY_ID);

        if (cursor.moveToFirst()) {
            return cursor.getLong(indexKey);
        }
        return 0;
    }

    public synchronized void addMessage(Message msg) {
        String sqlQuery = "INSERT OR IGNORE INTO " + (DatabaseHelper.TABLE_CHAT + chatName) +" (" +
                DatabaseHelper.KEY_ID + ", " + DatabaseHelper.KEY_MESSAGE_TEXT + ", " +
                DatabaseHelper.KEY_MESSAGE_NICKNAME + ", " + DatabaseHelper.KEY_MESSAGE_TIME +
                ") VALUES (?, ?, ?, ?)";
        SQLiteStatement sqLiteStatement = Database.getDatabase().compileStatement(sqlQuery);
        sqLiteStatement.bindLong(1, msg.key);
        sqLiteStatement.bindString(2, msg.text);
        sqLiteStatement.bindString(3, msg.nickname);
        sqLiteStatement.bindLong(4, msg.time.getTime());
        sqLiteStatement.execute();
    }

}
