package com.example.interfaces1_2trimestre;

import java.util.List;

public class Database_Piso {

    private String id;
    private String direccion;
    private String codigoPostal;
    private String descripcion;

    private double precioMensualidad;

    private double puntuacionMedia;

    private int puntuacion;  // Añadido campo puntuacion individual

    // Constructor vacío para Firebase
    public Database_Piso() {}

    // Constructor con parámetros (puedes añadir puntuacion si quieres)
    public Database_Piso(String id, String direccion, String codigoPostal,  double precioMensualidad, int puntuacion) {
        this.id = id;
        this.direccion = direccion;
        this.codigoPostal = codigoPostal;
        //this.descripcion = descripcion;
        this.precioMensualidad = precioMensualidad;
        this.puntuacionMedia = 0.0; // Inicialmente sin puntuaciones, se actualizará más tarde
        this.puntuacion = puntuacion;
    }

    // Getters y Setters
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

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecioMensualidad() {
        return precioMensualidad;
    }

    public void setPrecioMensualidad(double precioMensualidad) {
        this.precioMensualidad = precioMensualidad;
    }

    public double getPuntuacionMedia() {
        return puntuacionMedia;
    }

    public void setPuntuacionMedia(double puntuacionMedia) {
        this.puntuacionMedia = puntuacionMedia;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    @Override
    public String toString() {
        return "Piso {\n" +
                "  id = '" + getId() + "',\n" +
                "  dirección = '" + getDireccion() + "',\n" +
                "  código postal = '" + getCodigoPostal() + "',\n" +
                //"  descripción = '" + getDescripcion() + "',\n" +
                "  precioMensualidad = " + getPrecioMensualidad() + ",\n" +
                "  puntuacionMedia = " + getPuntuacionMedia() + ",\n" +
                "  puntuacion = " + getPuntuacion() + "\n" +
                "}";
    }

    // Método para calcular la media de las puntuaciones
    public void actualizarPuntuacionMedia(List<DataBase_Puntuacion> listaPuntuaciones) {
        if (listaPuntuaciones == null || listaPuntuaciones.isEmpty()) {
            this.puntuacionMedia = 0.0;
            return;
        }
        double suma = 0;
        for (DataBase_Puntuacion p : listaPuntuaciones) {
            suma += p.getPuntuacion();
        }
        this.puntuacionMedia = suma / listaPuntuaciones.size();
    }
}
