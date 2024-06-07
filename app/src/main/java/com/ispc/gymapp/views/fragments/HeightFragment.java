package com.ispc.gymapp.views.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.android.material.textfield.TextInputEditText;
import com.ispc.gymapp.R;
import com.ispc.gymapp.views.viewmodel.RegisterViewModel;


public class HeightFragment extends Fragment {



    private EditText heightEditText;
    private RegisterViewModel viewModel;
    private static final int ALTURA_MINIMA = 50; // Altura mínima permitida en centímetros
    private static final int ALTURA_MAXIMA = 250;
    public HeightFragment() {
        // Required empty public constructor
    }



    public static HeightFragment newInstance() {
        return new HeightFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_height, container, false);

        heightEditText = rootView.findViewById(R.id.heightEditText);

        viewModel  = new ViewModelProvider(requireActivity()).get(RegisterViewModel.class);
        heightEditText.requestFocus();

        heightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No-op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No-op
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int height = Integer.parseInt(s.toString());

                    // Verificar que la altura esté dentro del rango permitido
                    if (height < ALTURA_MINIMA || height > ALTURA_MAXIMA) {
                        // Mostrar mensaje de error
                        heightEditText.setError("La altura debe estar entre " + ALTURA_MINIMA + " y " + ALTURA_MAXIMA + " cm");
                    } else {
                        // Actualizar ViewModel si la altura es válida
                        viewModel.setInputData("height", height);
                    }
                } catch (NumberFormatException e) {
                    // Mostrar mensaje de error si no se puede convertir a un número válido
                    heightEditText.setError("Ingrese un número válido");
                }
            }
        });


        return rootView;
    }



}