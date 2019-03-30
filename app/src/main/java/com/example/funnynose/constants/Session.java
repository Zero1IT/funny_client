package com.example.funnynose.constants;


public class Session {

    public static final String TAG = "DEBUG";

    private static Session sSession;

    private Session() {}

    public static Session currentSession() {
        if (sSession == null) {
            throw new NullPointerException("Session doesn't init");
        }
        return sSession;
    }

    public static void initSession() {
        if (sSession != null) {
            return;
        }
        sSession = new Session();
    }

    public static void closeSession() {
        if (sSession == null) {
            return;
        }
        sSession = null;
    }
}