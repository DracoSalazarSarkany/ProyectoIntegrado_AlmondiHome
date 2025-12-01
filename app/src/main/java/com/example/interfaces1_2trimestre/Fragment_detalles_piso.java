package com.example.interfaces1_2trimestre;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Fragment_detalles_piso extends Fragment {

    private ViewPager2 viewPagerImagenes;
    private RatingBar ratingBar;
    private TextView direccionTextView;
    private TextView mostrarEnMapsTextView;
    private TextView escribirResenaTextView;
    private RecyclerView recyclerViewResenas;

    private ImagenesSQLiteHelper imagenesDbHelper;
    private Piso pisoActual;
    private String idPiso;

    private ResenasAdapter resenasAdapter;
    private final DataBase_Manager databaseManager = DataBase_Manager.getInstance();

    public Fragment_detalles_piso() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detalles_piso, container, false);

        // Inicializar vistas
        viewPagerImagenes = rootView.findViewById(R.id.view_pager_imagenes);
        ratingBar = rootView.findViewById(R.id.rating_bar_piso_principal);
        direccionTextView = rootView.findViewById(R.id.text_direccion_piso);
        mostrarEnMapsTextView = rootView.findViewById(R.id.text_mostrar_en_maps);
        escribirResenaTextView = rootView.findViewById(R.id.text_escribir_resena);
        recyclerViewResenas = rootView.findViewById(R.id.recycler_view_resenas);

        imagenesDbHelper = new ImagenesSQLiteHelper(getContext());

        recyclerViewResenas.setLayoutManager(new LinearLayoutManager(getContext()));
        resenasAdapter = new ResenasAdapter(getContext(), new ArrayList<>());
        recyclerViewResenas.setAdapter(resenasAdapter);

        if (getArguments() != null) {
            idPiso = getArguments().getString("id_piso");
            if (idPiso != null) {
                cargarPisoDesdeFirebase(idPiso);
                cargarResenasDelPiso(idPiso);
            }
        }

        escribirResenaTextView.setOnClickListener(v -> {
            if (idPiso != null) {
                Bundle bundle = new Bundle();
                bundle.putString("id_piso", idPiso);
                bundle.putBoolean("mostrar_resena", true);
                Navigation.findNavController(v)
                        .navigate(R.id.action_fragment_detalles_piso_to_fragment_preguntas, bundle);
            }
        });

        mostrarEnMapsTextView.setOnClickListener(v -> {
            String direccion = direccionTextView.getText().toString();
            if (direccion != null && !direccion.isEmpty()) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(direccion));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Log.e("Fragment_detalles_piso", "No se encontró Google Maps");
                }
            }
        });

        return rootView;
    }

    private void cargarPisoDesdeFirebase(String pisoId) {
        DatabaseReference refPiso = FirebaseDatabase.getInstance()
                .getReference("pisos")
                .child(pisoId);

        refPiso.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Database_Piso pisoFirebase = snapshot.getValue(Database_Piso.class);
                    if (pisoFirebase != null) {
                        List<String> imagenesLocales = imagenesDbHelper.obtenerImagenesPorPiso(pisoId);
                        if (imagenesLocales == null) imagenesLocales = new ArrayList<>();

                        pisoActual = new Piso(
                                pisoId,
                                pisoFirebase.getDireccion(),
                                0f,
                                imagenesLocales
                        );
                        pisoActual.setDescripcion(pisoFirebase.getDescripcion());

                        mostrarDatosEnPantalla();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                Log.e("Fragment_detalles_piso", "Error al cargar datos del piso", error.toException());
            }
        });
    }

    private void mostrarDatosEnPantalla() {
        if (pisoActual == null) return;

        direccionTextView.setText(
                pisoActual.getDireccion() != null && !pisoActual.getDireccion().isEmpty()
                        ? pisoActual.getDireccion()
                        : "Sin dirección"
        );

        List<String> imagenes = pisoActual.getImagenesLocal();

        if (imagenes.size() > 1) {
            List<String> imagenesParaMostrar = imagenes.subList(1, imagenes.size());
            ImagenesAdapter adapter = new ImagenesAdapter(imagenesParaMostrar);
            viewPagerImagenes.setAdapter(adapter);
        } else if (imagenes.size() == 1) {
            ImagenesAdapter adapter = new ImagenesAdapter(imagenes);
            viewPagerImagenes.setAdapter(adapter);
        }
    }

    private void cargarResenasDelPiso(String pisoId) {
        databaseManager.escucharResenasPorPiso(pisoId, new DataBase_Manager.ResenasListener() {
            @Override
            public void onResenasChanged(List<DataBase_Puntuacion> resenas) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    resenasAdapter.setResenas(resenas);
                    setRecyclerViewHeightBasedOnChildren(recyclerViewResenas);

                    float suma = 0;
                    for (DataBase_Puntuacion resena : resenas) {
                        suma += resena.getPuntuacion();
                    }

                    float media = resenas.isEmpty() ? 0 : suma / resenas.size();
                    ratingBar.setRating(media);
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e("Fragment_detalles_piso", "Error al cargar reseñas", e);
            }
        });
    }

    private void setRecyclerViewHeightBasedOnChildren(RecyclerView recyclerView) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) return;

        int totalHeight = 0;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            RecyclerView.ViewHolder holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i));
            adapter.bindViewHolder(holder, i);
            View itemView = holder.itemView;

            itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            totalHeight += itemView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = totalHeight + recyclerView.getPaddingTop() + recyclerView.getPaddingBottom();
        recyclerView.setLayoutParams(params);
        recyclerView.requestLayout();
    }

    private class ImagenesAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
        private final List<String> imagenUrls;

        public ImagenesAdapter(List<String> imagenUrls) {
            super(Fragment_detalles_piso.this);
            this.imagenUrls = imagenUrls;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            String imagenUrl = imagenUrls.get(position);
            return Fragment_carrusel.newInstance(imagenUrl);
        }

        @Override
        public int getItemCount() {
            return imagenUrls.size();
        }
    }
}
