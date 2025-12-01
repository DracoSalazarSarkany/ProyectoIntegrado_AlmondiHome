package com.example.interfaces1_2trimestre;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class Fragment_preguntas extends Fragment {

    private Button btn_continuar;
    private String pisoId;

    public Fragment_preguntas() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preguntas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            pisoId = getArguments().getString("id_piso");
        }

        btn_continuar = view.findViewById(R.id.btn_continuar);

        btn_continuar.setOnClickListener(v -> {
            Switch[] preguntas = {
                    view.findViewById(R.id.switch_paredes_techos),
                    view.findViewById(R.id.switch_suelos),
                    view.findViewById(R.id.switch_ventanas_puertas),
                    view.findViewById(R.id.switch_aislamiento),
                    view.findViewById(R.id.switch_pintura),
                    view.findViewById(R.id.switch_electricidad),
                    view.findViewById(R.id.switch_agua),
                    view.findViewById(R.id.switch_gas),
                    view.findViewById(R.id.switch_electrodomesticos),
                    view.findViewById(R.id.switch_calefaccion_aa),
                    view.findViewById(R.id.switch_muebles),
                    view.findViewById(R.id.switch_colchones_sofas),
                    view.findViewById(R.id.switch_portal),
                    view.findViewById(R.id.switch_ascensor),
                    view.findViewById(R.id.switch_trastero_garaje),
                    view.findViewById(R.id.switch_buzon),
                    view.findViewById(R.id.switch_contrato),
                    view.findViewById(R.id.switch_gastos),
                    view.findViewById(R.id.switch_certificado),
                    view.findViewById(R.id.switch_normas),
                    view.findViewById(R.id.switch_empadronamiento)
            };

            int totalActivados = 0;
            for (Switch pregunta : preguntas) {
                if (pregunta != null && pregunta.isChecked()) {
                    totalActivados++;
                }
            }

            int totalPreguntas = preguntas.length;
            float proporcion = (float) totalActivados / totalPreguntas;
            float puntuacion = Math.round(proporcion * 10) / 2.0f;

            if (pisoId == null) {
                Toast.makeText(getContext(), "Error: no se encontró el piso", Toast.LENGTH_LONG).show();
                return;
            }

            NavController navController = Navigation.findNavController(view);
            Bundle bundle = new Bundle();
            bundle.putString("id_piso", pisoId);
            bundle.putFloat("puntuacion", puntuacion);

            navController.navigate(R.id.action_fragment_preguntas_to_fragment_resena, bundle);
        });
    }
}
