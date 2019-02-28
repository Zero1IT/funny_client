package com.example.funnynose.authentification;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.funnynose.Constants;
import com.example.funnynose.R;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ThirdRegistrationFragment extends Fragment {

    private Spinner mChooseCitySpinner;
    private TextView mBirthDate;
    private TextView mFirstParticipationDate;

    private Calendar dateAndTime = Calendar.getInstance();
    private Date birthdayDate;
    private Date firstParticipationDate;

    private String city;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.third_registration_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mChooseCitySpinner = view.findViewById(R.id.choose_city);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, Constants.cities);
        mChooseCitySpinner.setAdapter(spinnerArrayAdapter);

        city = Constants.cities[0];

        mChooseCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                city = Constants.cities[selectedItemPosition];
                Log.d("DEBUG", "" + city);
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        mBirthDate = view.findViewById(R.id.birth_date);
        mFirstParticipationDate = view.findViewById(R.id.first_participation_date);


        Button mBtnBirthDate = view.findViewById(R.id.btn_change_birth_date);
        mBtnBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBirthDate(v);
            }
        });

        Button mBtnFirstParticipationDate = view.findViewById(R.id.btn_change_first_participation);
        mBtnFirstParticipationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFirstParticipationDate(v);
            }
        });

        Button mCompleteButton = view.findViewById(R.id.complete_button);
        mCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeRegistration();
            }
        });

        setInitialBirthDate();
        setInitialFirstParticipationDate();
    }

    private void completeRegistration() {
        ((RegistrationActivity) getActivity()).putThirdFragmentData(city, birthdayDate, firstParticipationDate);
        ((RegistrationActivity) getActivity()).nextFragment();
    }

    public void setBirthDate(View v) {
        new DatePickerDialog(getContext(), birthDateListener,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public void setFirstParticipationDate(View v) {
        new DatePickerDialog(getContext(), firstParticipationDateListener,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void setInitialBirthDate() {
        birthdayDate = dateAndTime.getTime();
        mBirthDate.setText(DateUtils.formatDateTime(getContext(),
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    private void setInitialFirstParticipationDate() {
        firstParticipationDate = dateAndTime.getTime();
        mFirstParticipationDate.setText(DateUtils.formatDateTime(getContext(),
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    DatePickerDialog.OnDateSetListener birthDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialBirthDate();
        }
    };

    DatePickerDialog.OnDateSetListener firstParticipationDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialFirstParticipationDate();
        }
    };


}
