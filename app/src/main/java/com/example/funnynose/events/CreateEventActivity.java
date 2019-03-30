package com.example.funnynose.events;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.funnynose.R;

public class CreateEventActivity extends AppCompatActivity {

    public static final int IDENTITY = 228;
    public static final String NAME_KEY = "layout_name";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            setTitle(getIntent().getStringExtra(NAME_KEY));
        }
        setContentView(R.layout.activity_create_event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_OK); //TODO: for debug, delete later
    }
}
