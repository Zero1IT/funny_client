package com.example.funnynose;



// сейчас не используется даже, потому что все части которые в Messenger'е мы скинули сюда, теперь наоборот выносим и разделяем
public class Session {

    public static final String TAG = "DEBUG";

    private static Session mSession;

    private Session() {}

    public static Session currentSession() {
        if (mSession == null) {
            throw new NullPointerException("Session doesn't init");
        }
        return mSession;
    }

    public static void initSession() {
        if (mSession != null) {
            return;
        }
        mSession = new Session();
    }

    public static void closeSession() {
        if (mSession == null) {
            return;
        }
        mSession = null;
    }
}