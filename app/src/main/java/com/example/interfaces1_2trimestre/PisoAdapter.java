package com.example.interfaces1_2trimestre;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class PisoAdapter extends RecyclerView.Adapter<PisoAdapter.PisoViewHolder> {

    private List<Piso> listaPisos;
    private Context context;
    private OnPisoClickListener listener;
    private OnFavoritoClickListener favoritoListener;

    public interface OnPisoClickListener {
        void onPisoClick(Piso piso);
    }

    public interface OnFavoritoClickListener {
        void onFavoritoClick(Piso piso);
    }

    public PisoAdapter(List<Piso> listaPisos, Context context, OnPisoClickListener listener, OnFavoritoClickListener favoritoListener) {
        this.listaPisos = listaPisos;
        this.context = context;
        this.listener = listener;
        this.favoritoListener = favoritoListener;
    }

    @NonNull
    @Override
    public PisoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_piso, parent, false);
        return new PisoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PisoViewHolder holder, int position) {
        Piso piso = listaPisos.get(position);

        holder.textDireccion.setText(piso.getDireccion());

        // Aquí asumimos que el fragmento ya ha cargado y seteado la media en el objeto Piso
        holder.ratingBar.setRating(piso.getRating());

        // Cargar imagen
        if (piso.getImagenesLocal() != null && !piso.getImagenesLocal().isEmpty()) {
            String rutaImagen = piso.getImagenesLocal().get(0);
            File imgFile = new File(rutaImagen);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.imageView.setImageBitmap(bitmap);
            } else {
                holder.imageView.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        }

        // Mostrar favorito
        if (piso.isFavorito()) {
            holder.iconFavorito.setImageResource(R.drawable.ic_favorito_selectorsi);
        } else {
            holder.iconFavorito.setImageResource(R.drawable.ic_favorito_selector);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPisoClick(piso);
        });

        holder.iconFavorito.setOnClickListener(v -> {
            if (favoritoListener != null) favoritoListener.onFavoritoClick(piso);
        });
    }

    @Override
    public int getItemCount() {
        return listaPisos.size();
    }

    public static class PisoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textDireccion;
        RatingBar ratingBar;
        ImageView iconFavorito;

        public PisoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_piso);
            textDireccion = itemView.findViewById(R.id.text_direccion_piso);
            ratingBar = itemView.findViewById(R.id.rating_bar_piso_principal);
            iconFavorito = itemView.findViewById(R.id.icon_favorito);
        }
    }

    public void actualizarLista(List<Piso> nuevosPisos) {
        listaPisos = nuevosPisos;
        notifyDataSetChanged();
    }
}
