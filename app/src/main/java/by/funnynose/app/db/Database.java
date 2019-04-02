package by.funnynose.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Database {

    private static DatabaseHelper sDbHelper;
    private static SQLiteDatabase sSqLiteDatabase;

    private Database() {}

    public static void initDatabase(Context context) {
        if (sDbHelper == null) {
            sDbHelper = new DatabaseHelper(context);
        }
        if (sSqLiteDatabase == null) {
            sSqLiteDatabase = sDbHelper.getWritableDatabase();
        }
    }

    static SQLiteDatabase getDatabase() {
        return sSqLiteDatabase;
    }

    public static DatabaseHelper getHelper() {
        return sDbHelper;
    }


}
