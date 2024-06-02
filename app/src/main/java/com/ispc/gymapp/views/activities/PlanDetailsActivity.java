package com.ispc.gymapp.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ispc.gymapp.R;

public class PlanDetailsActivity extends AppCompatActivity {

    private TextView planNameTextView;
    private TextView planDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_details);

        // Obtener referencias a los elementos de la interfaz de usuario
        planNameTextView = findViewById(R.id.planNameTextView);
        planDescriptionTextView = findViewById(R.id.planDescriptionTextView);

        // Obtener los datos del intent
        String planName = getIntent().getStringExtra("planName");
        String planDescription = getIntent().getStringExtra("planDescription");

        // Mostrar los detalles del plan en la interfaz de usuario
        if (planName != null && planDescription != null) {
            planNameTextView.setText(planName);
            planDescriptionTextView.setText(planDescription);
        } else {
            // Si falta alguno de los datos, muestra un mensaje de error
            planNameTextView.setText("Error: No se pudo obtener el nombre del plan.");
            planDescriptionTextView.setText("Error: No se pudo obtener la descripción del plan.");
        }

        Button startPlanButton = findViewById(R.id.startPlanButton);
        startPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanDetailsActivity.this, DietExerciseActivity.class);

                intent.putExtra("buttonPressed", true);
                startActivity(intent);
            }
        });
    }
}
