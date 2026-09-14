package com.example.tfg.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;

import java.util.List;

public class HorasAdapter extends RecyclerView.Adapter<HorasAdapter.HoraViewHolder> {

    private List<String> lista;
    private List<String> ocupadas;
    private OnHoraClickListener listener;

    public interface OnHoraClickListener {
        void onHoraClick(String hora);
    }

    public HorasAdapter(List<String> lista, List<String> ocupadas, OnHoraClickListener listener) {
        this.lista = lista;
        this.ocupadas = ocupadas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HoraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hora, parent, false);
        return new HoraViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull HoraViewHolder holder, int position) {
        String hora = lista.get(position);
        holder.textHora.setText(hora);
        if (ocupadas.contains(hora)) {
            holder.itemView.setEnabled(false);
            holder.textHora.setTextColor(Color.GRAY);
            holder.itemView.setBackgroundResource(R.drawable.bg_disabled);
        } else {
            holder.itemView.setEnabled(true);
            holder.textHora.setTextColor(Color.BLACK);
            holder.itemView.setBackgroundResource(R.drawable.bg_enabled);
            holder.itemView.setOnClickListener(v -> listener.onHoraClick(hora));
        }
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class HoraViewHolder extends RecyclerView.ViewHolder {
        TextView textHora;

        HoraViewHolder(@NonNull View itemView) {
            super(itemView);
            textHora = itemView.findViewById(R.id.textHora);
        }
    }
}