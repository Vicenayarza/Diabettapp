package com.example.tfg.paciente;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tfg.Adapters.ListaPersonasAdapter;
import com.example.tfg.Modelos.Persona;
import com.example.tfg.R;
import com.example.tfg.util.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatPaciente extends Fragment {

    private RecyclerView rvMedicos;
    private ListaPersonasAdapter adap;
    private ArrayList<Persona> medicos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_paciente, container, false);
        FileUtils fileUtils = new FileUtils();
        String id = fileUtils.readFile(getContext(), "config.txt");
        rvMedicos = view.findViewById(R.id.rvChat);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvMedicos.setLayoutManager(layoutManager);
        medicos = new ArrayList<>();

        adap = new ListaPersonasAdapter(getContext(),medicos,"Chat",id,true);
        rvMedicos.setAdapter(adap);
        cargarMedicos(id);

        return view;
    }

    private void cargarMedicos(String id) {
        String url = "http://" + "172.20.10.3" + ":3005/listarMedicos";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", id);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray a = response.getJSONArray("lista");
                            for (int i = 0; i < a.length(); i++) {
                                JSONObject cObj = a.getJSONObject(i);
                                Persona m = new Persona();
                                m.setNombre(cObj.getString("nombre")); // <- debe venir del backend
                                m.setId(cObj.getString("id"));
                                m.setimg(R.drawable.medic);
                                medicos.add(m);
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
