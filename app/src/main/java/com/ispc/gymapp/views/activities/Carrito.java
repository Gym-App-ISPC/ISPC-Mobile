package com.ispc.gymapp.views.activities;

import java.util.ArrayList;
import java.util.List;

public class Carrito {
    private static Carrito instance;
    private List<Plan> planes;

    private Carrito() {
        planes = new ArrayList<>();
    }

    public static synchronized Carrito getInstance() {
        if (instance == null) {
            instance = new Carrito();
        }
        return instance;
    }

    public void agregarPlan(Plan plan) {
        planes.add(plan);
    }

    public void eliminarPlan(Plan plan) {
        planes.remove(plan);
    }

    public List<Plan> getPlanes() {
        return planes;
    }
}

