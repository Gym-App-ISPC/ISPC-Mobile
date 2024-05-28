package com.ispc.gymapp.views.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ispc.gymapp.R;
import com.ispc.gymapp.views.adapter.PlanAdapter;

import java.util.ArrayList;
import java.util.List;

public class Ecommerce extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PlanAdapter adapter;
    private List<Plan> planList;
    private ImageButton cartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecommerce2);

        cartButton = findViewById(R.id.cartButton);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Ecommerce.this, CarritoActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        planList = new ArrayList<>();
        planList.add(new Plan("Plan Básico", getString(R.string.DescriptionPB), 8000, R.drawable.ecommerceplanprincipiantes));
        planList.add(new Plan("Plan Estándar", getString(R.string.DescriptionPM), 12000, R.drawable.ecommerceplanmedio));
        planList.add(new Plan("Plan Premium", getString(R.string.DescriptionPA), 15000, R.drawable.ecommerceplanavanzado));

        adapter = new PlanAdapter(planList, this);
        recyclerView.setAdapter(adapter);

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

}