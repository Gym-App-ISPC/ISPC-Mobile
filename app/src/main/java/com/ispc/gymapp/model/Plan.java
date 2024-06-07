package com.ispc.gymapp.model;

import java.util.UUID;

public class Plan {

    private String id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String imagen;
    private String detalles;

    public Plan() {
        this.id = UUID.randomUUID().toString();
    }

    public Plan(String nombre, String descripcion, double precio, String imagen) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrecioString() {
        return "$" + precio;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }
}
