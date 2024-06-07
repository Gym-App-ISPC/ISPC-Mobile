package com.ispc.gymapp.views.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ispc.gymapp.R;
import com.ispc.gymapp.views.adapter.ExercisesAdapter;
import com.ispc.gymapp.views.fragments.ExercisesAdvancedFragment;
import com.ispc.gymapp.views.fragments.ExercisesBeginnerFragment;
import com.ispc.gymapp.views.fragments.ExercisesIntermediateFragment;

public class ExerciseList extends AppCompatActivity {

    private ExercisesAdapter exercisesAdapter;
    private ViewPager2 viewPager2;

    public final static String EXTRA_EXERCISE_TYPE = "com.ispc.gymapp.extra.Exercise_Type";

    private String exerciseType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);
        viewPager2 = findViewById(R.id.viewPagerExercises);
        exercisesAdapter = new ExercisesAdapter(getSupportFragmentManager(), getLifecycle());
        // Add fragments
        exercisesAdapter.add(new ExercisesBeginnerFragment());
        exercisesAdapter.add(new ExercisesIntermediateFragment());
        exercisesAdapter.add(new ExercisesAdvancedFragment());

        viewPager2.setAdapter(exercisesAdapter);

        TabLayout exercisesTabLayout = findViewById(R.id.exercises_difficult_tabs);


        // Sync clic with tab
        new TabLayoutMediator(exercisesTabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0 -> tab.setText("Principiante");
                case 1 -> tab.setText("Intermedio");
                case 2 -> tab.setText("Avanzado");
            }
        }).attach();

        // Inicializa la vista del botón y configura el OnClickListener
        ImageButton arrow_return = findViewById(R.id.arrow_return);
        arrow_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cierra la actividad actual y vuelve a la actividad anterior
            }
        });
    }

    public void returnToParent(View view) {
        this.onBackPressed();
    }

    public void goToDescription(View view) {
        TextView textView = (TextView) view;

        Intent exercise = new Intent(this, ExercisesDescription.class);

        exerciseType = (String) textView.getText();

        exercise.putExtra(EXTRA_EXERCISE_TYPE, exerciseType);

        startActivity(exercise);
    }


}




