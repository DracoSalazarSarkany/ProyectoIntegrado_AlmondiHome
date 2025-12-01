package com.example.interfaces1_2trimestre;

public class Usuario {
    private static String id;
    private static String nombre;

    // Establece el usuario actual
    public static void set(String usuarioId, String nombreUsuario) {
        id = usuarioId;
        nombre = nombreUsuario;
    }

    // Obtiene el ID del usuario actual
    public static String getId() {
        return id;
    }

    // Obtiene el nombre del usuario actual
    public static String getNombre() {
        return nombre;
    }

    // Verifica si el usuario ha sido establecido
    public static boolean estaInicializado() {
        return id != null && nombre != null;
    }

    // Limpia los datos del usuario
    public static void limpiar() {
        id = null;
        nombre = null;
    }
}
