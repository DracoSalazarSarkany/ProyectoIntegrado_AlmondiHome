package com.example.interfaces1_2trimestre;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import java.io.File;

public class Fragment_carrusel extends Fragment {

    private static final String ARG_IMAGEN_URL = "imagen_url";
    private ImageView imageView;

    public Fragment_carrusel() {}

    public static Fragment_carrusel newInstance(String imagenUrl) {
        Fragment_carrusel fragment = new Fragment_carrusel();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGEN_URL, imagenUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_carrusel, container, false);
        imageView = rootView.findViewById(R.id.image_piso);

        String imagenUrl = getArguments() != null ? getArguments().getString(ARG_IMAGEN_URL) : null;

        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            new CargarImagenTask().execute(imagenUrl);
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        return rootView;
    }

    private class CargarImagenTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            String path = strings[0];
            File file = new File(path);
            if (!file.exists()) {
                Log.e("Fragment_carrusel", "Archivo NO encontrado: " + path);
                return null;
            }
            Log.d("Fragment_carrusel", "Archivo encontrado: " + path);
            return decodeSampledBitmapFromFile(path, 800, 800);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(android.R.drawable.ic_menu_report_image);
            }
        }
    }

    private Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight &&
                    (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
