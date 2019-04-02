package by.funnynose.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import by.funnynose.app.network.SocketAPI;

public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "funnynose_cache_db";

    static final String KEY_ID = _ID; // для всех

    // сообщения
    static final String TABLE_CHAT = "chat_";
    static final String KEY_MESSAGE_TEXT = "messageText";
    static final String KEY_MESSAGE_NICKNAME = "nickname";
    static final String KEY_MESSAGE_TIME = "messageTime";

    // пользователи
    static final String TABLE_USERS = "users";
    static final String KEY_USER_NICKNAME = "nickname";
    static final String KEY_USER_CITY = "city";
    static final String KEY_USER_LAST_PARTICIPATION = "lastParticipation";
    static final String KEY_USER_LAST_CHANGE = "lastChange";
    //static final String KEY_USER_IMAGE_SIGNATURE = "imageSignature";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String chatName : SocketAPI.sChatNames) {
            db.execSQL("CREATE TABLE " + (TABLE_CHAT + chatName) + " (" + KEY_ID
                    + " INTEGER PRIMARY KEY," + KEY_MESSAGE_TEXT + " TEXT, "
                    + KEY_MESSAGE_NICKNAME + " TEXT, " + KEY_MESSAGE_TIME + " INTEGER)");
        }

        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" + KEY_ID
                + " INTEGER PRIMARY KEY," + KEY_USER_NICKNAME + " TEXT, "
                + KEY_USER_CITY + " TEXT, " + KEY_USER_LAST_PARTICIPATION + " INTEGER, "
                + KEY_USER_LAST_CHANGE + " INTEGER)");// + KEY_USER_IMAGE_SIGNATURE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        resetTables(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        resetTables(db);
    }

    private void resetTables(SQLiteDatabase db) {
        for (String chatName : SocketAPI.sChatNames) {
            db.execSQL("DROP TABLE IF EXISTS " + (TABLE_CHAT + chatName));
        }
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}