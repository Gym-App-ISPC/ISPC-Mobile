package com.ispc.gymapp.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ispc.gymapp.R;

import java.util.List;

public class DietExerciseActivity extends AppCompatActivity implements View.OnClickListener {

    public FirebaseFirestore db ;
    public FirebaseAuth mAuth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_exercise);

        boolean buttonPressed = getIntent().getBooleanExtra("buttonPressed", false);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_Navigation);
        setupNavigation(bottomNavigationView);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        CardView cardViewBreakfast = findViewById(R.id.cardViewBreakfast);
        CardView cardViewExercise = findViewById(R.id.cardView);
        CardView cardViewLunch = findViewById(R.id.cardViewLunch);
        CardView cardViewDinner = findViewById(R.id.cardViewDinner);
        for (CardView card : List.of(cardViewLunch, cardViewExercise, cardViewBreakfast, cardViewDinner)) {
            card.setOnClickListener(this);
        }
        checkUserHistoryAndModifyCard();


    }
    @Override
    protected void onResume() {
        super.onResume();
        checkUserHistoryAndModifyCard();
    }
    private void checkUserHistoryAndModifyCard() {
        if (user != null) {
            DocumentReference userDocRef = db.collection("users").document(user.getUid());

            userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    List<String> historialCompras = (List<String>) documentSnapshot.get("historialCompras");
                    if (historialCompras != null && !historialCompras.isEmpty()) {
                        // Modificar las propiedades de las vistas
                        TextView textView8 = findViewById(R.id.textView8);
                        TextView textView7 = findViewById(R.id.textView7);
                        ImageView imageView9 = findViewById(R.id.imageView9);

                        if (textView8 != null) textView8.setText("Comienza a disfrutar de tu plan");
                        if (imageView9 != null) imageView9.setImageResource(R.drawable.ecommerceplanprincipiantes);
                        if (textView7 != null) textView7.setText("COMENZÁ A ENTRENAR");
                    } else {
                        Toast.makeText(this, "No tienes un plan activo.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "El documento del usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error al verificar el historial de compras", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show();
        }
    }



    private void setupNavigation(BottomNavigationView bottomNavigationView){
        bottomNavigationView.setSelectedItemId(R.id.title_activity_exercise);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.title_activity_exercise) {
                    return true;
                }

                if (id == R.id.home) {
                    startActivitySafely(MainActivity.class);
                    return true;
                }

                if (id == R.id.shopItem) {
                    startActivitySafely(Ecommerce.class);
                    return true;
                }

                if (id == R.id.accountItem) {
                    startActivitySafely(MiPerfilActivity.class);
                    return true;
                }

                return false;

            }
            private void startActivitySafely(Class<?> cls) {
                if (!cls.isInstance(this)) {
                    startActivity(new Intent(getApplicationContext(), cls));
                    overridePendingTransition(0, 0);
                }
            }


        });
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.cardView){
            Intent intent = new Intent(DietExerciseActivity.this, ExerciseList.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }
        if (view.getId() == R.id.cardViewBreakfast) {
            // Iniciar el fragmento de Desayuno con el tipo de comida como argumento
            Intent intent = new Intent(this, MealActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra("mealType", "breakfast");
            startActivity(intent);
        } else if (view.getId() == R.id.cardViewLunch) {
            // Iniciar el fragmento de Almuerzo con el tipo de comida como argumento
            Intent intent = new Intent(this, MealActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra("mealType", "lunch");
            startActivity(intent);
        } else if (view.getId() == R.id.cardViewDinner) {
            // Iniciar el fragmento de Cena con el tipo de comida como argumento
            Intent intent = new Intent(this, MealActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra("mealType", "dinner");
            startActivity(intent);
        }


        }
    }


