package com.ispc.gymapp.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ispc.gymapp.R;
import com.ispc.gymapp.model.Meal;
import com.ispc.gymapp.views.adapter.MealsAdapter;

import java.util.Objects;

public class MealActivity extends AppCompatActivity implements View.OnClickListener{

    public FirebaseFirestore db ;
    public FirebaseAuth mAuth;
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        data = Objects.requireNonNull(getIntent().getExtras()).getString("mealType");

        setUpRecyclerView();

        ImageButton arrow_diet = findViewById(R.id.arrow_diet);
        arrow_diet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cierra la actividad actual y vuelve a la actividad anterior
            }
        });

    }

    private void setUpRecyclerView() {
        // Query
        Query query = db.collection("meals").whereEqualTo("type",data.toUpperCase());
        // Options
        FirestoreRecyclerOptions<Meal> options = new FirestoreRecyclerOptions.Builder<Meal>()
                .setQuery(query, Meal.class)
                .setLifecycleOwner(this)
                .build();

        // Adapter
        MealsAdapter mealsAdapter = new MealsAdapter(this,options);
        // Recycler
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mealsAdapter);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnBack){
            this.onBackPressed();
        }
    }
}