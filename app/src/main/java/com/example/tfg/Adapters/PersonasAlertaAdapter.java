package com.example.tfg.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.Modelos.PersonasAlerta;
import com.example.tfg.R;

import java.util.ArrayList;

public class PersonasAlertaAdapter extends RecyclerView.Adapter<PersonasAlertaAdapter.PersonasAlertaViewHolder> {

    Context context;
    ArrayList<PersonasAlerta> arrayList;
    LayoutInflater layoutInflater;

    public PersonasAlertaAdapter(Context context, ArrayList<PersonasAlerta> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PersonasAlertaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_alerta, parent, false);
        return new PersonasAlertaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonasAlertaViewHolder holder, int position) {
        PersonasAlerta persona = arrayList.get(position);
        holder.username.setText(persona.getNombre());
        holder.glucemia.setText(String.valueOf(persona.getGlucemia()));
        holder.userimagen.setImageResource(persona.getimg());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class PersonasAlertaViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        TextView glucemia;
        ImageView userimagen;

        public PersonasAlertaViewHolder(@NonNull View itemView) {
            super(itemView);
            userimagen = itemView.findViewById(R.id.imagenalerta);
            username = itemView.findViewById(R.id.nl);
            glucemia = itemView.findViewById(R.id.glucemia);
        }
    }
}