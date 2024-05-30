package com.ispc.gymapp.views.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ispc.gymapp.R;
import com.ispc.gymapp.model.Plan;
import com.ispc.gymapp.presenters.login.LoginPresenter;
import com.ispc.gymapp.views.adapter.PlanAdapter;

import java.util.ArrayList;
import java.util.List;

public class Ecommerce extends AppCompatActivity implements PlanAdapter.OnPlanClickListener, LoginPresenter.RolUsuarioCallback{
    PlanAdapter planAdapter;
    ArrayList<Plan> plans;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    LoginPresenter loginPresenter;

    private ImageButton cartButton;

    private TextView cartBadge;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecommerce2);

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
    public void onPlanClick(Plan plan) {
        actualizarBadge();
        mostrarDialogo(plan);
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
        db.collection("planes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Plan plan = document.toObject(Plan.class);
                            plans.add(plan); //
                        }

                        planAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Error obteniendo los planes: ", task.getException());
                    }
                });

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
        planAdapter = new PlanAdapter(this, plans, this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(planAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }




}