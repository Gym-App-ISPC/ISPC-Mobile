package com.ispc.gymapp.views.fragments;

import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ispc.gymapp.R;
import com.ispc.gymapp.model.User;
import com.ispc.gymapp.views.activities.LoginActivity;

import java.util.Locale;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private User user;
    private EditText pesoEditText;
    private EditText alturaEditText;
    private TextView imcTextView, logout, deleteAccount;
    private FirebaseAuth mAuth;
    private ImageView perfilImageView;
    private FirebaseUser currentUser;

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
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            user = new User();
            user.setName(currentUser.getDisplayName());
            user.setMail(currentUser.getEmail());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Context context = requireContext();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        pesoEditText = view.findViewById(R.id.peso);
        alturaEditText = view.findViewById(R.id.altura);
        imcTextView = view.findViewById(R.id.textImc);
        logout = view.findViewById(R.id.textView21);
        deleteAccount = view.findViewById(R.id.deleteAccount);
        perfilImageView = view.findViewById(R.id.perfil);

        loadProfileImage();

        TextView nameText = view.findViewById(R.id.nameText);
        if (user != null) {
            nameText.setText(user.getName());
        }

        TextView emailText = view.findViewById(R.id.email);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            emailText.setText(currentUser.getEmail());
        }

        Button btnActualizar = view.findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(v -> updateUserData());

        pesoEditText.addTextChangedListener(textWatcher);
        alturaEditText.addTextChangedListener(textWatcher);

        logout.setOnClickListener(view1 -> logoutOperation());
        deleteAccount.setOnClickListener(view1 -> deleteAccountOperation());

        perfilImageView.setOnClickListener(v -> showUrlInputDialog());

        if (currentUser != null) {
            showUserData(currentUser);
        }

        return view;
    }
    private void showUserData(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtener los datos del usuario
                        Double peso = documentSnapshot.getDouble("weight");
                        Integer altura = documentSnapshot.getLong("height").intValue();

                        // Mostrar los datos en los EditTexts
                        if (peso != null && altura != null) {
                            pesoEditText.setText(String.valueOf(peso));
                            alturaEditText.setText(String.valueOf(altura));
                        }

                        // Calcular y mostrar el IMC
                        calculateAndDisplayIMC(peso, altura);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error getting user document", e));
    }

    private void loadProfileImage() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String imageUrl = documentSnapshot.getString("photoUrl");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.perfil)
                                        .error(R.drawable.perfil)
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(perfilImageView);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error getting user document", e));
        }
    }

    private void showUrlInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Paste Image URL");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String imageUrl = input.getText().toString();
            saveImageUrlToFirestore(imageUrl);
            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.perfil)
                    .error(R.drawable.perfil)
                    .apply(RequestOptions.circleCropTransform())
                    .into(perfilImageView);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    private void saveImageUrlToFirestore(String imageUrl) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(currentUser.getUid())
                    .update("photoUrl", imageUrl)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Image URL saved successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error saving image URL to Firestore", e);
                        Toast.makeText(getContext(), "Failed to save image URL", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
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
                        .addOnSuccessListener(aVoid -> {
                            Log.d("ProfileFragment", "User data updated successfully in Firestore");
                            Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
                        })
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


    private void calculateAndDisplayIMC(Double weight, Integer height) {
        // Calcular el IMC si el peso y la altura son válidos
        if (weight != null && height != null && weight > 0 && height > 0) {
            double heightInMeters = height / 100.0;
            double imc = weight / (heightInMeters * heightInMeters);

            // Mostrar el IMC en el TextView correspondiente
            imcTextView.setText(String.format(Locale.getDefault(), "%.2f", imc));
        }
    }
}

