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

public class GoalFragment extends Fragment {


    private RegisterViewModel viewModel;
    private EditText kgEditText, grEditText;


    public GoalFragment() {
    }


    public static GoalFragment newInstance() {
        return new GoalFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_goal_fragment, container, false);
        viewModel  = new ViewModelProvider(requireActivity()).get(RegisterViewModel.class);

        kgEditText = rootView.findViewById(R.id.kgEditText);
        grEditText = rootView.findViewById(R.id.grEditText);

        kgEditText.requestFocus();
        kgEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Handle changes in kilogram EditText
                updateCombinedValue();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        grEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Handle changes in gram EditText
                updateCombinedValue();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return rootView;
    }

    private void updateCombinedValue() {
        // Parse input from EditText fields
        double kgValue = 0;
        try {
            kgValue = Double.parseDouble(kgEditText.getText().toString());
        } catch (NumberFormatException e) {
            // Handle parsing error (optional)
        }

        int grValue = 0;
        try {
            grValue = Integer.parseInt(grEditText.getText().toString());
        } catch (NumberFormatException e) {
            // Handle parsing error (optional)
        }

        // Calculate combined value
        double combinedValue = kgValue + (grValue / 1000.0);

        // Update ViewModel
        viewModel.setInputData("goalWeight", combinedValue);
    }
}