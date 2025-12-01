package com.example.interfaces1_2trimestre;

import java.util.List;

public class Piso {
    private String id;
    private String direccion;
    private String descripcion;   // <-- Añadido
    private float rating;
    private List<String> imagenesLocal;

    private boolean favorito;  // <-- Añadido

    public Piso() {
        // Constructor vacío necesario para Firebase y otras librerías
    }

    public Piso(String id, String direccion, float rating, List<String> imagenesLocal) {
        this.id = id;
        this.direccion = direccion;
        this.rating = rating;
        this.imagenesLocal = imagenesLocal;
        this.favorito = false;  // Valor inicial
    }

    // Getter y setter para descripcion
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<String> getImagenesLocal() {
        return imagenesLocal;
    }

    public void setImagenesLocal(List<String> imagenesLocal) {
        this.imagenesLocal = imagenesLocal;
    }

    // GETTER Y SETTER PARA FAVORITO
    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }
}
