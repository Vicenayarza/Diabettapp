package com.example.tfg.paciente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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

public class Comida extends AppCompatActivity {

   ImageButton btnd,btnal,btnmer,btncen,btnap;
   String texto,altura,peso;
   int glucosa,edad;
   TextView insu1,menu1,tipoC;
   LottieAnimationView lot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comida);
        FileUtils fileUtils = new FileUtils();
        String id = fileUtils.readFile(this, "config.txt");
        btnd = findViewById(R.id.iDes);
        btnal = findViewById(R.id.iCom);
        btnmer = findViewById(R.id.iMer);
        btncen = findViewById(R.id.iCen);
        btnap = findViewById(R.id.iTen);
        insu1 = findViewById(R.id.txtMedida);
        menu1 = findViewById(R.id.txtReco);
        lot = findViewById(R.id.ia4);
        tipoC = findViewById(R.id.tipocomida);

        lot.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity_Pacientes.class);
            startActivity(intent);
        });



        View.OnClickListener botones = v -> {
            String tipoComida = (String) v.getTag();
            tipoC.setText(tipoComida);
            obtenerdatos(id);
            String query = "Soy una persona diabetica de tipo 1 , que tiene "+edad+ " años, mide "+altura+" cm y pesa" +peso+ " kilos, quiero hacer un tipo de comida principal que voy a realizar es "+tipoComida+" y tengo una glucosa de "+glucosa+" hazme una recomendación nutricional de que deberia comer adpatado a lo que te he pasado, solamente quiero el menu con los platos , todo seguido , en unas tres -4 lineas, en castellano y sin titulos solo el texto";
            String q = "expresame las unidades de insulina rapida y lenta que me recomiendas pincharme en una frase, tal que asi. Insulina Rapida:Unidades , Insulina Lenta: Unidades";
            enviarQuery(query,menu1);
            enviarQuery(q,insu1);


        };
        btnd.setOnClickListener(botones);
        btnal.setOnClickListener(botones);
        btnmer.setOnClickListener(botones);
        btncen.setOnClickListener(botones);
        btnap.setOnClickListener(botones);

    }

    public void obtenerdatos(String id){
        String url = "http://" + "172.20.10.3" + ":3005/cargarDatos";
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

                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Log.e("PA", "ERROR", error)
        );
        queue.add(request);

        
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
   /* public void enviarQuery(String query, TextView t){
        Log.d("GPT", "Lanzando petición con query: " + query);
        String url = "http://192.168.1.116:3005/chatgpt";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("query", query);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    String reply = response.optString("response", "Sin respuesta");
                    t.setText(reply);

                },
                error -> Log.e("PA", "ERROR", error)
        );

        queue.add(request);
    }
*/



    }
