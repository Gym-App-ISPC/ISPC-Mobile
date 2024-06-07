package com.ispc.gymapp.views.activities;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CustomTextWatcher implements TextWatcher {
    private TextInputEditText editText;
    private TextInputLayout textInputLayout;
    private ValidationType validationType;


    public enum ValidationType {
        CARD_NUMBER, EXPIRY_MONTH, EXPIRY_YEAR, CVV
    }
    public CustomTextWatcher(TextInputEditText editText, TextInputLayout textInputLayout, ValidationType validationType) {
        this.editText = editText;
        this.textInputLayout = textInputLayout;
        this.validationType = validationType;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        switch (validationType) {
            case EXPIRY_MONTH:
                if (charSequence.length() > 0) {
                    int month = Integer.parseInt(charSequence.toString());
                    if (month < 1 || month > 12) {
                        textInputLayout.setError("Mes de expiración inválido.");
                    } else {
                        textInputLayout.setError(null);
                    }
                }
                break;
            case EXPIRY_YEAR:
                if (charSequence.length() > 0) {
                    int year = Integer.parseInt(charSequence.toString());
                    if (year < 25) {
                        textInputLayout.setError("Año de expiración inválido.");
                    } else {
                        textInputLayout.setError(null);
                    }
                }
                break;
            case CVV:
                if (charSequence.length() != 3) {
                    textInputLayout.setError("CVV inválido.");
                } else {
                    textInputLayout.setError(null);
                }
                break;
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {}
}