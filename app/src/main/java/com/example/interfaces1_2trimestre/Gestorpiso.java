package com.example.interfaces1_2trimestre;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Gestorpiso {

    public interface PisoCallback {
        void onSuccess(String pisoId);
        void onFailure(Exception e);
    }

    public void registrarNuevoPisoConCallback(String direccion, String codigoPostal,  double precio,
                                              String nombreUsuario, int puntuacion, PisoCallback callback,
                                              ImagenesSQLiteHelper imagenesDbHelper, String rutaImagenLocal) {
        try {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("pisos");

            String pisoId = dbRef.push().getKey();
            if (pisoId == null) {
                throw new Exception("Error al generar ID del piso");
            }

            // Usamos el constructor actualizado que incluye puntuacion
            Database_Piso nuevoPiso = new Database_Piso(pisoId, direccion, codigoPostal, precio, puntuacion);

            dbRef.child(pisoId).setValue(nuevoPiso).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (rutaImagenLocal != null && !rutaImagenLocal.isEmpty()) {
                        imagenesDbHelper.insertarImagen(pisoId, rutaImagenLocal);
                    }
                    if (callback != null) {
                        callback.onSuccess(pisoId);
                    }
                } else {
                    if (callback != null) {
                        callback.onFailure(task.getException());
                    }
                }
            });

        } catch (Exception e) {
            if (callback != null) {
                callback.onFailure(e);
            }
        }
    }

    // NUEVO: actualizar puntuación en un piso
    public void actualizarPuntuacionPiso(String pisoId, float puntuacion, PisoCallback callback) {
        try {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("pisos").child(pisoId).child("puntuacion");
            dbRef.setValue(puntuacion).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (callback != null) {
                        callback.onSuccess(pisoId);
                    }
                } else {
                    if (callback != null) {
                        callback.onFailure(task.getException());
                    }
                }
            });
        } catch (Exception e) {
            if (callback != null) {
                callback.onFailure(e);
            }
        }
    }
}
