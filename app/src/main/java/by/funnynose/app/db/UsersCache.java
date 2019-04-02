package by.funnynose.app.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import by.funnynose.app.users.UserProfile;

import java.util.ArrayList;

public class UsersCache {

    private static final String GET_USERS_SQL_QUERY = "SELECT * FROM " +
            DatabaseHelper.TABLE_USERS +
            " ORDER BY " + DatabaseHelper.KEY_USER_NICKNAME;

    private static final String GET_LAST_MESSAGE_KEY_SQL_QUERY = "SELECT " + DatabaseHelper.KEY_ID +
            " FROM " + DatabaseHelper.TABLE_USERS +
            " WHERE " + DatabaseHelper.KEY_ID + " = (SELECT MAX(" +
            DatabaseHelper.KEY_ID + ") FROM " + DatabaseHelper.TABLE_USERS + ")";

    private static final String addUserSqlQuery = "INSERT OR REPLACE INTO " + DatabaseHelper.TABLE_USERS +" (" +
            DatabaseHelper.KEY_ID + ", " + DatabaseHelper.KEY_USER_NICKNAME + ", " +
            DatabaseHelper.KEY_USER_CITY + ", " + DatabaseHelper.KEY_USER_LAST_PARTICIPATION +
            ", " + DatabaseHelper.KEY_USER_LAST_CHANGE + ") VALUES (?, ?, ?, ?, ?)";

    private Cursor mCursor;

    public synchronized ArrayList<UserProfile> getUsers() {
        mCursor = Database.getDatabase().rawQuery(GET_USERS_SQL_QUERY, null);

        int indexKey = mCursor.getColumnIndex(DatabaseHelper.KEY_ID);
        int indexNickname = mCursor.getColumnIndex(DatabaseHelper.KEY_USER_NICKNAME);
        int indexCity = mCursor.getColumnIndex(DatabaseHelper.KEY_USER_CITY);
        int indexLastPatricipation = mCursor.getColumnIndex(DatabaseHelper.KEY_USER_LAST_PARTICIPATION);
        int indexLastChange = mCursor.getColumnIndex(DatabaseHelper.KEY_USER_LAST_CHANGE);

        ArrayList<UserProfile> list = new ArrayList<>();
        if (mCursor.moveToFirst()) {
            do {
                list.add(new UserProfile(mCursor.getLong(indexKey), mCursor.getString(indexNickname), mCursor.getString(indexCity),
                        mCursor.getLong(indexLastPatricipation), mCursor.getLong(indexLastChange)));
            } while (mCursor.moveToNext());
        }
        return list;
    }

    public synchronized long getLastUserKey() {
        mCursor = Database.getDatabase().rawQuery(GET_LAST_MESSAGE_KEY_SQL_QUERY, null);
        int indexKey = mCursor.getColumnIndex(DatabaseHelper.KEY_ID);
        if (mCursor.moveToFirst()) {
            return mCursor.getLong(indexKey);
        }
        return 0;
    }

    public synchronized void addUser(UserProfile user) {
        SQLiteStatement sqLiteStatement = Database.getDatabase().compileStatement(addUserSqlQuery);
        sqLiteStatement.bindLong(1, user.index);
        sqLiteStatement.bindString(2, user.nickname);
        sqLiteStatement.bindString(3, user.city);
        sqLiteStatement.bindLong(4, user.lastParticipation.getTime());
        sqLiteStatement.bindLong(5, user.lastChange.getTime());
        sqLiteStatement.execute();
    }
}
