package com.example.interfaces1_2trimestre;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Fragment_aniadir_piso extends Fragment {

    private ImageView imagenPiso;
    private final List<Uri> imageUriSeleccionadas = new ArrayList<>();

    public Fragment_aniadir_piso() {
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {

                    imageUriSeleccionadas.clear();

                    if (result.getData().getClipData() != null) {
                        int count = result.getData().getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri uri = result.getData().getClipData().getItemAt(i).getUri();
                            imageUriSeleccionadas.add(uri);
                        }
                    } else if (result.getData().getData() != null) {
                        Uri uri = result.getData().getData();
                        imageUriSeleccionadas.add(uri);
                    }

                    try {
                        if (!imageUriSeleccionadas.isEmpty()) {
                            Bitmap bitmap = decodeSampledBitmapFromUri(imageUriSeleccionadas.get(0), 1024, 1024);
                            imagenPiso.setImageBitmap(bitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aniadir_piso, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText ciudadField = view.findViewById(R.id.editTextText2);
        EditText direccionField = view.findViewById(R.id.editTextText3);
        EditText numeroField = view.findViewById(R.id.editTextText4);
        EditText codigoPostalField = view.findViewById(R.id.editTextText5);
        Button botonContinuar = view.findViewById(R.id.boton_continuar);
        imagenPiso = view.findViewById(R.id.aniadirimagen);

        imagenPiso.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            imagePickerLauncher.launch(Intent.createChooser(intent, "Selecciona imágenes"));
        });

        botonContinuar.setOnClickListener(v -> {
            String ciudad = ciudadField.getText().toString().trim();
            String direccion = direccionField.getText().toString().trim();
            String numero = numeroField.getText().toString().trim();
            String codigoPostal = codigoPostalField.getText().toString().trim();

            if (ciudad.isEmpty() || direccion.isEmpty() || numero.isEmpty()
                    || codigoPostal.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUriSeleccionadas.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, selecciona al menos una imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            String direccionCompleta = ciudad + ", " + direccion + " " + numero;

            Gestorpiso gestor = new Gestorpiso();
            ImagenesSQLiteHelper imagenesDbHelper = new ImagenesSQLiteHelper(getContext());

            List<String> rutasImagenesLocales = new ArrayList<>();
            for (Uri uriImagen : imageUriSeleccionadas) {
                String ruta = copiarImagenAlmacenamientoInterno(uriImagen);
                if (ruta != null) {
                    rutasImagenesLocales.add(ruta);
                }
            }

            gestor.registrarNuevoPisoConCallback(
                    direccionCompleta,
                    codigoPostal,
                    0.0,
                    "test",
                    0,
                    new Gestorpiso.PisoCallback() {
                        @Override
                        public void onSuccess(String pisoId) {
                            for (String rutaImagen : rutasImagenesLocales) {
                                imagenesDbHelper.insertarImagen(pisoId, rutaImagen);
                            }

                            if (getActivity() == null) return;
                            getActivity().runOnUiThread(() -> {
                                NavController navController = Navigation.findNavController(view);
                                Bundle bundle = new Bundle();
                                bundle.putString("id_piso", pisoId);
                                navController.navigate(R.id.action_fragment_aniadir_piso_to_fragment_preguntas, bundle);
                            });
                        }

                        @Override
                        public void onFailure(Exception e) {
                            if (getActivity() == null) return;
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Error al añadir piso: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                        }
                    },
                    imagenesDbHelper,
                    rutasImagenesLocales.isEmpty() ? null : rutasImagenesLocales.get(0)
            );
        });
    }

    private Bitmap decodeSampledBitmapFromUri(Uri imageUri, int reqWidth, int reqHeight) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        InputStream input = getActivity().getContentResolver().openInputStream(imageUri);
        BitmapFactory.decodeStream(input, null, options);
        input.close();

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        input = getActivity().getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
        input.close();

        return bitmap;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private String copiarImagenAlmacenamientoInterno(Uri uriImagen) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uriImagen);

            String nombreArchivo = "imagen_" + System.currentTimeMillis() + ".jpg";

            File carpeta = getContext().getFilesDir();
            File archivoDestino = new File(carpeta, nombreArchivo);

            OutputStream outputStream = new FileOutputStream(archivoDestino);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return archivoDestino.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
