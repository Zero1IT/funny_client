package com.example.funnynose.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.example.funnynose.chat.Message;

import java.util.ArrayList;


public class ChatCache {

    public static final int ONE_TIME_PACKAGE_SIZE = 40;

    private String mChatName;
    private Cursor mCursor;

    private int mIndexKey;

    public ChatCache(String chatName) {
        mChatName = chatName;
    }

    public synchronized ArrayList<Message> getMessagesFromTo(long to) {
        mCursor = Database.getDatabase().rawQuery("SELECT * FROM " +
                (DatabaseHelper.TABLE_CHAT + mChatName) +
                " WHERE " + DatabaseHelper.KEY_ID + " < ? " +
                 " ORDER BY " + DatabaseHelper.KEY_ID +
                " DESC LIMIT ?", new String[] {String.valueOf(to),
                String.valueOf(ONE_TIME_PACKAGE_SIZE)});

        int indexText = mCursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE_TEXT);
        int indexNickname = mCursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE_NICKNAME);
        int indexTime = mCursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE_TIME);
        mIndexKey = mCursor.getColumnIndex(DatabaseHelper.KEY_ID);

        ArrayList<Message> list = new ArrayList<>();
        if (mCursor.moveToLast()) {
            if (mCursor.getPosition() > 0) {
                do {
                    list.add(new Message(mCursor.getString(indexText), mCursor.getString(indexNickname),
                            mCursor.getLong(indexTime), mCursor.getLong(mIndexKey)));
                } while (mCursor.moveToPrevious());
            }
        }

        return list;
    }

    public synchronized ArrayList<Message> getMessagesFromTo() {
        return getMessagesFromTo((long) 1e6);
    }

    public synchronized long getLastMessageKey() {
        mCursor = Database.getDatabase().rawQuery("SELECT " + DatabaseHelper.KEY_ID +
                " FROM " + (DatabaseHelper.TABLE_CHAT + mChatName) +
                " WHERE " + DatabaseHelper.KEY_ID + " = (SELECT MAX(" +
                DatabaseHelper.KEY_ID + ") FROM " + (DatabaseHelper.TABLE_CHAT + mChatName) + ")", null);

        mIndexKey = mCursor.getColumnIndex(DatabaseHelper.KEY_ID);

        if (mCursor.moveToFirst()) {
            return mCursor.getLong(mIndexKey);
        }
        return 0;
    }

    public synchronized void addMessage(Message msg) {
        String sqlQuery = "INSERT OR IGNORE INTO " + (DatabaseHelper.TABLE_CHAT + mChatName) +" (" +
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
