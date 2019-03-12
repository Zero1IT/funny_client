package com.example.funnynose.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.funnynose.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EventHospitalFragment extends Fragment {

    public static EventHospitalFragment newInstance() {

        Bundle args = new Bundle();

        EventHospitalFragment fragment = new EventHospitalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.events_hospital, container, false);
    }
}
