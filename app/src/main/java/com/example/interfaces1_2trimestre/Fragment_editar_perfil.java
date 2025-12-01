package com.example.interfaces1_2trimestre;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class Fragment_editar_perfil extends Fragment {

    private static final int REQUEST_CODE_PICK_IMAGE = 2001;

    private ImageView profileImage;
    private ImagenesSQLiteHelper dbHelper;

    private TextView nombrebd, ciudadbd;
    private EditText nameEditText, cityEditText, websiteEditText, aboutEditText;
    private Button guardarButton;

    public Fragment_editar_perfil() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_perfil, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        dbHelper = new ImagenesSQLiteHelper(requireContext());

        nombrebd = view.findViewById(R.id.nombrebd);
        ciudadbd = view.findViewById(R.id.ciudadbd);

        nameEditText = view.findViewById(R.id.name_edittext);
        cityEditText = view.findViewById(R.id.city_edittext);
        websiteEditText = view.findViewById(R.id.website_edittext);
        aboutEditText = view.findViewById(R.id.about_edittext);
        guardarButton = view.findViewById(R.id.guardar_button);

        // Inicializar tag para controlar si el campo fue editado ya
        nameEditText.setTag("");
        cityEditText.setTag("");
        websiteEditText.setTag("");
        aboutEditText.setTag("");

        // Setup para borrar texto al hacer foco si no ha sido editado antes
        setupClearOnFocus(nameEditText);
        setupClearOnFocus(cityEditText);
        setupClearOnFocus(websiteEditText);
        setupClearOnFocus(aboutEditText);

        cargarDatosUsuario();

        // Cargar imagen actual si existe
        List<String> imagenes = dbHelper.obtenerImagenesPorPiso(Usuario.getId());
        if (!imagenes.isEmpty()) {
            File imgFile = new File(imagenes.get(0));
            if (imgFile.exists()) {
                profileImage.setImageURI(Uri.fromFile(imgFile));
            }
        }

        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), REQUEST_CODE_PICK_IMAGE);
        });

        guardarButton.setOnClickListener(v -> {
            guardarDatosUsuario();
        });

        return view;
    }

    private void setupClearOnFocus(EditText editText) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (!"edited".equals(editText.getTag())) {
                    editText.setText("");
                    editText.setTag("edited");
                }
            }
        });
    }

    private void cargarDatosUsuario() {
        String usuarioId = Usuario.getId();
        if (usuarioId == null) {
            Toast.makeText(requireContext(), "Usuario no logueado", Toast.LENGTH_SHORT).show();
            return;
        }

        DataBase_Manager.getInstance().leerUsuario(usuarioId, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DataBase_Usuario usuario = snapshot.getValue(DataBase_Usuario.class);
                if (usuario != null) {
                    // Solo rellenar EditText con los valores reales
                    nameEditText.setText(usuario.getNombre());
                    nameEditText.setTag("edited");

                    String ciudad = snapshot.hasChild("ciudad") ? snapshot.child("ciudad").getValue(String.class) : "";
                    cityEditText.setText(ciudad);
                    cityEditText.setTag("edited");

                    String website = snapshot.hasChild("website") ? snapshot.child("website").getValue(String.class) : "";
                    websiteEditText.setText(website != null ? website : "");
                    websiteEditText.setTag("edited");

                    String about = snapshot.hasChild("about") ? snapshot.child("about").getValue(String.class) : "";
                    aboutEditText.setText(about != null ? about : "");
                    aboutEditText.setTag("edited");
                } else {
                    Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(requireContext(), "Error al cargar usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarDatosUsuario() {
        String usuarioId = Usuario.getId();
        if (usuarioId == null) {
            Toast.makeText(requireContext(), "Usuario no logueado", Toast.LENGTH_SHORT).show();
            return;
        }

        String nuevoNombre = nameEditText.getText().toString().trim();
        String nuevaCiudad = cityEditText.getText().toString().trim();
        String nuevoWebsite = websiteEditText.getText().toString().trim();
        String nuevoAbout = aboutEditText.getText().toString().trim();

        if (nuevoNombre.isEmpty()) {
            Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar en Firebase
        DataBase_Manager.getInstance().db.child("usuarios").child(usuarioId).child("nombre").setValue(nuevoNombre);
        DataBase_Manager.getInstance().db.child("usuarios").child(usuarioId).child("ciudad").setValue(nuevaCiudad);
        DataBase_Manager.getInstance().db.child("usuarios").child(usuarioId).child("website").setValue(nuevoWebsite);
        DataBase_Manager.getInstance().db.child("usuarios").child(usuarioId).child("about").setValue(nuevoAbout);

        Toast.makeText(requireContext(), "Datos guardados correctamente", Toast.LENGTH_SHORT).show();

        // Actualizar TextViews con los nuevos datos
        nombrebd.setText("Nombre: " + nuevoNombre);
        ciudadbd.setText("Ciudad: " + (nuevaCiudad.isEmpty() ? "No definida" : nuevaCiudad));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                String rutaGuardada = guardarImagenLocal(imageUri);
                if (rutaGuardada != null) {
                    dbHelper.borrarImagenesPorPiso(Usuario.getId());
                    dbHelper.insertarImagen(Usuario.getId(), rutaGuardada);
                    profileImage.setImageURI(Uri.fromFile(new File(rutaGuardada)));
                    Toast.makeText(requireContext(), "Imagen actualizada", Toast.LENGTH_SHORT).show();

                    // Actualizar imagen en la actividad principal
                    if (getActivity() instanceof MainActivity_principal) {
                        ((MainActivity_principal) getActivity()).actualizarImagenPerfil();
                    }
                }
            }
        }
    }

    private String guardarImagenLocal(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            String fileName = getFileNameFromUri(uri);

            File dir = requireContext().getFilesDir();
            File imageFile = new File(dir, fileName);

            OutputStream outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[1024];
            int len;

            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            inputStream.close();
            outputStream.close();

            return imageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}
