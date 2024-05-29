package com.ispc.gymapp.views.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ispc.gymapp.R;
import com.ispc.gymapp.model.Plan;
import com.ispc.gymapp.views.adapter.PlanAdapter;

import java.util.ArrayList;
import java.util.List;

public class Ecommerce extends AppCompatActivity {
    PlanAdapter planAdapter;
    ArrayList<Plan> plans;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ImageButton cartButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecommerce2);
        setUpRecyclerView();

        cartButton = findViewById(R.id.cartButton);

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


    private void setUpRecyclerView() {
        plans = new ArrayList<>();
        planAdapter = new PlanAdapter(this, plans);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(planAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


}