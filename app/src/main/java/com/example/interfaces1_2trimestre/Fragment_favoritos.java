package com.example.interfaces1_2trimestre;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Fragment_favoritos extends Fragment {

    private RecyclerView recyclerView;
    private EditText searchBar;
    private PisoAdapter pisoAdapter;
    private List<Piso> listaPisos = new ArrayList<>();
    private List<Piso> listaPisosFiltrada = new ArrayList<>();
    private ImagenesSQLiteHelper imagenesDbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favoritos, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_pisos_fav);
        searchBar = view.findViewById(R.id.search_bar_fav);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        imagenesDbHelper = new ImagenesSQLiteHelper(getContext());

        pisoAdapter = new PisoAdapter(listaPisosFiltrada, getContext(),
                piso -> {
                    // Navegar a detalles del piso
                    Bundle bundle = new Bundle();
                    bundle.putString("id_piso", piso.getId());
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_fragment_favoritos_to_fragment_detalles_piso, bundle);
                },
                piso -> {
                    // Listener para toggle favorito: eliminar piso de favoritos aquí
                    toggleFavorito(piso);
                }
        );

        recyclerView.setAdapter(pisoAdapter);

        cargarPisosDesdeFirebase();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarPisos(s.toString());
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cada vez que vuelve a mostrar este fragment recarga la lista para reflejar cambios
        cargarPisosDesdeFirebase();
    }

    private void toggleFavorito(Piso piso) {
        String usuarioId = obtenerUserIdActual();
        if (usuarioId == null) {
            Toast.makeText(getContext(), "Debes iniciar sesión para usar favoritos", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference refFavoritos = FirebaseDatabase.getInstance()
                .getReference("favoritos")
                .child(usuarioId)
                .child(piso.getId());

        // Aquí como es la lista de favoritos, quitar el favorito es eliminar el nodo
        refFavoritos.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Favorito eliminado", Toast.LENGTH_SHORT).show();
                // Recarga la lista de favoritos para actualizar UI
                cargarPisosDesdeFirebase();
            } else {
                Toast.makeText(getContext(), "Error al eliminar favorito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarPisosDesdeFirebase() {
        String userId = obtenerUserIdActual();
        if (userId == null) {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            listaPisos.clear();
            listaPisosFiltrada.clear();
            pisoAdapter.notifyDataSetChanged();
            return;
        }

        DatabaseReference favoritosRef = FirebaseDatabase.getInstance().getReference("favoritos").child(userId);
        DatabaseReference pisosRef = FirebaseDatabase.getInstance().getReference("pisos");

        favoritosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot favoritosSnapshot) {
                if (!favoritosSnapshot.exists()) {
                    listaPisos.clear();
                    filtrarPisos(searchBar.getText().toString());
                    return;
                }

                List<String> idsFavoritos = new ArrayList<>();
                for (DataSnapshot fav : favoritosSnapshot.getChildren()) {
                    idsFavoritos.add(fav.getKey());
                }

                pisosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot pisosSnapshot) {
                        listaPisos.clear();

                        // Para controlar cuando se han cargado todas las medias
                        final int totalPisos = (int)pisosSnapshot.getChildrenCount();
                        final int[] pisosProcesados = {0};

                        for (DataSnapshot pisoSnapshot : pisosSnapshot.getChildren()) {
                            String id = pisoSnapshot.getKey();
                            if (idsFavoritos.contains(id)) {
                                Database_Piso piso = pisoSnapshot.getValue(Database_Piso.class);
                                if (piso != null) {

                                    List<String> imagenesLocales = imagenesDbHelper.obtenerImagenesPorPiso(id);
                                    if (imagenesLocales == null) {
                                        imagenesLocales = new ArrayList<>();
                                    }

                                    Piso nuevoPiso = new Piso(id, piso.getDireccion(), 0f, imagenesLocales);
                                    nuevoPiso.setFavorito(true);
                                    listaPisos.add(nuevoPiso);

                                    // Obtener puntuación media real para este piso
                                    obtenerPuntuacionMedia(id, media -> {
                                        nuevoPiso.setRating(media);

                                        // Cada vez que actualizamos uno, actualizamos la lista filtrada y el adapter
                                        filtrarPisos(searchBar.getText().toString());

                                        pisosProcesados[0]++;
                                        // Opcional: si quieres hacer algo cuando termine de cargar todas medias
                                        if (pisosProcesados[0] == idsFavoritos.size()) {
                                            // ya se han calculado todas las medias
                                        }
                                    });
                                } else {
                                    pisosProcesados[0]++;
                                }
                            } else {
                                pisosProcesados[0]++;
                            }
                        }

                        // Por si no hay favoritos (pero sí pisos), refrescamos UI
                        if (idsFavoritos.size() == 0) {
                            filtrarPisos(searchBar.getText().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error al cargar pisos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar favoritos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarPisos(String texto) {
        listaPisosFiltrada.clear();
        String textoLower = texto.toLowerCase();
        for (Piso p : listaPisos) {
            if (p.getDireccion() != null && p.getDireccion().toLowerCase().contains(textoLower)) {
                listaPisosFiltrada.add(p);
            }
        }
        pisoAdapter.notifyDataSetChanged();
    }

    private String obtenerUserIdActual() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    private void obtenerPuntuacionMedia(String idPiso, OnPuntuacionMediaListener listener) {
        DatabaseReference refPuntuaciones = FirebaseDatabase.getInstance()
                .getReference("puntuaciones"); // Cambia "puntuaciones" por el nodo correcto si es otro

        refPuntuaciones.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float suma = 0f;
                int contador = 0;

                for (DataSnapshot puntuacionSnapshot : snapshot.getChildren()) {
                    String pisoIdResena = puntuacionSnapshot.child("pisoId").getValue(String.class);
                    if (pisoIdResena != null && pisoIdResena.equals(idPiso)) {
                        Long puntuacionLong = puntuacionSnapshot.child("puntuacion").getValue(Long.class);
                        if (puntuacionLong != null) {
                            suma += puntuacionLong.floatValue();
                            contador++;
                        }
                    }
                }

                float media = (contador > 0) ? (suma / contador) : 0f;
                listener.onMediaObtenida(media);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onMediaObtenida(0f);
            }
        });
    }

    private interface OnPuntuacionMediaListener {
        void onMediaObtenida(float media);
    }

    private void borrarTodasLasImagenesLocales() {
        List<String> todasLasRutas = new ArrayList<>();
        for (Piso piso : listaPisos) {
            if (piso.getImagenesLocal() != null) {
                todasLasRutas.addAll(piso.getImagenesLocal());
            }
        }

        for (String ruta : todasLasRutas) {
            File archivo = new File(ruta);
            if (archivo.exists()) {
                archivo.delete();
            }
        }
    }
}
