package com.ispc.gymapp.views.activities;

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

    public void limpiarCarrito() {
        planes.clear();
    }


}

