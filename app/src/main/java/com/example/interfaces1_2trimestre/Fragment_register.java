package com.example.interfaces1_2trimestre;

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
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Fragment_register extends Fragment {

    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private CheckBox termsCheckBox;

    public Fragment_register() {
        // Constructor vacío requerido
    }

    public static Fragment_register newInstance(String param1, String param2) {
        Fragment_register fragment = new Fragment_register();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("param1");
            String mParam2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        confirmPasswordEditText = view.findViewById(R.id.confirm_password);
        termsCheckBox = view.findViewById(R.id.terms_and_conditions);
        Button button_registrar = view.findViewById(R.id.button_registrar);
        TextView sincuentanueva = view.findViewById(R.id.sincuentanueva);

        sincuentanueva.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_fragment_register_to_fragment_content_view_loguin);
        });

        button_registrar.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (!termsCheckBox.isChecked()) {
                Toast.makeText(getActivity(), "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(getActivity(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(getActivity(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(email, password, view);
        });

        return view;
    }

    private void registerUser(String email, String password, View view) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            Toast.makeText(getActivity(), "Usuario registrado: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                            // Crear Bundle y pasar el ID
                            Bundle bundle = new Bundle();
                            bundle.putString("usuarioId", uid);

                            NavController navController = Navigation.findNavController(view);
                            navController.navigate(R.id.action_fragment_register_to_fragment_formulario_registro, bundle);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
