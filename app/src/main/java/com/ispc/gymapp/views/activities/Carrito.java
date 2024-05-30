package com.ispc.gymapp.views.activities;

import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.ispc.gymapp.helper.OnCarritoCargadoListener;
import com.ispc.gymapp.model.Plan;

import java.util.ArrayList;
import java.util.List;

public class Carrito {
    private static Carrito instance;
    private List<Plan> planes;

    private Carrito() {
        planes = new ArrayList<>();
    }

    public static Carrito getInstance() {
        if (instance == null) {
            instance = new Carrito();
        }
        return instance;
    }
    public List<Plan> getPlanes() {
        return planes;
    }
    public void agregarPlan(Plan plan) {
        planes.clear();
        planes.add(plan);
    }

    public void guardarCarritoEnFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Gson gson = new Gson();
            String json = gson.toJson(planes);
            db.collection("users").document(uid).update("carrito", json);
        }
    }

    public void cargarCarritoDesdeFirestore(OnCarritoCargadoListener listener) {
        if (planes.isEmpty()) { // Solo carga desde Firestore si el carrito está vacío
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(uid).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        String json = task.getResult().getString("carrito");
                        Gson gson = new Gson();
                        planes = gson.fromJson(json, new TypeToken<ArrayList<Plan>>() {}.getType());
                        if (planes == null) {
                            planes = new ArrayList<>();
                        }
                    }
                    if (listener != null) {
                        listener.onCarritoCargado();
                    }
                });
            } else {
                if (listener != null) {
                    listener.onCarritoCargado();
                }
            }
        } else {
            // Si el carrito ya tiene datos, no es necesario cargarlos desde Firestore
            if (listener != null) {
                listener.onCarritoCargado();
            }
        }
    }

}






