package com.ispc.gymapp.views.activities;

import static android.content.ContentValues.TAG;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ispc.gymapp.R;
import com.ispc.gymapp.model.Plan;
import com.ispc.gymapp.presenters.login.LoginPresenter;
import com.ispc.gymapp.views.adapter.PlanAdapter;
import com.ispc.gymapp.views.fragments.AgregarPlanFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Ecommerce extends AppCompatActivity implements PlanAdapter.OnPlanItemClickListener,PlanAdapter.OnPlanButtonClickListener, LoginPresenter.RolUsuarioCallback, AgregarPlanFragment.AgregarPlanDialogListener{
    PlanAdapter planAdapter;
    ArrayList<Plan> plans;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage;
    LoginPresenter loginPresenter;

    private ImageButton cartButton;

    private TextView cartBadge;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecommerce2);
        storage = FirebaseStorage.getInstance();

        loginPresenter = new LoginPresenter(this, FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());

        loginPresenter.obtenerRolUsuario(this);

        addButton = findViewById(R.id.addButton);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);






        Carrito.getInstance().cargarCarritoDesdeFirestore(this::setUpRecyclerView);
        setUpRecyclerView();


        cartButton = findViewById(R.id.cartButton);
        cartBadge = findViewById(R.id.cart_badge);


        obtenerPlanes();

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Ecommerce.this, CarritoActivity.class);
                startActivity(intent);
            }
        });
        addButton.setOnClickListener(v -> mostrarAgregarPlanFragment());

        editButton.setOnClickListener(v -> mostrarEditarPlanFragment());
        deleteButton.setOnClickListener(v -> eliminarPlan());



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_Navigator);
        bottomNavigationView.setSelectedItemId(R.id.shopItem);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.shopItem) {
                    return true;
                }

                if (id == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                if (id == R.id.title_activity_exercise) {
                    startActivity(new Intent(getApplicationContext(), DietExerciseActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                if (id == R.id.accountItem) {
                    startActivity(new Intent(getApplicationContext(), MiPerfilActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;

            }


        });



    }

    @Override
    public void onRolUsuarioObtenido(String roleName) {
        if (roleName.equals("ADMIN")) {
            addButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFalloObtenerRolUsuario(Exception e) {

        Log.e(TAG, "Error al obtener el rol del usuario: ", e);
    }

    @Override
    public void onPlanItemClick(Plan plan) {
        mostrarDialogoPlanCompleto(plan); // Show plan details
    }

    @Override
    public void onPlanButtonClick(Plan plan) {
        // Handle button click
        actualizarBadge();
        mostrarDialogo(plan); // Assuming this method handles the button action
    }




    //PROCESO PARA AGREGAR UN PLAN

    @Override
    public void onAgregarPlan(String nombre, String descripcion, double precio, String imageUrl) {
        Log.d(TAG, "onAgregarPlan: Plan agregado con éxito");
        Plan nuevoPlan = new Plan(nombre, descripcion, precio, imageUrl);
        plans.add(nuevoPlan);
        planAdapter.notifyDataSetChanged();

        // Llamar a LoadDataAsyncTask para cargar datos actualizados
        new LoadDataAsyncTask(db, new LoadDataAsyncTask.OnDataLoadedListener() {
            @Override
            public void onDataLoaded(List<Plan> plans) {
                // Actualizar la lista de planes en la actividad
                Ecommerce.this.plans.clear();
                Ecommerce.this.plans.addAll(plans);
                planAdapter.notifyDataSetChanged();
            }
        }).execute();

        Toast.makeText(this, "Has agregado un nuevo plan", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            actualizarListaPlanes();
        }
    }

    private void actualizarListaPlanes() {
        // Actualiza la lista de planes sin bloquear la UI
        new Handler(Looper.getMainLooper()).post(() -> planAdapter.notifyDataSetChanged());
    }


    private void mostrarDialogo(Plan plan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Plan agregado");
        builder.setMessage("Haz agregado: " + plan.getNombre() + " al carrito.");
        builder.setPositiveButton("Ver carrito", (dialog, which) -> {
            Intent intent = new Intent(Ecommerce.this, CarritoActivity.class);
            startActivity(intent);
        });
        builder.setNegativeButton("Cerrar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void obtenerPlanes() {
        new LoadDataAsyncTask(db, new LoadDataAsyncTask.OnDataLoadedListener() {
            @Override
            public void onDataLoaded(List<Plan> plans) {
                Ecommerce.this.plans.clear();
                Ecommerce.this.plans.addAll(plans);
                planAdapter.notifyDataSetChanged();
            }
        }).execute();

    }
    private void actualizarBadge() {
        int planCount = Carrito.getInstance().getPlanes().size();
        if (planCount > 0) {
            cartBadge.setVisibility(View.VISIBLE);
            cartBadge.setText(String.valueOf(planCount));
        } else {
            cartBadge.setVisibility(View.GONE);
        }
    }

    private void setUpRecyclerView() {
        plans = new ArrayList<>();
        planAdapter = new PlanAdapter(this, plans, this, this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(planAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private void mostrarAgregarPlanFragment() {
        AgregarPlanFragment dialog = new AgregarPlanFragment();
        dialog.show(getSupportFragmentManager(), "AgregarPlanDialog");

    }
    //END PROCESO PARA AGREGAR UN PLAN



    //PROCESO PARA EDITAR UN PLAN

    private void mostrarEditarPlanFragment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Plan a Editar");

        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(planAdapter);

        builder.setView(recyclerView);

        // Manejar el clic en un plan para editarlo
        builder.setPositiveButton("Editar", (dialog, which) -> {
            int selectedPosition = planAdapter.getSelectedPosition();
            if (selectedPosition != RecyclerView.NO_POSITION) {
                Plan plan = plans.get(selectedPosition);
                // Aquí deberías mostrar un diálogo o fragmento de edición del plan seleccionado
                mostrarDialogoEdicionPlan(plan);
            } else {
                Toast.makeText(this, "Por favor, selecciona un plan para editar", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón de cancelar
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
    private ImageView selectedImageView;
    private void mostrarDialogoEdicionPlan(Plan plan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Plan");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_plan, null);
        builder.setView(view);

        EditText nombreEditText = view.findViewById(R.id.editTextNombre);
        EditText descripcionEditText = view.findViewById(R.id.editTextDescripcion);
        EditText precioEditText = view.findViewById(R.id.editTextPrecio);
        Button seleccionarImagenButton = view.findViewById(R.id.buttonSeleccionarImagenEditada);
        selectedImageView = view.findViewById(R.id.imageVieweditada); // Mantener referencia


        nombreEditText.setText(plan.getNombre());
        descripcionEditText.setText(plan.getDescripcion());
        precioEditText.setText(String.valueOf(plan.getPrecio()));
        Picasso.get().load(plan.getImagen()).into(selectedImageView);
        seleccionarImagenButton.setOnClickListener(v -> seleccionarImagen());


        builder.setPositiveButton("Guardar", (dialog, which) -> {

            String nuevoNombre = nombreEditText.getText().toString().trim();
            String nuevaDescripcion = descripcionEditText.getText().toString().trim();

            double nuevoPrecio = Double.parseDouble(precioEditText.getText().toString());
            plan.setNombre(nuevoNombre);
            plan.setDescripcion(nuevaDescripcion);
            plan.setPrecio(nuevoPrecio);

            if (selectedImageUri != null) {
                guardarImagenYActualizarPlan(plan, selectedImageUri);
            } else {
                actualizarPlan(plan);
            }
        });


        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();

    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            if (selectedImageView != null) {
                Picasso.get().load(selectedImageUri).into(selectedImageView);
            } else {
                Toast.makeText(this, "Error: Imagen no seleccionada correctamente", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void guardarImagenYActualizarPlan(Plan plan, Uri imageUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("planes/" + plan.getId() + ".jpg");
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    plan.setImagen(uri.toString());

                    actualizarPlanEnFirestore(plan);
                    actualizarPlan(plan);
                }))
                .addOnFailureListener(e -> Toast.makeText(Ecommerce.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show());
    }
    private void actualizarPlan(Plan plan) {
        db.collection("planes").document(plan.getId())
                .set(plan)
                .addOnSuccessListener(aVoid -> {
                    obtenerPlanes(); // Refrescar la lista de planes
                    Toast.makeText(Ecommerce.this, "Plan actualizado exitosamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(Ecommerce.this, "Error al actualizar el plan", Toast.LENGTH_SHORT).show());
    }
    private void actualizarPlanEnFirestore(Plan plan) {
        db.collection("planes").document(plan.getId())
                .set(plan)
                .addOnSuccessListener(aVoid -> {
                    // Refrescar la lista de planes
                    obtenerPlanes(); // Método para obtener los planes actualizados de Firestore
                    Toast.makeText(Ecommerce.this, "Plan actualizado exitosamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Manejar el error al actualizar el plan
                    Toast.makeText(Ecommerce.this, "Error al actualizar el plan", Toast.LENGTH_SHORT).show();
                });
    }

    //END PROCESO PARA EDITAR UN PLAN


    //PROCESO PARA ELIMINAR UN PLAN


    private void eliminarPlan() {
        mostrarDialogoSeleccionPlan();
    }

    private void mostrarDialogoSeleccionPlan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Plan a Eliminar");

        // Crear RecyclerView y asignar un adaptador
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(planAdapter);

        builder.setView(recyclerView);

        // Manejar el clic en un plan para eliminarlo
        builder.setPositiveButton("Eliminar", (dialog, which) -> {
            int selectedPosition = planAdapter.getSelectedPosition();
            if (selectedPosition != RecyclerView.NO_POSITION) {
                Plan plan = plans.get(selectedPosition);
                mostrarConfirmacionEliminarPlan(plan);
            } else {
                Toast.makeText(this, "Por favor, selecciona un plan para eliminar", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón de cancelar
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void mostrarConfirmacionEliminarPlan(Plan plan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Plan");
        builder.setMessage("¿Estás seguro de que deseas eliminar el plan \"" + plan.getNombre() + "\"?");
        builder.setPositiveButton("Eliminar", (dialog, which) -> {
            Log.d(TAG, "ID del plan a eliminar: " + plan.getId());
            eliminarPlanDeFirestore(plan.getId());
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
    private void eliminarPlanDeFirestore(String planId) {
        Log.d(TAG, "Intentando eliminar plan con ID: " + planId);

        // Verificar que el ID del plan no sea nulo o vacío
        if (planId == null || planId.isEmpty()) {
            Log.e(TAG, "El ID del plan es nulo o vacío. No se puede eliminar el plan.");
            Toast.makeText(this, "Error: El ID del plan es inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("planes").document(planId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "Documento encontrado. Procediendo a eliminar el plan con ID: " + planId);

                            db.collection("planes").document(planId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Plan eliminado exitosamente de la base de datos.");
                                        Toast.makeText(this, "Plan eliminado exitosamente", Toast.LENGTH_SHORT).show();
                                        // Una vez eliminado con éxito, actualizar la lista de planes
                                        obtenerPlanes();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error al eliminar el plan de la base de datos: ", e);
                                        Toast.makeText(this, "Error al eliminar el plan", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Log.d(TAG, "El documento no existe. No se puede eliminar el plan con ID: " + planId);
                            Toast.makeText(this, "El plan no existe. No se puede eliminar.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Error al obtener el documento: ", task.getException());
                    }
                });
    }
    public interface AgregarPlanDialogListener {
        void onAgregarPlan(String nombre, String descripcion, double precio, String imageUrl);
    }
    private void mostrarDialogoPlanCompleto(Plan plan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_plan_details, null);
        builder.setView(dialogView);

        TextView textViewPlanName = dialogView.findViewById(R.id.textViewPlanName);
        TextView textViewPlanDescription = dialogView.findViewById(R.id.textViewPlanDescription);
        TextView textViewPlanDetails = dialogView.findViewById(R.id.textViewPlanDetails);

        textViewPlanName.setText(plan.getNombre());
        textViewPlanDescription.setText(plan.getDescripcion());

        String detallesFormateados = formatPlanDetails(plan.getDetalles());
        textViewPlanDetails.setText(detallesFormateados);

        builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private String formatPlanDetails(String detalles) {
        // Dividir los detalles por saltos de línea
        String[] detallesArray = detalles.split("\n");

        // Usar una StringBuilder para construir los detalles formateados
        StringBuilder formattedDetails = new StringBuilder();

        // Iterar sobre cada detalle
        for (String detail : detallesArray) {
            // Agregar una viñeta antes de cada detalle y luego un salto de línea
            formattedDetails.append("• ").append(detail.trim()).append("\n");
        }

        // Devolver los detalles formateados como una cadena
        return formattedDetails.toString();
    }







}