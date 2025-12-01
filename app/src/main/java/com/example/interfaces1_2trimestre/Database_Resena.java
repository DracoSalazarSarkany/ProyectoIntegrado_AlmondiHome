package com.example.interfaces1_2trimestre;

public class Database_Resena {
    private String id;
    private String usuarioId;
    private String pisoId;
    private int puntuacion;
    private String comentario;
    private long timestamp;

    public Database_Resena() {}  // Requerido por Firebase

    public Database_Resena(String id, String usuarioId, String pisoId, int puntuacion, String comentario, long timestamp) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.pisoId = pisoId;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.timestamp = timestamp;
    }

    // Getters y setters
    public String getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public String getPisoId() { return pisoId; }
    public int getPuntuacion() { return puntuacion; }
    public String getComentario() { return comentario; }
    public long getTimestamp() { return timestamp; }

    public void setId(String id) { this.id = id; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public void setPisoId(String pisoId) { this.pisoId = pisoId; }
    public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
