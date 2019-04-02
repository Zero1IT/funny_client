package by.funnynose.app.events.Support;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.example.funnynose.R;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DatePickerDialog extends DialogFragment {

    private static final String DATE_KEY = "key_date";
    private DatePicker mDatePicker;
    private Date mDefaultDate;
    private int mYear;
    private int mMonth;
    private int mDay;

    public static DatePickerDialog newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(DATE_KEY, date);
        DatePickerDialog fragment = new DatePickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDefaultDate);

        mDatePicker = v.findViewById(R.id.date_picker_dialog);

        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        mDatePicker.init(y, m, d, null);

        return new AlertDialog.Builder(getActivity()).setView(v)
                .setTitle(R.string.select_date_dialog_name)
                .setPositiveButton(android.R.string.ok, positiveClick)
                .setNegativeButton(android.R.string.cancel, negativeClick)
                .create();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mDefaultDate = (Date) args.getSerializable(DATE_KEY);
        } else {
            mDefaultDate = new Date();
        }
    }

    private final DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mYear = mDatePicker.getYear();
            mMonth = mDatePicker.getMonth();
            mDay = mDatePicker.getDayOfMonth();
            Calendar calendar = Calendar.getInstance();
            calendar.set(mYear, mMonth, mDay);
            TimePickerDialog dialogTime = TimePickerDialog.newInstance(calendar);
            if (getFragmentManager() != null)
                dialogTime.show(getFragmentManager(), "ANY"); // TODO:
            else throw new ExceptionInInitializerError("Null pointer fragment manager, time picker show");
        }
    };

    private final DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            onDestroy();
        }
    };
}
