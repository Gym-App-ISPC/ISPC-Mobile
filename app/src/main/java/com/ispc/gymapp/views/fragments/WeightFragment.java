package com.ispc.gymapp.views.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.ispc.gymapp.R;
import com.ispc.gymapp.views.viewmodel.RegisterViewModel;

import java.lang.reflect.Array;
import java.util.Arrays;


public class WeightFragment extends Fragment {


    private RegisterViewModel viewModel;

    private EditText kgEditText, grEditText;

    public WeightFragment() {
        // Required empty public constructor
    }

    public static WeightFragment newInstance() {
        return new WeightFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weight, container, false);
        viewModel  = new ViewModelProvider(requireActivity()).get(RegisterViewModel.class);

        kgEditText = rootView.findViewById(R.id.kgEditText);
        grEditText = rootView.findViewById(R.id.grEditText);

        kgEditText.requestFocus();

        kgEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateCombinedValue();
            }
        });

        grEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateCombinedValue();
            }
        });

        return rootView;
    }
    private void updateCombinedValue() {
        String kgText = kgEditText.getText().toString();
        String grText = grEditText.getText().toString();

        double kgValue = kgText.isEmpty() ? 0 : Double.parseDouble(kgText);
        double grValue = grText.isEmpty() ? 0 : Double.parseDouble(grText);

        double combinedValue = kgValue + (grValue / 1000.0);
        viewModel.setInputData("weight", combinedValue);
    }
}