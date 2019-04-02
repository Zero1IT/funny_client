package com.example.funnynose.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.funnynose.constants.Session;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketAPI {

    private final static String URL = "http://app.funnynose.by";
    //private final static String URL = "http://192.168.0.105:3000";
    public static ArrayList<String> sCities = new ArrayList<String>() {
        {
            add("Гомель"); add("Минск"); add("Могилёв");
            add("Брест"); add("Витебск"); add("Гродно");
        }
    };

    public static String[] sChatNames = new String[]{"common", "gomel", "minsk",
            "mogilev", "brest", "vitebsk", "grodno"};

    private static Socket sSocket;

    private SocketAPI() {}

    public static Socket getSocket() {
        if (sSocket == null) {
            try {
                sSocket = IO.socket(URL);
                sSocket.connect();
            } catch (URISyntaxException e) {
                Log.d(Session.TAG, e.getMessage());
            }
        } else if (!sSocket.connected()) {
            sSocket.connect();
        }
        return sSocket;
    }

    public static void closeSocket() {
        if (sSocket == null) {
            return;
        }
        sSocket.disconnect();
        sSocket = null;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean isOnline() {
        if (sSocket != null) {
            return sSocket.connected();
        }
        return false;
    }
}