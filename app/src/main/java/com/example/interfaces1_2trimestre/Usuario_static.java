package com.example.interfaces1_2trimestre;

/**
 * Clase singleton estática para almacenar la sesión de usuario en memoria.
 */
public final class Usuario_static {
    // Campos estáticos que almacenan los datos del usuario
    private static String userId;
    private static String userName;

    // Constructor privado para evitar instanciación
    private Usuario_static() { }

    /**
     * Inicia la sesión del usuario guardando su ID y nombre.
     * Debes llamar a este método tras un login exitoso.
     *
     * @param id   El identificador único del usuario
     * @param name El nombre del usuario
     */
    public static void startSession(String id, String name) {
        userId = id;
        userName = name;
    }

    /**
     * Obtiene el ID del usuario actualmente en sesión.
     *
     * @return El userId, o null si aún no se ha iniciado sesión.
     */
    public static String getUserId() {
        return userId;
    }

    /**
     * Obtiene el nombre del usuario actualmente en sesión.
     *
     * @return El userName, o null si aún no se ha iniciado sesión.
     */
    public static String getUserName() {
        return userName;
    }

    /**
     * Establece manualmente el ID del usuario.
     *
     * @param id Nuevo ID del usuario
     */
    public static void setUserId(String id) {
        userId = id;
    }

    /**
     * Establece manualmente el nombre del usuario.
     *
     * @param name Nuevo nombre del usuario
     */
    public static void setUserName(String name) {
        userName = name;
    }

    /**
     * Indica si hay una sesión iniciada.
     *
     * @return true si userId y userName no son null.
     */
    public static boolean isLoggedIn() {
        return userId != null && userName != null;
    }

    /**
     * Termina la sesión borrando los datos del usuario.
     * Llama a este método al hacer logout.
     */
    public static void clearSession() {
        userId = null;
        userName = null;
    }
}