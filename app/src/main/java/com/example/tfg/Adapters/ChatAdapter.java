package com.example.tfg.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.Modelos.Mensaje;
import com.example.tfg.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{
    Context context;
    ArrayList<Mensaje> mensajes;
    String idUsuario;

    public ChatAdapter(Context context, ArrayList<Mensaje> mensajes, String idUsuario) {
        this.context = context;
        this.mensajes = mensajes;
        this.idUsuario = idUsuario;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);

        if (mensaje.getEmisor().equals(idUsuario)) {
            holder.layoutC.setGravity(Gravity.END);
           holder.mensaje.setText( mensaje.getMensaje());
           holder.mensaje.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DDDDDD")));


        } else {
            holder.layoutC.setGravity(Gravity.START);
            holder.mensaje.setText(mensaje.getMensaje());

        }

        if (mensaje.getTimestamp() != null) {
            String fecha = new SimpleDateFormat("HH:mm").format(mensaje.getTimestamp().toDate());
            holder.hora.setText(fecha);
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView mensaje, hora;
        LinearLayout layoutC;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            mensaje = itemView.findViewById(R.id.textMensaje);
            hora = itemView.findViewById(R.id.textHoras);
            layoutC = itemView.findViewById(R.id.layoutChats);
        }
    }
}
