package com.example.interfaces1_2trimestre;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Fragment_formulario_registro extends Fragment {

    private EditText editUsuario, editNombreCompleto, editEmail, editLocalidad, editPais;
    private String usuarioId;

    public Fragment_formulario_registro() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_formulario_registro, container, false);

        // Referencias de los campos del formulario
        editUsuario = view.findViewById(R.id.edit_usuario);
        editNombreCompleto = view.findViewById(R.id.edit_nombre_completo);
        editEmail = view.findViewById(R.id.edit_email);
        editLocalidad = view.findViewById(R.id.edit_localidad);
        editPais = view.findViewById(R.id.edit_pais);
        Button btnRegistrar = view.findViewById(R.id.button_registrar);

        // Recibir el usuarioId desde el Bundle
        if (getArguments() != null) {
            usuarioId = getArguments().getString("usuarioId");
        }

        btnRegistrar.setOnClickListener(v -> {
            if (usuarioId == null) {
                Toast.makeText(requireContext(), "Error: ID de usuario no disponible", Toast.LENGTH_SHORT).show();
                return;
            }

            String usuario = editUsuario.getText().toString().trim();
            String nombreCompleto = editNombreCompleto.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String localidad = editLocalidad.getText().toString().trim();
            String pais = editPais.getText().toString().trim();

            if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(nombreCompleto) || TextUtils.isEmpty(email)) {
                Toast.makeText(requireContext(), "Por favor, completa los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardar en Firebase
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios");

            // Crear instancia de usuario base
            DataBase_Usuario usuarioObj = new DataBase_Usuario();
            usuarioObj.setId(usuarioId);
            usuarioObj.setNombre(nombreCompleto);

            // Guardar estructura base
            ref.child(usuarioId).setValue(usuarioObj)
                    .addOnSuccessListener(aVoid -> {
                        // Guardar campos extra por separado
                        Map<String, Object> datosExtra = new HashMap<>();
                        datosExtra.put("usuario", usuario);
                        datosExtra.put("email", email);
                        datosExtra.put("ciudad", localidad);
                        datosExtra.put("pais", pais);

                        ref.child(usuarioId).updateChildren(datosExtra)
                                .addOnSuccessListener(aVoid2 -> {
                                    Toast.makeText(requireContext(), "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(view).navigate(R.id.action_fragment_formulario_registro_to_fragment_content_view_loguin);
                                })
                                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error al guardar datos extra", Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error al registrar usuario", Toast.LENGTH_SHORT).show());
        });

        return view;
    }
}
