package com.example.interfaces1_2trimestre;

import java.util.List;
import java.util.Map;

public class DataBase_Usuario {
    private String id;
    private String nombre;
    private Map<String, Integer> puntuaciones; // Clave: ID del piso, Valor: Puntuación dada
    private List<String> favoritos; // NUEVO: Lista de favoritos

    public DataBase_Usuario() {}

    public DataBase_Usuario(String id, String nombre, Map<String, Integer> puntuaciones, List<String> favoritos) {
        this.id = id;
        this.nombre = nombre;
        this.puntuaciones = puntuaciones;
        this.favoritos = favoritos;
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

    public Map<String, Integer> getPuntuaciones() {
        return puntuaciones;
    }

    public void setPuntuaciones(Map<String, Integer> puntuaciones) {
        this.puntuaciones = puntuaciones;
    }

    public List<String> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(List<String> favoritos) {
        this.favoritos = favoritos;
    }

    public void agregarPuntuacion(String pisoId, int puntuacion) {
        if (puntuaciones != null) {
            puntuaciones.put(pisoId, puntuacion);
        }
    }
}
