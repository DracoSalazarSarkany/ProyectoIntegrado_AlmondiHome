package com.example.interfaces1_2trimestre;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.Locale;

public class Fragment_ajustes extends Fragment {

    public CardView favoritos;
    public CardView ayuda;
    public CardView idioma;
    public CardView notificaciones;

    public Fragment_ajustes() {
        // Constructor vacío requerido
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Si hay parámetros pasados al fragmento, los recoges aquí
        if (getArguments() != null) {
            // Puedes guardar los argumentos si los necesitas
        }
    }

    private void cambiarIdioma(String codigoIdioma) {
        // Guardar idioma en SharedPreferences
        requireActivity()
                .getSharedPreferences("prefs", Context.MODE_PRIVATE)
                .edit()
                .putString("idioma", codigoIdioma)
                .apply();

        // Aplicar configuración
        Locale locale = new Locale(codigoIdioma);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        requireActivity().getResources().updateConfiguration(config, requireActivity().getResources().getDisplayMetrics());

        // Reiniciar actividad principal
        Intent intent = new Intent(requireContext(), MainActivity_principal.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void mostrarDialogoIdiomas() {
        String[] idiomas = {"Español", "English", "Deutsch", "Français"};
        String[] codigos = {"es", "en", "de", "fr"};

        new AlertDialog.Builder(getContext())
                .setTitle("Seleccionar idioma")
                .setItems(idiomas, (dialog, which) -> cambiarIdioma(codigos[which]))
                .show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ajustes, container, false);

        favoritos = view.findViewById(R.id.favoritos);
        ayuda = view.findViewById(R.id.ayuda);
        idioma = view.findViewById(R.id.idioma);
        notificaciones = view.findViewById(R.id.notificaciones);

        favoritos.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_fragment_ajustes_to_fragment_favoritos);
        });

        ayuda.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_fragment_ajustes_to_fragment_chat);
        });

        idioma.setOnClickListener(v -> mostrarDialogoIdiomas());

        notificaciones.setOnClickListener(v -> {
            Toast.makeText(getContext(), "En desarrollo", Toast.LENGTH_SHORT).show();
            Log.d("AJUSTES", "En desarrollo");
        });

        return view;
    }
}
