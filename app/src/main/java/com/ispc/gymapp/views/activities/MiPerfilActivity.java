package com.ispc.gymapp.views.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ispc.gymapp.R;
import com.ispc.gymapp.model.User;
import com.ispc.gymapp.views.activities.DietExerciseActivity;
import com.ispc.gymapp.views.activities.Ecommerce;
import com.ispc.gymapp.views.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ispc.gymapp.views.adapter.ProfileAdapter;

import java.text.DecimalFormat;

public class MiPerfilActivity extends AppCompatActivity {

    private EditText pesoEditText;
    private EditText alturaEditText;
    private TextView imcTextView;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ProfileAdapter adapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private User user;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            DocumentReference usernameRef = db.collection("users").document(firebaseUser.getUid());
            usernameRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {

                        user = documentSnapshot.toObject(User.class);
                        createAdapterAndViewPager();
                    }
                }
            });

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_Navigator);
        bottomNavigationView.setSelectedItemId(R.id.accountItem);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.accountItem) {
                    return true;
                }

                if (id == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                if (id == R.id.shopItem) {
                    startActivity(new Intent(getApplicationContext(), Ecommerce.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                if (id == R.id.title_activity_exercise) {
                    startActivity(new Intent(getApplicationContext(), DietExerciseActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;

            }


        });

    }

    private void createAdapterAndViewPager() {
        tabLayout = findViewById(R.id.tabLayoutProfile);
        viewPager = findViewById(R.id.viewPagerProfile);

        adapter = new ProfileAdapter(getSupportFragmentManager(), getLifecycle(), user);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Mi Perfil");
            } else if (position == 1) {
                tab.setText("Soporte");
            }
        }).attach();

    }

    @SuppressLint("SetTextI18n")
    private void calcularIMC() {
        String pesoStr = pesoEditText.getText().toString();
        String alturaStr = alturaEditText.getText().toString();

        if (!pesoStr.isEmpty() && !alturaStr.isEmpty()) {
            // Convierte las entradas de texto a números
            double peso = Double.parseDouble(pesoStr);
            double altura = Double.parseDouble(alturaStr);

            // Calcula el IMC
            double imc = peso / (altura * altura);

            // Formatea el resultado con dos decimales
            DecimalFormat df = new DecimalFormat("#.##");
            String resultadoIMC = df.format(imc);

            // Muestra el resultado en el TextView
            imcTextView.setText("IMC: " + resultadoIMC);
        } else {
            // Muestra un mensaje de error si falta peso o altura
            imcTextView.setText("Por favor, ingresa peso y altura.");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void returnToHome(View view) {
        Intent home = new Intent(this, MainActivity.class);
        startActivity(home);
    }

    public void goToRoutine(View view) {
        // Intent
        Intent intent = new Intent(this, RoutineActivity.class);
        // Go to Routine Activity
        startActivity(intent);
    }
}
