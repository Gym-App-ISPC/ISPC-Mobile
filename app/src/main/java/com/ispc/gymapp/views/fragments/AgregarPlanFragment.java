package com.ispc.gymapp.views.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ispc.gymapp.R;
import com.ispc.gymapp.model.Plan;

import java.io.IOException;
import java.util.UUID;

public class AgregarPlanFragment extends AppCompatDialogFragment {

    private EditText editTextNombre, editTextDescripcion, editTextPrecio, editTextDetalles;
    private Button buttonSeleccionarImagen;
    private FirebaseFirestore db;
    private Uri imageUri;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageViewSeleccionada;
    private AgregarPlanDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_agregar_plan, null);

        editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextDescripcion = view.findViewById(R.id.editTextDescripcion);
        editTextDetalles = view.findViewById(R.id.editTextDetalles);
        editTextPrecio = view.findViewById(R.id.editTextPrecio);
        buttonSeleccionarImagen = view.findViewById(R.id.buttonSeleccionarImagen);
        imageViewSeleccionada = view.findViewById(R.id.imageViewSeleccionada);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("images");

        buttonSeleccionarImagen.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
        });

        builder.setView(view)
                .setTitle("Agregar Plan")
                .setNegativeButton("Cancelar", (dialog, which) -> {})
                .setPositiveButton("Guardar", (dialog, which) -> {
                    if (imageUri != null) {
                        uploadImageAndSavePlan();
                    } else {
                        agregarPlan(null, UUID.randomUUID().toString());
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AgregarPlanDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AgregarPlanDialogListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                imageViewSeleccionada.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageAndSavePlan() {
        String fileName = UUID.randomUUID().toString();
        StorageReference fileReference = storageReference.child(fileName);
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    agregarPlan(uri.toString(), fileName);
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                });
    }

    private void agregarPlan(String imageUrl, String planId) {
        String nombre = editTextNombre.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        String detalles = editTextDetalles.getText().toString().trim();
        String precioStr = editTextPrecio.getText().toString().trim();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(descripcion) ||TextUtils.isEmpty(detalles) || TextUtils.isEmpty(precioStr)) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Por favor, ingresa un precio válido", Toast.LENGTH_SHORT).show();
            return;
        }
        Plan plan = new Plan(nombre, descripcion,detalles, precio, imageUrl);

        db.collection("planes").add(plan)
                .addOnSuccessListener(documentReference -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "Plan agregado exitosamente", Toast.LENGTH_SHORT).show();
                            listener.onAgregarPlan(nombre, descripcion, detalles, precio, imageUrl);
                            dismiss();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "Error al agregar el plan", Toast.LENGTH_SHORT).show();
                            dismiss();
                        });
                    }
                });
        listener.onAgregarPlan(nombre, descripcion,detalles, precio, imageUrl);
        Log.d("AgregarPlanFragment", "Agregando plan en hilo: " + Thread.currentThread().getName());
    }

    public interface AgregarPlanDialogListener {
        void onAgregarPlan(String nombre, String descripcion,String detalles, double precio, String imageUrl);
    }
}
