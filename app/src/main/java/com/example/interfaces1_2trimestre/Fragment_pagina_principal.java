package com.example.interfaces1_2trimestre;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Fragment_pagina_principal extends Fragment {

    private static final String TAG = "FragmentPaginaPrincipal";

    private RecyclerView recyclerView;
    private EditText searchBar;
    private PisoAdapter pisoAdapter;
    private List<Piso> listaPisos = new ArrayList<>();
    private List<Piso> listaPisosFiltrada = new ArrayList<>();
    private Button btnAñadirPiso;
    private ImagenesSQLiteHelper imagenesDbHelper;
    private List<String> listaFavoritos = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pagina_principal, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_pisos);
        searchBar = view.findViewById(R.id.search_bar);
        btnAñadirPiso = view.findViewById(R.id.button2);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        imagenesDbHelper = new ImagenesSQLiteHelper(getContext());

        pisoAdapter = new PisoAdapter(listaPisosFiltrada, getContext(),
                piso -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("id_piso", piso.getId());
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_fragment_pagina_principal_to_fragment_detalles_piso, bundle);
                },
                this::toggleFavorito);

        recyclerView.setAdapter(pisoAdapter);

        // Guardar nombre del usuario si no está ya guardado
        String usuarioId = Usuario.getId();
        if (usuarioId != null && !Usuario.estaInicializado()) {
            DatabaseReference refUsuario = FirebaseDatabase.getInstance()
                    .getReference("usuarios")
                    .child(usuarioId);

            refUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String nombreUsuario = snapshot.child("nombre").getValue(String.class);
                        if (nombreUsuario == null) nombreUsuario = "Usuario";

                        Usuario.set(usuarioId, nombreUsuario);
                        Usuario_static.startSession(usuarioId, nombreUsuario);
                        Log.d(TAG, "Usuario cargado: " + nombreUsuario);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error al cargar nombre de usuario: " + error.getMessage());
                }
            });
        }

        cargarPisosDesdeFirebase();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarPisos(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnAñadirPiso.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_fragment_pagina_principal_to_fragment_aniadir_piso);
        });

        return view;
    }

    private void toggleFavorito(Piso piso) {
        String usuarioId = Usuario.getId();
        if (usuarioId == null) {
            Log.d(TAG, "Usuario no logueado");
            return;
        }

        DataBase_Manager dbManager = new DataBase_Manager();

        dbManager.leerUsuario(usuarioId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DatabaseReference refFavoritos = FirebaseDatabase.getInstance()
                            .getReference("favoritos")
                            .child(usuarioId)
                            .child(piso.getId());

                    boolean nuevoEstado = !piso.isFavorito();

                    if (nuevoEstado) {
                        refFavoritos.setValue(true).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                cargarPisosDesdeFirebase();
                            }
                        });
                    } else {
                        refFavoritos.removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                cargarPisosDesdeFirebase();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void cargarFavoritosDesdeFirebase(Runnable onComplete) {
        String usuarioId = Usuario.getId();
        if (usuarioId == null) {
            listaFavoritos.clear();
            onComplete.run();
            return;
        }

        DatabaseReference refFavoritos = FirebaseDatabase.getInstance()
                .getReference("favoritos")
                .child(usuarioId);

        refFavoritos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaFavoritos.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String idPisoFavorito = ds.getKey();
                    listaFavoritos.add(idPisoFavorito);
                }
                onComplete.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onComplete.run();
            }
        });
    }

    private void cargarPisosDesdeFirebase() {
        cargarFavoritosDesdeFirebase(() -> {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("pisos");

            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listaPisos.clear();
                    if (!snapshot.exists()) {
                        filtrarPisos(searchBar.getText().toString());
                        return;
                    }

                    AtomicInteger pisosProcesados = new AtomicInteger(0);
                    int totalPisos = (int) snapshot.getChildrenCount();

                    for (DataSnapshot pisoSnapshot : snapshot.getChildren()) {
                        Database_Piso pisoDb = pisoSnapshot.getValue(Database_Piso.class);
                        if (pisoDb != null) {
                            String id = pisoSnapshot.getKey();
                            List<String> imagenesLocales = imagenesDbHelper.obtenerImagenesPorPiso(id);
                            if (imagenesLocales == null) imagenesLocales = new ArrayList<>();

                            Piso nuevoPiso = new Piso(id, pisoDb.getDireccion(), 0f, imagenesLocales);
                            nuevoPiso.setFavorito(listaFavoritos.contains(id));
                            listaPisos.add(nuevoPiso);

                            obtenerPuntuacionMedia(id, media -> {
                                nuevoPiso.setRating(media);
                                if (pisosProcesados.incrementAndGet() == totalPisos) {
                                    filtrarPisos(searchBar.getText().toString());
                                }
                            });
                        } else {
                            if (pisosProcesados.incrementAndGet() == totalPisos) {
                                filtrarPisos(searchBar.getText().toString());
                            }
                        }
                    }

                    if (totalPisos == 0) {
                        filtrarPisos(searchBar.getText().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });
    }

    private void obtenerPuntuacionMedia(String idPiso, PuntuacionCallback callback) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("puntuaciones")
                .child(idPiso);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float suma = 0;
                int contador = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Integer puntuacion = ds.getValue(Integer.class);
                    if (puntuacion != null) {
                        suma += puntuacion;
                        contador++;
                    }
                }

                float media = contador > 0 ? suma / contador : 0f;
                callback.onPuntuacionCalculada(media);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onPuntuacionCalculada(0f);
            }
        });
    }

    private void filtrarPisos(String texto) {
        listaPisosFiltrada.clear();
        if (texto.isEmpty()) {
            listaPisosFiltrada.addAll(listaPisos);
        } else {
            String textoMinuscula = texto.toLowerCase();
            for (Piso piso : listaPisos) {
                if (piso.getDireccion().toLowerCase().contains(textoMinuscula)) {
                    listaPisosFiltrada.add(piso);
                }
            }
        }
        pisoAdapter.notifyDataSetChanged();
    }

    private interface PuntuacionCallback {
        void onPuntuacionCalculada(float media);
    }
}
