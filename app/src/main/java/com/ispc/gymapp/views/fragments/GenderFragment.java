package com.ispc.gymapp.views.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ispc.gymapp.R;
import com.ispc.gymapp.views.viewmodel.RegisterViewModel;

import java.util.HashMap;


public class GenderFragment extends Fragment {


    private RadioButton rbMen;
    private RadioButton rbWomen;
    private RegisterViewModel viewModel;

    public GenderFragment() {
        // Required empty public constructor
    }


    public static GenderFragment newInstance() {
        return new GenderFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_genre, container, false);
        RadioGroup imageRadioGroup = rootView.findViewById(R.id.radioGroup);
        rbMen = rootView.findViewById(R.id.rbMen);
        rbWomen = rootView.findViewById(R.id.rbWomen);
        viewModel  = new ViewModelProvider(requireActivity()).get(RegisterViewModel.class);
        imageRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                       @Override
                                                       public void onCheckedChanged(RadioGroup group, int checkedId) {
                                                           if (checkedId == R.id.rbMen) {
                                                               viewModel.setInputData("gender","M");
                                                           } else if (checkedId == R.id.rbWomen) {
                                                               viewModel.setInputData("gender","F");
                                                           }
                                                       }
        });

        return rootView;
    }
}