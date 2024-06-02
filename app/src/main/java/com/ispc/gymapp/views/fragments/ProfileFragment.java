package com.ispc.gymapp.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;
import com.google.android.gms.tasks.OnFailureListener;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import android.net.Uri;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import android.provider.MediaStore;
import android.app.Activity;
import android.text.InputType;
import android.util.Log; // Agregado para importar Log
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
import com.google.firebase.auth.UserProfileChangeRequest;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import com.bumptech.glide.request.RequestOptions;

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
import com.ispc.gymapp.util.Constants; // Importar Constants desde el paquete util

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private User user;
    private EditText pesoEditText;
    private EditText alturaEditText;
    private TextView imcTextView, logout, deleteAccount;
    private FirebaseAuth mAuth;
    private AppCompatImageView perfilImageView; // Declarar perfilImageView como una variable de clase

    private static final int REQUEST_SELECT_IMAGE = 1;

    // Reemplazar "ProfileFragment" con un identificador único para tu clase
    private static final String LOG_TAG = "ProfileFragment";

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.user = user;
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener el usuario actual de FirebaseAuth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Crear un objeto User a partir del usuario actual si existe
        if (currentUser != null) {
            user = new User();
            user.setName(currentUser.getDisplayName()); // Obtener el nombre del usuario
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

        // Obtener la URL de la imagen almacenada en Firestore
        FirebaseUser currentUserFirebase = mAuth.getCurrentUser();
        if (currentUserFirebase != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(currentUserFirebase.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String imageUrl = documentSnapshot.getString("photoUrl");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                // Cargar la imagen desde la URL usando Glide
                                Glide.with(this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.perfil) // Imagen de placeholder mientras se carga la imagen
                                        .error(R.drawable.perfil) // Imagen de error si falla la carga
                                        .into(perfilImageView);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error getting user document", e));
        }

        // Obtener el nombre del usuario actual y establecerlo en nameText
        TextView nameText = view.findViewById(R.id.nameText);
        if (user != null) {
            nameText.setText(user.getName()); // Suponiendo que tienes un método getName() en la clase User
        }

        // Obtener el correo electrónico del usuario actual y establecerlo en email
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

        // Cargar la imagen de perfil desde la URL usando Glide
        perfilImageView = view.findViewById(R.id.perfil);
        if (currentUser != null && currentUser.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(currentUser.getPhotoUrl())
                    .placeholder(R.drawable.perfil) // Imagen de placeholder mientras se carga la imagen
                    .error(R.drawable.perfil) // Imagen de error si falla la carga
                    .into(perfilImageView);
        }

        // Configurar el OnClickListener para seleccionar una nueva imagen de perfil
        perfilImageView.setOnClickListener(v -> {
            // Aquí puedes abrir un cuadro de diálogo para que el usuario pegue una URL
            // y luego cargar la imagen desde esa URL
            showUrlInputDialog();
        });

        return view;
    }

    // Método para mostrar un cuadro de diálogo para ingresar la URL de la imagen
    // Método para mostrar un cuadro de diálogo para ingresar la URL de la imagen
    private void showUrlInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Paste Image URL");

        // Agregar un EditText para que el usuario pegue la URL
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT); // Usa la variable InputType
        builder.setView(input);

        // Agregar los botones "Guardar" y "Cancelar"
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String imageUrl = input.getText().toString();
                // Guardar la URL en Firestore
                saveImageUrlToFirestore(imageUrl);
                // Cargar automáticamente la imagen después de guardarla
                Glide.with(ProfileFragment.this)
                        .load(imageUrl)
                        .placeholder(R.drawable.perfil)
                        .error(R.drawable.perfil)
                        .apply(RequestOptions.circleCropTransform()) // Recortar la imagen de forma circular
                        .into(perfilImageView);
            }
        });

        // Botón Cancelar
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Método para guardar la URL de la imagen en Firestore
    private void saveImageUrlToFirestore(String imageUrl) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(currentUser.getUid())
                    .update("photoUrl", imageUrl)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Image URL saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error saving image URL to Firestore", e);
                            Toast.makeText(getContext(), "Failed to save image URL", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }




    // Método para manejar el resultado de la selección de la imagen de perfil
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            updateProfilePhoto(selectedImageUri);
        }
    }

    // Método para actualizar la foto de perfil del usuario en Firebase
    private void updateProfilePhoto(Uri photoUri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(photoUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                        // Aquí puedes manejar lo que sucede después de que se actualiza la foto de perfil
                    } else {
                        Log.e(TAG, "Failed to update user profile.", task.getException());
                        // Aquí puedes manejar el caso en el que falla la actualización de la foto de perfil
                    }
                });
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
