package com.example.tfg.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.Chat;
import com.example.tfg.Modelos.Persona;
import com.example.tfg.R;
import com.example.tfg.medico.informes.GraficosMedico;

import java.util.ArrayList;

public class ListaPersonasAdapter extends RecyclerView.Adapter<ListaPersonasAdapter.PacienteViewHolder> {
     Context context;
     ArrayList<Persona> arrayList;
     LayoutInflater layoutInflater;
     String origen;
     String id;
     String chatKey;
     boolean t;
     public void setFilterList(ArrayList<Persona>filteredList){
         this.arrayList = filteredList;
         notifyDataSetChanged();
     }

     public ListaPersonasAdapter(Context context, ArrayList<Persona> arrayList, String origen, String id,boolean t){
         this.context = context;
         this.arrayList = arrayList;
         layoutInflater = LayoutInflater.from(context);
         this.origen = origen;
         this.id = id;
         this.t=t;
     }
    @NonNull
    @Override
    public PacienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_medico, parent, false);
        return new PacienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PacienteViewHolder holder, int position) {
        holder.username.setText(arrayList.get(position).getNombre());
        holder.userimagen.setImageResource(arrayList.get(position).getimg());
        holder.itemView.setOnClickListener(v -> {
            if (origen.equals("Lista")) {
                Intent intent = new Intent(context, GraficosMedico.class);
                intent.putExtra("idPaciente", arrayList.get(position).getId());
                intent.putExtra("nombrePaciente", arrayList.get(position).getNombre());
                context.startActivity(intent);
            } else if (origen.equals("Chat")) {
                Intent intent = new Intent(context, Chat.class); // ← Reemplaza con tu clase real
                intent.putExtra("receptor", arrayList.get(position).getId());
                intent.putExtra("nombrereceptor", arrayList.get(position).getNombre());
                intent.putExtra("emisor",id);
                if(t){
                    chatKey = arrayList.get(position).getId() + "_" +id;
                }else{
                    chatKey = id + "_" + arrayList.get(position).getId();
                }

                intent.putExtra("chatKey", chatKey);

                context.startActivity(intent);
            } else if (origen.equals("Home")) {
                // No hacer nada
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class PacienteViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        ImageView userimagen;


        public PacienteViewHolder(@NonNull View itemView) {
            super(itemView);
           userimagen= itemView.findViewById(R.id.imagenview3);
            username = itemView.findViewById(R.id.nl);
        }


    }


}
