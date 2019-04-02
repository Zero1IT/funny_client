package com.example.funnynose.events.Support;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.example.funnynose.R;
import com.example.funnynose.constants.Session;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class TimePickerDialog extends DialogFragment {

    private static final String TIME_KEY = "key_time";
    private static final String DATE_TIME_KEY = "dt_key";

    private Calendar mDefaultDate;
    private TimePicker mTimePicker;

    public static TimePickerDialog newInstance(Calendar date) {

        Bundle args = new Bundle();
        args.putSerializable(TIME_KEY, date);
        TimePickerDialog fragment = new TimePickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = v.findViewById(R.id.time_picker_dialog);
        mTimePicker.setIs24HourView(true);
        return new AlertDialog.Builder(getActivity()).setView(v)
                .setTitle(R.string.select_time_dialog_name)
                .setPositiveButton(android.R.string.ok, positiveClick)
                .setNegativeButton(android.R.string.cancel, negativeClick)
                .create();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mDefaultDate = (Calendar) args.getSerializable(TIME_KEY);
        } else {
            mDefaultDate = Calendar.getInstance();
        }
    }

    private final DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            int h = mTimePicker.getCurrentHour();
            int m = mTimePicker.getCurrentMinute();

            Calendar calendar = Calendar.getInstance();
            calendar.set(mDefaultDate.get(Calendar.YEAR), mDefaultDate.get(Calendar.MONTH),
                    mDefaultDate.get(Calendar.DAY_OF_MONTH), h, m);
            if (getActivity() instanceof onGettingFragmentResult) {
                Intent intent = new Intent();
                intent.putExtra(DATE_TIME_KEY, calendar.getTime());
                ((onGettingFragmentResult)getActivity()).fragmentResult(intent);
            }
        }
    };

    private final DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            onDestroy();
        }
    };
}
