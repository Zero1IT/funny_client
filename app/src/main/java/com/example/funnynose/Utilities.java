package com.example.funnynose;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class Utilities {

    public static void showSnackbar(View v, String str, boolean button) {
        if (v != null) {
            Snackbar snackbar = Snackbar.make(v,
                    str, Snackbar.LENGTH_SHORT);
            if (button) {
                snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { }
                });
            }
            snackbar.show();
        }
    }

    public static void showSnackbar(View v, String str) {
        showSnackbar(v, str, false);
    }

}
