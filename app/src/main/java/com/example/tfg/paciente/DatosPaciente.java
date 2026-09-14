package com.example.tfg.paciente;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tfg.R;
import com.example.tfg.util.FileUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class DatosPaciente extends Fragment {
    private TabLayout tabLayout;
    private EditText pes,alt;
    private TextView imc,mg,hem,ui,tir,nomP;
    private Button btnGuardar;

    LayoutInflater inflat;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_datos_paciente, container, false);
        FileUtils fileUtils = new FileUtils();
        String id = fileUtils.readFile(getContext(), "config.txt");
        ViewFlipper viewFlipper = view.findViewById(R.id.vf);
        nomP=view.findViewById(R.id.nombrePaciente);
        inflat = LayoutInflater.from(getContext());
        viewFlipper.addView(inflat.inflate(R.layout.dp, null));
        viewFlipper.addView(inflat.inflate(R.layout.dmedicos, null));
        tabLayout = view.findViewById(R.id.tb);
        tabLayout.addTab(tabLayout.newTab().setText("Fisicos"));
        tabLayout.addTab(tabLayout.newTab().setText("Medicos"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("TAB", "Tab seleccionado: " + tab.getPosition());
                viewFlipper.setDisplayedChild(tab.getPosition());;
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        db = FirebaseFirestore.getInstance();
        pes = view.findViewById(R.id.pes);
        alt = view.findViewById(R.id.alt);
        imc = view.findViewById(R.id.imced);
        mg = view.findViewById(R.id.mg);
        hem = view.findViewById(R.id.hem);
        ui = view.findViewById(R.id.UI);
        tir = view.findViewById(R.id.tir);
        btnGuardar= view.findViewById(R.id.guard);
        obtenerMedidas(id);
        obtenerMedidasFisicas(id);
        btnGuardar.setOnClickListener(v -> {
            String altura1 = alt.getText().toString();
            String peso1 = pes.getText().toString();
            StringBuilder errorMessage = new StringBuilder();
            if(altura1.isEmpty() || peso1.isEmpty()){
                Toast.makeText(getContext(), "Deben estar todos los " +
                        "campos rellenados", Toast.LENGTH_LONG).show();

            }else{

                if (errorMessage.length() > 0) {
                    Toast.makeText(getContext(), errorMessage.toString(), Toast.LENGTH_LONG).show();
                } else {
                    actualizarmedidas(altura1,peso1,id);
                }


            }

        });

        return view;
    }
    public void actualizarmedidas(String altura, String peso, String id){
        String url = "http://" + "192.168.1.116" + ":3005/actualizarMedidas";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", id);
            requestBody.put("altura", altura);
            requestBody.put("peso", peso);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                           obtenerMedidas(id);
                           obtenerMedidasFisicas(id);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Log.e("PA", "ERROR", error)
        );
        queue.add(request);
    }


    public void obtenerMedidas(String id){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -2); // hace 2 meses
        Date fechaLimite = calendar.getTime();
        db.collection("mediciones")
                .whereEqualTo("dni", id)
                .whereGreaterThanOrEqualTo("timestamp", fechaLimite)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int suma = 0;
                    int contador = 0;
                    int enRango = 0;


                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Long glucosa = doc.getLong("dato1");
                        if (glucosa != null) {
                            suma += glucosa;
                            if (glucosa >= 70 && glucosa <= 180) {
                                enRango++;
                            }
                            contador++;
                        }
                    }

                    if (contador > 0) {
                        double media = (double) suma / contador;
                        Log.d("MEDIA", "Media glucosa (últimos 2 meses): " + media);
                        double porcentaje = (double) enRango /contador * 100;
                        double hbA1c = (media + 46.7) / 28.7;
                        DecimalFormat df = new DecimalFormat("#.##");
                        String m2 = df.format(media);
                        mg.setText(m2);
                        String h1 = df.format(hbA1c);
                        //hem.setText(String.valueOf(hbA1c));
                        hem.setText(h1);
                        String p1 = df.format(porcentaje);
                        //tir.setText(String.valueOf(porcentaje));
                        tir.setText(p1);
                    } else {
                        Log.d("MEDIA", "No hay datos en los últimos 2 meses");
                    }
                })
                .addOnFailureListener(e -> Log.e("MEDIA", "Error al consultar Firestore", e));
    }
    public void obtenerMedidasFisicas(String id){
        String url = "http://" + "192.168.1.116" + ":3005/obtenerMedidas";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", id);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            String peso = response.getString("peso");
                            String altura = response.getString("altura");
                            String nombre = response.getString("nombre");
                            alt.setText(altura);
                            pes.setText(peso);
                            nomP.setText(nombre);
                            double a = Integer.parseInt(altura);
                            double p = Double.parseDouble(peso);
                            double im = (p / (a * a)) * 10000;
                            DecimalFormat df = new DecimalFormat("#.##");
                            String imc2 = df.format(im);
                            imc.setText(imc2);
                            double dt = p * 0.5;
                            String dt2 = df.format(dt);
                            ui.setText(dt2);

                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Log.e("PA", "ERROR", error)
        );
        queue.add(request);
    }
}