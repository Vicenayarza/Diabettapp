package com.example.tfg;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tfg.Adapters.ChatAdapter;
import com.example.tfg.Modelos.Mensaje;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class Chat extends AppCompatActivity {
    private String emisor, nombrereceptor, receptor, chatKey;
    private TextView nombreTextView;
    private EditText messageInput;
    private ImageView sendButton;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    LottieAnimationView lf;
    private ArrayList<Mensaje> mensajes;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        emisor = getIntent().getStringExtra("emisor");
        receptor = getIntent().getStringExtra("receptor");
        nombrereceptor = getIntent().getStringExtra("nombrereceptor");
        chatKey = getIntent().getStringExtra("chatKey");
        lf=findViewById(R.id.atrasIA);

        nombreTextView = findViewById(R.id.other_username);
        messageInput = findViewById(R.id.chat_message_input);
        sendButton = findViewById(R.id.message_send_btn);
        recyclerView = findViewById(R.id.chat_recycler_view);

        nombreTextView.setText(nombrereceptor);

        db = FirebaseFirestore.getInstance();

        mensajes = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, mensajes, emisor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        cargarMensajes();
        lf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageInput.getText().toString().trim();
                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(Chat.this, "Mensaje vacío", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> mensaje = new HashMap<>();
                mensaje.put("emisor", emisor);
                mensaje.put("receptor", receptor);
                mensaje.put("mensaje", msg);
                mensaje.put("timestamp", new Timestamp(new Date()));

                db.collection("chats").document(chatKey)
                        .collection("mensajes")
                        .add(mensaje);
                actualizarChat(emisor);
               // enviarNotificacion(idPaciente,"Nuevo mensaje", msg);

                messageInput.setText("");
            }
        });
    }
    private void cargarMensajes() {
        db.collection("chats").document(chatKey)
                .collection("mensajes")
                .orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("FirestoreChat", "Listen failed.", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                Mensaje mensaje = dc.getDocument().toObject(Mensaje.class);
                                mensajes.add(mensaje);
                                chatAdapter.notifyItemInserted(mensajes.size() - 1);
                                recyclerView.scrollToPosition(mensajes.size() - 1);
                            }
                        }
                    }
                });
    }
    /*private void enviarNotificacion(String idReceptor, String titulo, String cuerpo) {
        FirebaseFirestore.getInstance().collection("tokens")
                .document(idReceptor)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String token = documentSnapshot.getString("token");

                        try {
                            JSONObject json = new JSONObject();
                            JSONObject notification = new JSONObject();
                            notification.put("title", titulo);
                            notification.put("body", cuerpo);
                            json.put("to", token);
                            json.put("notification", notification);

                            String url = "https://fcm.googleapis.com/fcm/send";
                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,
                                    response -> Log.d("FCM", "Notificación enviada"),
                                    error -> Log.e("FCM", "Error al enviar notificación", error)) {

                                @Override
                                public Map<String, String> getHeaders() {
                                    Map<String, String> headers = new HashMap<>();
                                    headers.put("Content-Type", "application/json");
                                    headers.put("Authorization", "key=");
                                    return headers;
                                }
                            };

                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            queue.add(request);

                        } catch (Exception e) {
                            Log.e("FCM", "Excepción al construir JSON", e);
                        }

                    } else {
                        Log.w("FCM", "Token no encontrado para " + idReceptor);
                    }
                });
    }*/
    public void actualizarChat(String id){
        String url = "http://" + "192.168.1.116" + ":3005/actualizarChat";
        //String url = "http://" + "172.20.10.3" + ":3005/actualizarChat";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", id);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        if (response.getBoolean("success")) {}
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Log.e("PA", "ERROR", error)
        );

        queue.add(request);
    }

}