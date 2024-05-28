package com.ispc.gymapp.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ispc.gymapp.R;
import com.ispc.gymapp.views.adapter.CarritoAdapter;



import java.util.List;

public class CarritoActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCarrito;
    private CarritoAdapter carritoAdapter;
    private Button btnCheckout;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(CarritoActivity.this, Ecommerce.class);
            startActivity(intent);
            finish();
        });

        recyclerViewCarrito = findViewById(R.id.recyclerViewCarrito);
        btnCheckout = findViewById(R.id.btnCheckout);

        recyclerViewCarrito.setLayoutManager(new LinearLayoutManager(this));
        List<Plan> planes = Carrito.getInstance().getPlanes();
        carritoAdapter = new CarritoAdapter(planes);
        recyclerViewCarrito.setAdapter(carritoAdapter);

        btnCheckout.setOnClickListener(v -> {
            // Aquí puedes manejar el proceso de checkout
            Toast.makeText(CarritoActivity.this, "Proceso de checkout iniciado", Toast.LENGTH_SHORT).show();
        });
    }
}
