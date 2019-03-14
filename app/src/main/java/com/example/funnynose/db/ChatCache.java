package com.example.funnynose.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.example.funnynose.chat.Message;

import java.util.ArrayList;


public class ChatCache {

    public static final int ONE_TIME_PACKAGE_SIZE = 30;

    private String chatName;
    private Cursor cursor;

    private int indexText;
    private int indexNickname;
    private int indexTime;
    private int indexKey;

    public ChatCache(String chatName) {
        this.chatName = chatName;
    }


    public ArrayList<Message> getMessagesFromTo(long from, long to) {
        cursor = Database.getDatabase().rawQuery("SELECT " + DatabaseHelper.KEY_MESSAGE_KEY + ", " +
                DatabaseHelper.KEY_MESSAGE_TEXT + ", " + DatabaseHelper.KEY_MESSAGE_NICKNAME + ", " +
                DatabaseHelper.KEY_MESSAGE_TIME + " FROM " + (DatabaseHelper.TABLE_CHAT + chatName) +
                " WHERE " + DatabaseHelper.KEY_MESSAGE_KEY + " > ? AND " +
                DatabaseHelper.KEY_MESSAGE_KEY + " < ? " +
                 " ORDER BY " + DatabaseHelper.KEY_MESSAGE_KEY +
                " DESC LIMIT ?", new String[] {String.valueOf(from), String.valueOf(to),
                String.valueOf(ONE_TIME_PACKAGE_SIZE)});

        indexText = cursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE_TEXT);
        indexNickname = cursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE_NICKNAME);
        indexTime = cursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE_TIME);
        indexKey = cursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE_KEY);

        ArrayList<Message> list = new ArrayList<>();
        if (cursor.moveToLast()) {
            do {
                list.add(new Message(cursor.getString(indexText), cursor.getString(indexNickname),
                        cursor.getLong(indexTime), cursor.getLong(indexKey)));
            } while (cursor.moveToPrevious());
        }

        return list;
    }

    public ArrayList<Message> getMessagesFromTo(long to) {
        return getMessagesFromTo(0, to);
    }

    public ArrayList<Message> getMessagesFromTo() {
        return getMessagesFromTo((long) 1e6);
    }

    public long getLastMessageKey() {
        cursor = Database.getDatabase().rawQuery("SELECT " + DatabaseHelper.KEY_MESSAGE_KEY +
                " FROM " + (DatabaseHelper.TABLE_CHAT + chatName) +
                " WHERE " + DatabaseHelper.KEY_MESSAGE_KEY + " = (SELECT MAX(" +
                DatabaseHelper.KEY_MESSAGE_KEY + ") FROM " + (DatabaseHelper.TABLE_CHAT + chatName) + ")", null);

        indexText = cursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE_TEXT);
        indexNickname = cursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE_NICKNAME);
        indexTime = cursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE_TIME);
        indexKey = cursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE_KEY);

        if (cursor.moveToFirst()) {
            return cursor.getLong(indexKey);
        }
        return 0;
    }

    public void addMessage(Message msg) {
        String sqlQuery = "INSERT OR IGNORE INTO " + (DatabaseHelper.TABLE_CHAT + chatName) +" (" +
                DatabaseHelper.KEY_MESSAGE_KEY + ", " + DatabaseHelper.KEY_MESSAGE_TEXT + ", " +
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
