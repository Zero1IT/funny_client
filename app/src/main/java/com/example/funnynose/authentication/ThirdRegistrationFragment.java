package com.example.funnynose.authentication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.funnynose.R;
import com.example.funnynose.network.SocketAPI;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ThirdRegistrationFragment extends CommonRegistrationFragment {

    private TextView mBirthdayDateView;
    private TextView mFirstParticipationDateView;

    private Calendar mDateAndTime = Calendar.getInstance();
    private Date mBirthdayDate;
    private Date mFirstParticipationDate;

    private String mCity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.third_registration_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mParent = (RegistrationActivity) getActivity();
        mContext = getContext();
        Spinner mChooseCitySpinner = view.findViewById(R.id.choose_city);
        
        if (mContext != null) {
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<> (
                    mContext, android.R.layout.simple_spinner_dropdown_item, SocketAPI.sCities);
            mChooseCitySpinner.setAdapter(spinnerArrayAdapter);
        }

        mCity = SocketAPI.sCities.get(0);

        mChooseCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                mCity = SocketAPI.sCities.get(selectedItemPosition);
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        mBirthdayDateView = view.findViewById(R.id.birth_date);
        mFirstParticipationDateView = view.findViewById(R.id.first_participation_date);


        Button mBtnBirthDate = view.findViewById(R.id.btn_change_birth_date);
        mBtnBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBirthDate();
            }
        });

        Button mBtnFirstParticipationDate = view.findViewById(R.id.btn_change_first_participation);
        mBtnFirstParticipationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFirstParticipationDate();
            }
        });

        mContinueButton = view.findViewById(R.id.complete_button);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeRegistration();
            }
        });

        setInitialBirthDate();
        setInitialFirstParticipationDate();
    }

    private void completeRegistration() {
        if (mParent != null) {
            mParent.putThirdFragmentData(mCity, mBirthdayDate, mFirstParticipationDate);
            mParent.nextFragment();
        }
    }

    private void setBirthDate() {
        if (mContext != null) {
            new DatePickerDialog(mContext, birthDateListener,
                    mDateAndTime.get(Calendar.YEAR),
                    mDateAndTime.get(Calendar.MONTH),
                    mDateAndTime.get(Calendar.DAY_OF_MONTH))
                    .show();
        }
    }

    private void setFirstParticipationDate() {
        if (mContext != null) {
            new DatePickerDialog(mContext, firstParticipationDateListener,
                    mDateAndTime.get(Calendar.YEAR),
                    mDateAndTime.get(Calendar.MONTH),
                    mDateAndTime.get(Calendar.DAY_OF_MONTH))
                    .show();
        }
    }

    private void setInitialBirthDate() {
        mBirthdayDate = mDateAndTime.getTime();
        mBirthdayDateView.setText(DateUtils.formatDateTime(mContext,
                mDateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    private void setInitialFirstParticipationDate() {
        mFirstParticipationDate = mDateAndTime.getTime();
        mFirstParticipationDateView.setText(DateUtils.formatDateTime(mContext,
                mDateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    private DatePickerDialog.OnDateSetListener birthDateListener =
            new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            mDateAndTime.set(Calendar.YEAR, year);
            mDateAndTime.set(Calendar.MONTH, month);
            mDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialBirthDate();
        }
    };

    private DatePickerDialog.OnDateSetListener firstParticipationDateListener =
            new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            mDateAndTime.set(Calendar.YEAR, year);
            mDateAndTime.set(Calendar.MONTH, month);
            mDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialFirstParticipationDate();
        }
    };
}
