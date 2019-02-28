package com.example.funnynose;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.funnynose.authentification.AuthentificationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.socket.emitter.Emitter;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Session.context = getApplicationContext();

        if (Session.isOnline()) {
            SocketAPI.initSocket();

            Log.d("DEBUG", "1");

            JSONObject obj = new JSONObject();
            try {
                obj.put("phone", "ada11ad");
                obj.put("password", "lol0");
            } catch (JSONException e) {
                Log.d("DEBUG", "" + e.getMessage());
            }
            SocketAPI.currentSocket().emit("authentication", obj)
                    .once("authentication", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if ((boolean) args[0]) {
                        Log.d("DEBUG", "2");
                        Intent intent = new Intent(Session.context, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d("DEBUG", "3");
                        Intent intent = new Intent(Session.context, AuthentificationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        } else {
            // проверка данных пользователя
            Intent intent = new Intent(Session.context, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
