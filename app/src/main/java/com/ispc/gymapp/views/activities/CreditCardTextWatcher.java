package com.ispc.gymapp.views.activities;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CreditCardTextWatcher implements TextWatcher {

    private EditText editText;
    private static final int MAX_CARD_NUMBER_LENGTH = 16;

    public CreditCardTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // No se necesita implementar
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // No se necesita implementar
    }

    @Override
    public void afterTextChanged(Editable s) {
        String originalText = s.toString();
        StringBuilder formattedText = new StringBuilder();


        String cleanText = originalText.replaceAll("\\s", "");

        if (cleanText.length() > MAX_CARD_NUMBER_LENGTH) {

            cleanText = cleanText.substring(0, MAX_CARD_NUMBER_LENGTH);
        }
        for (int i = 0; i < cleanText.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formattedText.append(" ");
            }
            formattedText.append(cleanText.charAt(i));
        }

        // Actualizar el texto del EditText con el texto formateado
        editText.removeTextChangedListener(this); // Evitar un bucle infinito al cambiar el texto programáticamente
        editText.setText(formattedText.toString());
        editText.setSelection(formattedText.length()); // Colocar el cursor al final del texto
        editText.addTextChangedListener(this); // Volver a agregar el TextWatcher
    }
}
