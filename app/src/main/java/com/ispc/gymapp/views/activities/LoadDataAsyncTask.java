package com.ispc.gymapp.views.activities;

import android.os.AsyncTask;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ispc.gymapp.model.Plan;

import java.util.ArrayList;
import java.util.List;

public class LoadDataAsyncTask extends AsyncTask<Void, Void, List<Plan>> {
    private FirebaseFirestore db;
    private OnDataLoadedListener listener;

    public LoadDataAsyncTask(FirebaseFirestore db, OnDataLoadedListener listener) {
        this.db = db;
        this.listener = listener;
    }

    @Override
    protected List<Plan> doInBackground(Void... voids) {
        List<Plan> plans = new ArrayList<>();
        // Realiza la carga de datos desde Firestore en segundo plano
        db.collection("planes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Plan plan = document.toObject(Plan.class);
                        plan.setId(document.getId());
                        plans.add(plan);
                    }
                    // Notifica al listener una vez que se cargan los datos
                    listener.onDataLoaded(plans);
                })
                .addOnFailureListener(e -> {
                    // Maneja errores de carga de datos
                });
        return plans;
    }

    @Override
    protected void onPostExecute(List<Plan> plans) {
        super.onPostExecute(plans);
        // Realiza cualquier acción necesaria después de que se carguen los datos
    }

    public interface OnDataLoadedListener {
        void onDataLoaded(List<Plan> plans);
    }
}
