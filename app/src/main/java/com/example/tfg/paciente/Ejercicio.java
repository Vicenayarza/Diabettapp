package com.example.tfg.paciente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tfg.ChatGPT.ChatGPT;
import com.example.tfg.Chatbot.ResponseCallBack;
import com.example.tfg.MainActivity_Pacientes;
import com.example.tfg.R;
import com.example.tfg.util.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class Ejercicio extends AppCompatActivity {
   private ImageView correr,nadar,andar,padel,bici;
   Button empezar;
    String dep,altura,peso;
    int glucosa,edad;
    TextView ejercicio1,deporte;
    LottieAnimationView l;
    EditText minutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicio);
        FileUtils fileUtils = new FileUtils();
        String id = fileUtils.readFile(this, "config.txt");
        correr = findViewById(R.id.iCor);
        nadar = findViewById(R.id.iNad);
        andar = findViewById(R.id.iAnd);
        padel = findViewById(R.id.ipad);
        bici = findViewById(R.id.iBic);
        ejercicio1 = findViewById(R.id.ejemplo);
        minutos = findViewById(R.id.mins);
        deporte = findViewById(R.id.ejercicio);
        empezar = findViewById(R.id.start);
        l= findViewById(R.id.i2);

        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity_Pacientes.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });


        View.OnClickListener botones = v -> {
           dep = (String) v.getTag();
            deporte.setText(dep);
            obtenerdatos(id);
        };
        correr.setOnClickListener(botones);
        andar.setOnClickListener(botones);
        nadar.setOnClickListener(botones);
        padel.setOnClickListener(botones);
        bici.setOnClickListener(botones);
        empezar.setOnClickListener(v -> {
            Intent intent = new Intent(this, EjercicioFinal.class);
            intent.putExtra("deporte", dep);
            intent.putExtra("minutos", minutos.getText().toString());
            startActivity(intent);
        });
    }
    public void obtenerdatos(String id){
        String url = "http://" + "192.168.1.116" + ":3005/cargarDatos";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", id);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            peso = response.getString("peso");
                            altura = response.getString("altura");
                            edad = response.getInt("edad");
                            glucosa = response.getInt("glucosa");
                            if (glucosa < 80 || glucosa >= 250) {
                               obtenerRecomendacion(edad,glucosa,peso,altura);
                            }
                            else{
                                String query = "Soy una persona diabetica de tipo 1 , que tiene "+edad+ " años, mide "+altura+" cm y pesa" +peso+ " kilos, quiero el deporte" +dep+ "y tengo una glucosa de "+glucosa+" hazme unos tres o cuatro ejercicios de ese deporte que podría hacer, solo los ejercicios sin texto aparte.";
                                String q = "expresame el numero de minutos que debe durar la sesion recomendada, solamente sacame el numero, nada mas escrito";
                                enviarQuery(query,ejercicio1);
                                enviarQuery(q,minutos);

                            }

                            }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Log.e("PA", "ERROR", error)
        );
        queue.add(request);

    }
    public void obtenerRecomendacion(int edad, int glucosa, String peso, String altura){
        String query = "Soy una persona diabetica de tipo 1 , que tiene "+edad+ " años, mide "+altura+" cm y pesa" +peso+ " kilos y tengo una glucosa de "+glucosa+" como con esa glucosa no puedo hacer ejercicio, hazme una recomendación si es por hipoglucemia de que deberia tomar y si es por hiperglucemia de cuantas unidades debería pincharme. En unas dos o tres lineas y sin titulos, en castellano y solo texto";

        ChatGPT.getResponse(query, new ResponseCallBack() {
            @Override
            public void onResponse(String response) {
                new android.app.AlertDialog.Builder(Ejercicio.this)
                        .setTitle("Advertencia")
                        .setMessage(response)
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", (dialog, which) -> {

                            Intent intent = new Intent(Ejercicio.this, MainActivity_Pacientes.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .show();
            }

            @Override
            public void onError(String throwable) {

            }

        });

    }
    public void enviarQuery(String query, TextView t){
        Log.d("GPT", "Lanzando petición con query: " + query);
        ChatGPT.getResponse(query, new ResponseCallBack() {
            @Override
            public void onResponse(String response) {
                Log.d("h","holaaaa2");
                t.setText(response);

            }

            @Override
            public void onError(String throwable) {
                Log.d("e", throwable);

            }

        });



    }

}
