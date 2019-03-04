package com.example.funnynose.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.funnynose.constants.Session;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketAPI {

    //private final static String URL = "http://app.funnynose.by";
    private final static String URL = "http://192.168.0.105:3000";

    public static String[] cities = new String[]{"Гомель", "Минск", "Могилёв",
            "Брест", "Витебск", "Гродно"};

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
        } else if (!mSocket.connected()) {
            mSocket.connect();
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

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
