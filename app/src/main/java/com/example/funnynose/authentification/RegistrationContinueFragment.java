package com.example.funnynose.authentification;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.funnynose.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RegistrationContinueFragment extends Fragment {

    private static final String ARGUMENTS = "args";

    private TextView mName;
    private TextView mSurname;
    private TextView mEmail;
    private TextView mCity;
    private Button mButtonNext;
    private Button mButtonBack;

    public static Fragment newInstance(String ... array) {
        Fragment fragment = new RegistrationContinueFragment();
        Bundle args = new Bundle();
        args.putStringArray(ARGUMENTS, array);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mName = view.findViewById(R.id.user_name);
        mSurname = view.findViewById(R.id.user_surname);
        mEmail = view.findViewById(R.id.user_email);
        mCity = view.findViewById(R.id.user_city);
        mButtonNext = view.findViewById(R.id.continue_btn);
        mButtonBack = view.findViewById(R.id.back_button);
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegistrationActivity parent = (RegistrationActivity) getActivity();
                if (parent != null) {
                    parent.goBack();
                }
            }
        });
    }
}
