package com.example.tfg.medico;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tfg.Adapters.PersonasAlertaAdapter;
import com.example.tfg.Modelos.PersonasAlerta;
import com.example.tfg.R;
import com.example.tfg.util.FileUtils;
import com.google.android.material.chip.Chip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeMedico extends Fragment {
    TextView nombre,total,segui,sinp;

    RecyclerView rv;
    PersonasAlertaAdapter adap;
    ArrayList<PersonasAlerta> array;
    Chip chiphipo, chipest, chiphiper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_medico_copia, container, false);
        FileUtils fileUtils = new FileUtils();
        String id = fileUtils.readFile(getContext(), "config.txt");
        nombre = view.findViewById(R.id.nombredoctor);
        total = view.findViewById(R.id.total);
        segui = view.findViewById(R.id.segui);
        sinp = view.findViewById(R.id.sin);
        rv = view.findViewById(R.id.rvA);
        chiphipo = view.findViewById(R.id.chip);
        chipest = view.findViewById(R.id.chip2);
        chiphiper = view.findViewById(R.id.chip3);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);
        array = new ArrayList<>();
        adap = new PersonasAlertaAdapter(getContext(),array);
        rv.setAdapter(adap);
        chiphipo.setOnClickListener(v -> filterList(id,0));
        chipest.setOnClickListener(v -> filterList(id,1));
        chiphiper.setOnClickListener(v -> filterList(id,2));
        obtenermedico(id);
        cargartabla(id);
        filterList(id,0);


        return view;

    }

    private void obtenermedico(String id) {
        String url = "http://" + "192.168.1.116" + ":3005/obtenerMedico";
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
                            String n = response.getString("nombre");
                            Log.d("nombremedico",n);
                            String nom =  " Dr: " + n;
                            nombre.setText(nom);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Log.e("PA", "ERROR", error)
        );
        queue.add(request);
    }

    public void cargartabla(String id){
        String url = "http://" + "192.168.1.116" + ":3005/cargarResumen";
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
                            int pac = response.getInt("pac");
                            int seg = response.getInt("seg");
                            int sin = pac - seg;
                            total.setText(String.valueOf(pac));
                            segui.setText(String.valueOf(seg));
                            sinp.setText(String.valueOf(sin));
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Log.e("PA", "ERROR", error)
        );
        queue.add(request);
    }
    public void filterList(String id, int num){
        array.clear();
        adap.notifyDataSetChanged();
        String url = "http://" + "192.168.1.116" + ":3005/listarPacientesAlerta";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", id);
            requestBody.put("int", num);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray a = response.getJSONArray("lista");
                            for (int i = 0; i < a.length(); i++) {
                                JSONObject cObj = a.getJSONObject(i);
                                PersonasAlerta p = new PersonasAlerta();
                                p.setNombre(cObj.getString("nombre"));
                                p.setGlucemia(cObj.getInt("glucosa"));
                                p.setimg(R.drawable.av);
                                array.add(p);
                            }
                            adap.notifyDataSetChanged();
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

