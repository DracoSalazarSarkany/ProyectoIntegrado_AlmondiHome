package com.example.interfaces1_2trimestre;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Fragment_login extends Fragment {

    private FirebaseAuth mAuth;
    private EditText editUsuarioOEmail, editContrasena;
    private CheckBox checkboxRecordarContrasena;
    private Button buttonLogin;
    private TextView sicuenta;

    private static final String PREFS_NAME = "prefs_login";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    public Fragment_login() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editUsuarioOEmail = view.findViewById(R.id.edit_usuario_o_email);
        editContrasena = view.findViewById(R.id.edit_contrasena);
        checkboxRecordarContrasena = view.findViewById(R.id.checkbox_recordar_contrasena);
        buttonLogin = view.findViewById(R.id.button_login);
        sicuenta = view.findViewById(R.id.sicuenta);

        // Cargar datos guardados si existen
        cargarDatosGuardados();

        buttonLogin.setOnClickListener(v -> {
            String email = editUsuarioOEmail.getText().toString().trim();
            String pwd = editContrasena.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd)) {
                Toast.makeText(getActivity(), "Email y contraseña obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, pwd);
        });

        sicuenta.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_fragment_content_view_loguin_to_fragment_register);
        });
    }

    private void cargarDatosGuardados() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String email = prefs.getString(KEY_EMAIL, "");
        String password = prefs.getString(KEY_PASSWORD, "");

        if (!email.isEmpty() && !password.isEmpty()) {
            editUsuarioOEmail.setText(email);
            editContrasena.setText(password);
            checkboxRecordarContrasena.setChecked(true);
        }
    }

    private void guardarDatos(String email, String password) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    private void borrarDatos() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_PASSWORD);
        editor.apply();
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String usuarioId = user.getUid();
                            String nombreUsuario = user.getDisplayName();

                            if (nombreUsuario == null || nombreUsuario.isEmpty()) {
                                nombreUsuario = user.getEmail();
                            }

                            // Guardar en clase estática Usuario
                            Usuario.set(usuarioId, nombreUsuario);
                        }

                        // Guardar o borrar datos según checkbox
                        if (checkboxRecordarContrasena.isChecked()) {
                            guardarDatos(email, password);
                        } else {
                            borrarDatos();
                        }

                        Toast.makeText(getActivity(), "Bienvenido: " + (user != null ? user.getEmail() : ""), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(requireActivity(), MainActivity_principal.class);
                        startActivity(intent);
                        requireActivity().finish();

                    } else {
                        Toast.makeText(getActivity(), "Error al iniciar sesión: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
