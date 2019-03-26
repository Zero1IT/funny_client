package com.example.funnynose;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utilities {

    private static final Locale LOCALE = new Locale("ru", "RU");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy", LOCALE);
    public static final SimpleDateFormat HOURS_MINUTES = new SimpleDateFormat("HH:mm", LOCALE);

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
