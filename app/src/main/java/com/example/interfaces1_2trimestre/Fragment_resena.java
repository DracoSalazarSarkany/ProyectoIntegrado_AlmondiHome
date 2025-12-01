package com.example.interfaces1_2trimestre;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class Fragment_resena extends Fragment {

    private String idPiso;
    private float puntuacion;

    private EditText editTextDescription;
    private Button publishTextView;

    private DatabaseReference refUsuarios;

    public Fragment_resena() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            idPiso = getArguments().getString("id_piso");
            puntuacion = getArguments().getFloat("puntuacion", -1f);
        }

        refUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resena, container, false);

        editTextDescription = view.findViewById(R.id.editTextDescription);
        publishTextView = view.findViewById(R.id.buttonResena);

        publishTextView.setOnClickListener(this::publicarResena);

        return view;
    }

    private void publicarResena(View v) {
        if (idPiso == null || puntuacion < 0) return;

        String descripcion = editTextDescription.getText().toString().trim();
        if (descripcion.isEmpty()) return;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String usuarioId = user.getUid();

        refUsuarios.child(usuarioId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String usuarioNombre = "Anónimo";
                if (snapshot.exists()) {
                    DataBase_Usuario usuario = snapshot.getValue(DataBase_Usuario.class);
                    if (usuario != null && usuario.getNombre() != null) {
                        usuarioNombre = usuario.getNombre();
                    }
                }

                // Guardar usuario en sesión
                Usuario_static.startSession(usuarioId, usuarioNombre);

                DatabaseReference refPuntuaciones = FirebaseDatabase.getInstance().getReference("puntuaciones");
                String key = refPuntuaciones.push().getKey();
                if (key == null) return;

                DataBase_Puntuacion nuevaPuntuacion = new DataBase_Puntuacion();
                nuevaPuntuacion.setId(key);
                nuevaPuntuacion.setPisoId(idPiso);
                nuevaPuntuacion.setUsuarioId(usuarioId);
                nuevaPuntuacion.setUsuarioNombre(usuarioNombre);
                nuevaPuntuacion.setPuntuacion(Math.round(puntuacion));
                nuevaPuntuacion.setDescripcion(descripcion);
                nuevaPuntuacion.setTimestamp(System.currentTimeMillis());

                refPuntuaciones.child(key).setValue(nuevaPuntuacion)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Bundle bundle = new Bundle();
                                bundle.putString("id_piso", idPiso);
                                Navigation.findNavController(v).navigate(R.id.action_fragment_resena_to_fragment_detalles_piso, bundle);
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Error silencioso
            }
        });
    }
}
