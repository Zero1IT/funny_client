package com.example.funnynose;

import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketAPI {

    //private final static String URL = "http://app.funnynose.by";
    //private final static String URL = "http://192.168.0.105:3000";
    private final static String URL = "http://192.168.43.1:3000";

    private static Socket mSocket;

    private SocketAPI() {}

    public static Socket getSocket() {
        if (mSocket == null) {
            try {
                mSocket = IO.socket(URL);
                mSocket.connect();
            } catch (URISyntaxException e) {
                Log.d(Session.TAG, e.getMessage());
            }
        }
        return mSocket;
    }

    public static void closeSocket() {
        if (mSocket == null) {
            return;
        }
        mSocket.disconnect();
        mSocket = null;
    }
}
