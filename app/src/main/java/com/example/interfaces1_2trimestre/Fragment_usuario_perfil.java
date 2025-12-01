package com.example.interfaces1_2trimestre;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.List;

public class Fragment_usuario_perfil extends Fragment {

    private ImageView profileImage;
    private ImagenesSQLiteHelper dbHelper;

    public Fragment_usuario_perfil() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuario_perfil, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        TextView addFlat = view.findViewById(R.id.add_flat_button);
        TextView subscriptions = view.findViewById(R.id.subscriptions_button);
        TextView settings = view.findViewById(R.id.settings_button);
        TextView editProfile = view.findViewById(R.id.edit_profile_button);
        TextView logout = view.findViewById(R.id.logout_button);

        dbHelper = new ImagenesSQLiteHelper(requireContext());

        // Cargar imagen existente si ya está guardada
        cargarImagenDePerfil();

        // Quitamos el listener que abría selector de archivos
        // profileImage.setOnClickListener(v -> { ... });

        addFlat.setOnClickListener(v -> Navigation.findNavController(view)
                .navigate(R.id.action_fragment_usuario_perfil_to_Fragment_aniadir_piso));

        subscriptions.setOnClickListener(v -> Navigation.findNavController(view)
                .navigate(R.id.action_fragment_usuario_perfil_to_Fragment_suscripciones));

        settings.setOnClickListener(v -> Navigation.findNavController(view)
                .navigate(R.id.action_fragment_usuario_perfil_to_Fragment_ajustes));

        editProfile.setOnClickListener(v -> Navigation.findNavController(view)
                .navigate(R.id.action_fragment_usuario_perfil_to_Fragment_editar_perfil));

        logout.setOnClickListener(v -> {
            // Cerrar sesión Firebase
            FirebaseAuth.getInstance().signOut();

            // Lanzar la actividad MainActivity_register
            Intent intent = new Intent(requireContext(), MainActivity_register.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Finalizar la actividad actual para evitar volver atrás
            requireActivity().finish();
        });


        return view;
    }

    private void cargarImagenDePerfil() {
        List<String> imagenes = dbHelper.obtenerImagenesPorPiso(Usuario.getId());
        if (!imagenes.isEmpty()) {
            String ruta = imagenes.get(0);
            File file = new File(ruta);
            if (file.exists()) {
                Bitmap bitmap = decodeSampledBitmapFromFile(ruta, 300, 300);
                profileImage.setImageBitmap(bitmap);
            }
        }
    }

    // Método para escalar imagen y evitar errores de memoria
    private Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
