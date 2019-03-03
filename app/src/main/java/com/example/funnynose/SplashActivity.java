package com.example.funnynose;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.example.funnynose.authentication.AuthenticationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.socket.emitter.Emitter;

public class SplashActivity extends AppCompatActivity {

    private boolean successfulAuthentication;
    private boolean responseAuthentication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (User.getUserAppData(getApplicationContext())) {
            if (isOnline(getApplicationContext())) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("phone", User.stringData.get("phone"));
                    obj.put("password", User.stringData.get("password"));
                } catch (JSONException e) {
                    Log.d("DEBUG", e.getMessage());
                }
                SocketAPI.getSocket().emit("authentication_with_update", obj)
                        .once("authentication_with_update", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                try{
                                    JSONObject jsonResponse = (JSONObject) args[0];
                                    successfulAuthentication = (boolean) jsonResponse.get("auth");
                                    jsonResponse.remove("auth");
                                    if (successfulAuthentication && jsonResponse.length() > 0) {
                                        User.userDataFromJson(jsonResponse);
                                        User.setUserAppData(getApplicationContext());
                                    }
                                } catch (JSONException e) {
                                    Log.d("DEBUG", e.getMessage());
                                }
                                responseAuthentication = true;
                            }
                        });
                signInThread();
            } else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void signInThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long end = System.currentTimeMillis() + 2500;
                while (!responseAuthentication && System.currentTimeMillis() < end) {
                    try{
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Log.d("DEBUG", e.getMessage());
                    }
                }

                if (successfulAuthentication) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (!responseAuthentication) {
                    Log.d("DEBUG", "Ошибка соединения в SplashActivity");
                    Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).start();
    }

}
