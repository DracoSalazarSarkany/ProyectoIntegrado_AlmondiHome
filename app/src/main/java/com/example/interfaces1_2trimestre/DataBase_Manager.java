package com.example.interfaces1_2trimestre;

import android.util.Log;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class DataBase_Manager {

    static {
        // Habilitar persistencia offline una vez
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static DataBase_Manager instance;
    public final DatabaseReference db;

    DataBase_Manager() {
        db = FirebaseDatabase.getInstance().getReference();
    }

    public static synchronized DataBase_Manager getInstance() {
        if (instance == null) {
            instance = new DataBase_Manager();
        }
        return instance;
    }

    public String generarKey(String nodo) {
        return db.child(nodo).push().getKey();
    }

    public void agregarPiso(@NonNull Database_Piso piso) {
        db.child("pisos")
                .child(piso.getId())
                .setValue(piso)
                .addOnSuccessListener(aVoid -> Log.d("DBM", "Piso agregado: " + piso.getId()))
                .addOnFailureListener(e -> Log.w("DBM", "Error al agregar piso", e));
    }

    public void agregarUsuario(@NonNull DataBase_Usuario usuario) {
        db.child("usuarios")
                .child(usuario.getId())
                .setValue(usuario)
                .addOnSuccessListener(aVoid -> Log.d("DBM", "Usuario agregado: " + usuario.getId()))
                .addOnFailureListener(e -> Log.w("DBM", "Error al agregar usuario", e));
    }

    public void agregarPuntuacion(@NonNull DataBase_Puntuacion punt) {
        db.child("puntuaciones")
                .child(punt.getId())
                .setValue(punt)
                .addOnSuccessListener(aVoid -> Log.d("DBM", "Puntuación agregada: " + punt.getId()))
                .addOnFailureListener(e -> Log.w("DBM", "Error al agregar puntuación", e));
    }

    public void agregarPuntuacionYActualizarUsuario(String usuarioId, String pisoId, int valor) {
        String puntId = db.child("puntuaciones").push().getKey();
        DataBase_Puntuacion p = new DataBase_Puntuacion(puntId, usuarioId, pisoId, valor, "");
        agregarPuntuacion(p);

        db.child("usuarios")
                .child(usuarioId)
                .child("puntuaciones")
                .child(pisoId)
                .setValue(valor)
                .addOnSuccessListener(aVoid -> Log.d("DBM", "Mapa de usuario actualizado"))
                .addOnFailureListener(e -> Log.w("DBM", "Error actualizando mapa usuario", e));

        recalcularMediaPiso(pisoId);
    }

    public void leerPiso(String pisoId, ValueEventListener listener) {
        db.child("pisos").child(pisoId).get()
                .addOnSuccessListener(snapshot -> listener.onDataChange(snapshot))
                .addOnFailureListener(e -> listener.onCancelled(DatabaseError.fromException(e)));
    }

    public void leerTodosPisos(ValueEventListener listener) {
        db.child("pisos").get()
                .addOnSuccessListener(snapshot -> listener.onDataChange(snapshot))
                .addOnFailureListener(e -> listener.onCancelled(DatabaseError.fromException(e)));
    }

    public void leerUsuario(String usuarioId, ValueEventListener listener) {
        db.child("usuarios").child(usuarioId).get()
                .addOnSuccessListener(snapshot -> listener.onDataChange(snapshot))
                .addOnFailureListener(e -> listener.onCancelled(DatabaseError.fromException(e)));
    }

    public void leerTodosUsuarios(ValueEventListener listener) {
        db.child("usuarios").get()
                .addOnSuccessListener(snapshot -> listener.onDataChange(snapshot))
                .addOnFailureListener(e -> listener.onCancelled(DatabaseError.fromException(e)));
    }

    public void leerPuntuacion(String puntuacionId, ValueEventListener listener) {
        db.child("puntuaciones").child(puntuacionId).get()
                .addOnSuccessListener(snapshot -> listener.onDataChange(snapshot))
                .addOnFailureListener(e -> listener.onCancelled(DatabaseError.fromException(e)));
    }

    public void recalcularMediaPiso(String pisoId) {
        db.child("puntuaciones")
                .orderByChild("pisoId")
                .equalTo(pisoId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int suma = 0, cnt = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            DataBase_Puntuacion punt = ds.getValue(DataBase_Puntuacion.class);
                            if (punt != null) {
                                suma += punt.getPuntuacion();
                                cnt++;
                            }
                        }
                        double media = cnt > 0 ? (double) suma / cnt : 0.0;
                        db.child("pisos")
                                .child(pisoId)
                                .child("puntuacionMedia")
                                .setValue(media);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("DBM", "Error recalculando media", error.toException());
                    }
                });
    }

    public void borrarPiso(@NonNull String pisoId) {
        db.child("pisos")
                .child(pisoId)
                .removeValue()
                .addOnSuccessListener(aVoid -> Log.d("DBM", "Piso borrado: " + pisoId))
                .addOnFailureListener(e -> Log.w("DBM", "Error al borrar piso", e));
    }

    public void borrarUsuario(@NonNull String usuarioId) {
        db.child("usuarios")
                .child(usuarioId)
                .removeValue()
                .addOnSuccessListener(aVoid -> Log.d("DBM", "Usuario borrado: " + usuarioId))
                .addOnFailureListener(e -> Log.w("DBM", "Error al borrar usuario", e));
    }

    public void borrarPuntuacion(@NonNull String puntuacionId) {
        db.child("puntuaciones")
                .child(puntuacionId)
                .removeValue()
                .addOnSuccessListener(aVoid -> Log.d("DBM", "Puntuación borrada: " + puntuacionId))
                .addOnFailureListener(e -> Log.w("DBM", "Error al borrar puntuación", e));
    }

    public String getUsuarioLogueadoId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            Log.w("DBM", "No hay ningún usuario autenticado");
            return null;
        }
    }

    public void obtenerPuntuacionUsuarioParaPiso(String pisoId, String usuarioId, ValueEventListener listener) {
        db.child("puntuaciones")
                .orderByChild("pisoId")
                .equalTo(pisoId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DataBase_Puntuacion puntuacionUsuario = null;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            DataBase_Puntuacion punt = ds.getValue(DataBase_Puntuacion.class);
                            if (punt != null && usuarioId.equals(punt.getUsuarioId())) {
                                puntuacionUsuario = punt;
                                break;
                            }
                        }
                        if (puntuacionUsuario != null) {
                            listener.onDataChange(snapshot);
                        } else {
                            listener.onCancelled(DatabaseError.fromException(
                                    new Exception("No existe puntuación para este usuario y piso")));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onCancelled(error);
                    }
                });
    }

    // Listener personalizado para resenas
    public interface ResenasListener {
        void onResenasChanged(List<DataBase_Puntuacion> resenas);
        void onError(Exception e);
    }

    public void escucharResenasPorPiso(String pisoId, ResenasListener listener) {
        db.child("puntuaciones")
                .orderByChild("pisoId")
                .equalTo(pisoId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<DataBase_Puntuacion> lista = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            DataBase_Puntuacion punt = ds.getValue(DataBase_Puntuacion.class);
                            if (punt != null) {
                                lista.add(punt);
                            }
                        }
                        listener.onResenasChanged(lista);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onError(error.toException());
                    }
                });
    }
}
