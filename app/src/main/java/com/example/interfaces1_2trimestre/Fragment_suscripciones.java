package com.example.interfaces1_2trimestre;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Fragment_suscripciones extends Fragment {

    public Fragment_suscripciones() {
        // Constructor vacío requerido
    }

    public static Fragment_suscripciones newInstance(String param1, String param2) {
        return new Fragment_suscripciones();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.suscripciones, container, false);

        // Configurar botones de suscripción
        Button btnPlata = view.findViewById(R.id.btnPlata);
        Button btnOro = view.findViewById(R.id.btnOro);
        Button btnDiamante = view.findViewById(R.id.btnDiamante);

        // Abrir enlace de pago al pulsar cada botón
        btnPlata.setOnClickListener(v -> abrirEnlace("https://tuweb.com/pago/plata"));
        btnOro.setOnClickListener(v -> abrirEnlace("https://tuweb.com/pago/oro"));
        btnDiamante.setOnClickListener(v -> abrirEnlace("https://tuweb.com/pago/diamante"));

        return view;
    }

    // Método para abrir un enlace web
    private void abrirEnlace(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
