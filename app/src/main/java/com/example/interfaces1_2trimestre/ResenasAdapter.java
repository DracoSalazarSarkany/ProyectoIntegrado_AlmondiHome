package com.example.interfaces1_2trimestre;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ResenasAdapter extends RecyclerView.Adapter<ResenasAdapter.ViewHolder> {

    private List<DataBase_Puntuacion> resenas = new ArrayList<>();
    private Context context;

    public ResenasAdapter(Context context, List<DataBase_Puntuacion> resenas) {
        this.context = context;
        if (resenas != null) {
            this.resenas = resenas;
        }
    }

    @NonNull
    @Override
    public ResenasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_resena, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ResenasAdapter.ViewHolder holder, int position) {
        DataBase_Puntuacion resena = resenas.get(position);
        holder.ratingBar.setRating(resena.getPuntuacion());
        holder.txtDescripcion.setText(resena.getDescripcion());
        holder.txtNombreUsuario.setText(resena.getUsuarioNombre());  // Mostrar nombre del usuario
    }

    @Override
    public int getItemCount() {
        return resenas.size();
    }

    public void setResenas(List<DataBase_Puntuacion> nuevasResenas) {
        resenas.clear();
        resenas.addAll(nuevasResenas);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RatingBar ratingBar;
        TextView txtDescripcion;
        TextView txtNombreUsuario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.rating_bar_usuario);
            txtDescripcion = itemView.findViewById(R.id.text_resena_usuario);
            txtNombreUsuario = itemView.findViewById(R.id.text_nombre_usuario);
        }
    }
}
