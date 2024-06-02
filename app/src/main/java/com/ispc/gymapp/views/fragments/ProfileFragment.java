package com.ispc.gymapp.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;
import com.google.android.gms.tasks.OnFailureListener;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;
import java.util.Locale;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser; // Importante: Agregué esta línea
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ispc.gymapp.R;
import com.ispc.gymapp.model.User;
import com.ispc.gymapp.views.activities.ActivityFavoritos;
import com.ispc.gymapp.views.activities.LoginActivity;
import com.ispc.gymapp.views.activities.SplashActivity;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private User user;
    private EditText pesoEditText;
    private EditText alturaEditText;
    private TextView imcTextView, logout, deleteAccount;

    private FirebaseAuth mAuth;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.user = user;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener el usuario actual de FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Crear un objeto User a partir del usuario actual si existe
        if (currentUser != null) {
            user = new User();
            user.setMail(currentUser.getEmail());
            // Puedes establecer otros campos de usuario según sea necesario
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        pesoEditText = view.findViewById(R.id.peso);
        alturaEditText = view.findViewById(R.id.altura);
        imcTextView = view.findViewById(R.id.textImc);
        logout = view.findViewById(R.id.textView21);
        deleteAccount = view.findViewById(R.id.deleteAccount);
        mAuth = FirebaseAuth.getInstance();

        Button btnActualizar = view.findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(v -> updateUserData());

        pesoEditText.addTextChangedListener(textWatcher);
        alturaEditText.addTextChangedListener(textWatcher);

        logout.setOnClickListener(view1 -> logoutOperation());
        deleteAccount.setOnClickListener(view1 -> deleteAccountOperation());

        // Verificar si el usuario no es nulo y si las vistas de peso y altura no son nulas antes de establecer los valores y calcular IMC
        if (user != null && pesoEditText != null && alturaEditText != null) {
            if (user.getWeight() != null && user.getHeight() != null) { // Verificar si los valores de peso y altura son diferentes de null
                pesoEditText.setText(String.format(Locale.getDefault(), "%.2f", user.getWeight()));
                alturaEditText.setText(String.format(Locale.getDefault(), "%d", user.getHeight()));
                calculateAndDisplayIMC(user.getWeight(), user.getHeight());

            } else {
                // Manejar el caso en el que los valores de peso y altura sean null
                // Por ejemplo, puedes establecer valores predeterminados o mostrar un mensaje de error
                // Aquí puedes decidir qué hacer en este caso
            }
        }

        AppCompatImageView favoritos = view.findViewById(R.id.favoritos);
        favoritos.setOnClickListener(v -> startActivity(new Intent(getActivity(), ActivityFavoritos.class)));

        return view;
    }



    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            updateUserData();
        }
    };
    private void updateUserData() {
        if (user != null) {
            String pesoString = pesoEditText.getText().toString();
            String alturaString = alturaEditText.getText().toString();

            // Verificar si los campos están vacíos
            if (pesoString.isEmpty() || alturaString.isEmpty()) {
                return;
            }

            try {
                double weight = Double.parseDouble(pesoString);
                int height = Integer.parseInt(alturaString);

                user.setWeight(weight);
                user.setHeight(height);
                calculateAndDisplayIMC(user.getWeight(), user.getHeight());

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(mAuth.getCurrentUser().getUid())
                        .update("weight", weight, "height", height)
                        .addOnSuccessListener(aVoid -> Log.d("ProfileFragment", "User data updated successfully in Firestore"))
                        .addOnFailureListener(e -> Log.e("ProfileFragment", "Error updating user data in Firestore", e));
            } catch (NumberFormatException e) {
                Log.e("ProfileFragment", "Invalid input for weight or height");
            }
        }
    }




    private void logoutOperation() {
        mAuth.signOut();
        startActivity(new Intent(getContext(), LoginActivity.class));
    }

    private void deleteAccountOperation() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("mail", user.getMail())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        documentSnapshot.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Account deleted", Toast.LENGTH_LONG).show();
                                    mAuth.signOut();
                                    startActivity(new Intent(getContext(), LoginActivity.class));
                                })
                                .addOnFailureListener(e -> Log.e("ProfileFragment", "Error deleting account", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("ProfileFragment", "Error querying user document", e));
    }

    private void calculateAndDisplayIMC(Double weight, int height) {
        if (weight <= 0 || height <= 0) {
            // Handling the case where weight or height are zero or negative
            return;
        }

        double imc = weight / (height * height) * 10000;

        String imcCategory;
        if (imc < 18.5) {
            imcCategory = "Underweight";
        } else if (imc >= 18.5 && imc <= 24.9) {
            imcCategory = "Normal";
        } else if (imc >= 25 && imc <= 29.9) {
            imcCategory = "Overweight";
        } else {
            imcCategory = "Obesity";
        }

        imcTextView.setText(String.format(Locale.getDefault(), "%.2f (%s)", imc, imcCategory));
    }



}
