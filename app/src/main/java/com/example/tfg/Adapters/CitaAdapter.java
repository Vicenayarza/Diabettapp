package com.example.tfg.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.Modelos.Cita;
import com.example.tfg.R;

import java.util.ArrayList;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.CitaViewHolder> {
    Context context;
    ArrayList<Cita> arrayList;
    LayoutInflater layoutInflater;
    public CitaAdapter(Context context,ArrayList<Cita> arrayList){
        this.context = context;
        this.arrayList = arrayList;
        layoutInflater = LayoutInflater.from(context);
    } @NonNull
    @Override
    public CitaAdapter.CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.cita_item, parent, false);
        return new CitaAdapter.CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaAdapter.CitaViewHolder holder, int position) {
        holder.username.setText(arrayList.get(position).getNombre());
        holder.userfecha.setText(arrayList.get(position).getFecha());
        holder.userimagen.setImageResource(arrayList.get(position).getimg());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class CitaViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        TextView userfecha;
        ImageView userimagen;


        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            userimagen= itemView.findViewById(R.id.imageViewCitas);
            username = itemView.findViewById(R.id.nombrecita);
            userfecha = itemView.findViewById(R.id.FechaHora);
        }


    }


}