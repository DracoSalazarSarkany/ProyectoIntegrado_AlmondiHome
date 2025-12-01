package com.example.interfaces1_2trimestre;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class MainActivity_principal extends AppCompatActivity {

    public ImageView imageView3;
    public ImageView imageView2;
    public TextView textViewAlmondiHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Guardar idioma español por defecto si aún no se ha guardado
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        if (!prefs.contains("idioma")) {
            prefs.edit().putString("idioma", "es").apply();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MAIN_LIFE", "▶ onCreate arrancado");

        imageView3 = findViewById(R.id.imageView3);
        imageView2 = findViewById(R.id.imageView2);
        textViewAlmondiHome = findViewById(R.id.textViewAlmondiHome);

        // Obtener el NavController desde el FragmentContainerView
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_graph_principal);
        NavController navController = navHostFragment.getNavController();

        // Listener del botón de perfil
        imageView3.setOnClickListener(v -> {
            navController.navigate(R.id.fragment_usuario_perfil);
        });
        imageView2.setOnClickListener(v -> {
            navController.navigate(R.id.fragment_ajustes);
        });
        textViewAlmondiHome.setOnClickListener(v -> {
            navController.navigate(R.id.fragment_pagina_principal);
        });

        // Carga inicial de la imagen de perfil
        actualizarImagenPerfil();
    }

    public void actualizarImagenPerfil() {
        ImagenesSQLiteHelper dbHelper = new ImagenesSQLiteHelper(this);
        List<String> imagenes = dbHelper.obtenerImagenesPorPiso(Usuario.getId());
        if (!imagenes.isEmpty()) {
            File imgFile = new File(imagenes.get(0));
            if (imgFile.exists()) {
                imageView3.setImageURI(Uri.fromFile(imgFile));
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String lang = prefs.getString("idioma", "es"); // "es" por defecto
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        super.attachBaseContext(newBase.createConfigurationContext(config));
    }
}
