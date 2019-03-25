package com.example.funnynose;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utilities {

    private static final Locale locale = new Locale("ru", "RU");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", locale);
    public static final SimpleDateFormat hoursMinutes = new SimpleDateFormat("HH:mm", locale);

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
