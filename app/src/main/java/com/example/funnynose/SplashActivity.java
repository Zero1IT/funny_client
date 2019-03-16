package com.example.funnynose;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.funnynose.authentication.AuthenticationActivity;
import com.example.funnynose.db.Database;
import com.example.funnynose.network.AsyncServerResponse;
import com.example.funnynose.network.SocketAPI;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.socket.emitter.Emitter;

public class SplashActivity extends AppCompatActivity {

    private AsyncServerResponse mAsyncServerResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!User.tryConnectUser(getApplicationContext())) {
            Utilities.showSnackbar(getCurrentFocus(), "Error");
        }

        initAsyncServerResponse();
        Database.initDatabase(getApplicationContext());

        if (User.getUserAppData(getApplicationContext())) {
            if (SocketAPI.isOnline(getApplicationContext())) {
                sendAuthorizationDataToServer();
                mAsyncServerResponse.start(getApplicationContext());
            } else {
                openMainActivity();
            }
        } else {
            openAuthenticationActivity();
        }
    }

    private void initAsyncServerResponse() {
        mAsyncServerResponse = new AsyncServerResponse(2500, new AsyncServerResponse.AsyncTask() {
            @Override
            public void call() { openMainActivity(); }
        });
        mAsyncServerResponse.setFailResponse(new AsyncServerResponse.AsyncTask() {
            @Override
            public void call() { openMainActivity(); }
        });
        mAsyncServerResponse.setFailSuccessful(new AsyncServerResponse.AsyncTask() {
            @Override
            public void call() { openAuthenticationActivity(); }
        });
    }

    private void openMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void openAuthenticationActivity() {
        Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendAuthorizationDataToServer() {
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
                    JSONObject jsonResponse = (JSONObject) args[0];
                    boolean successfulAuthentication = (boolean) jsonResponse.remove("auth");
                    if (successfulAuthentication && jsonResponse.length() > 0) {
                        User.userDataFromJson(jsonResponse);
                        User.setUserAppData(getApplicationContext());
                    }
                    mAsyncServerResponse.setSuccessful(successfulAuthentication);
                    mAsyncServerResponse.setResponse(true);
                }
            });
    }
}
