package com.example.interfaces1_2trimestre;

public class DataBase_Puntuacion {
    private String id;
    private String usuarioId;
    private String usuarioNombre;  // NUEVO campo
    private String pisoId;
    private int puntuacion;
    private String descripcion;
    private long timestamp;

    public DataBase_Puntuacion() {}

    public DataBase_Puntuacion(String id, String usuarioId, String usuarioNombre, String pisoId, int puntuacion, String descripcion) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;  // asignar
        this.pisoId = pisoId;
        this.puntuacion = puntuacion;
        this.descripcion = descripcion;
        this.timestamp = System.currentTimeMillis();
    }

    public DataBase_Puntuacion(String puntId, String usuarioId, String pisoId, int valor, String s) {
    }

    // Getters
    public String getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public String getUsuarioNombre() { return usuarioNombre; }  // nuevo getter
    public String getPisoId() { return pisoId; }
    public int getPuntuacion() { return puntuacion; }
    public String getDescripcion() { return descripcion; }
    public long getTimestamp() { return timestamp; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }  // nuevo setter
    public void setPisoId(String pisoId) { this.pisoId = pisoId; }
    public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
