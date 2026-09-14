package com.example.tfg.medico.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
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

public class ListaChat extends Fragment {
    private SearchView txtBuscar;
    private RecyclerView rvchats;
    private ListaPersonasAdapter adap;
    private ArrayList<Persona> personas;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista_chat, container, false);
        FileUtils fileUtils = new FileUtils();
        String id = fileUtils.readFile(getContext(), "config.txt");
        rvchats = view.findViewById(R.id.rvchats);
        txtBuscar = view.findViewById(R.id.txtBuscarchats);
        txtBuscar.clearFocus();
        txtBuscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fileList(newText);
                return true;
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvchats.setLayoutManager(layoutManager);
        personas = new ArrayList<>();
        adap = new ListaPersonasAdapter(getContext(), personas,"Chat",id,false);
        rvchats.setAdapter(adap);
        cargarPacientes(id);

        return view;
    }

    private void cargarPacientes(String id) {
        personas.clear();
        adap.notifyDataSetChanged();
        String url = "http://" + "192.168.1.116" + ":3005/listarPacientesChat";
       // String url = "http://" + "1172.20.10.3" + ":3005/listarPacientesChat";
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
                            JSONArray a = response.getJSONArray("lista");
                            for (int i = 0; i < a.length(); i++) {
                                JSONObject cObj = a.getJSONObject(i);
                                Persona p = new Persona();
                                p.setNombre(cObj.getString("nombre")); // <- debe venir del backend
                                p.setId(cObj.getString("id"));
                                p.setimg(R.drawable.av);
                                personas.add(p);
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



    private void fileList(String newText) {
        ArrayList<Persona> filteredList = new ArrayList<>();
        for(Persona persona : personas){
            if(persona.getNombre().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(persona);

            }
        }
        if(filteredList.isEmpty()){
           // Toast.makeText(getContext(),"No user found",Toast.LENGTH_SHORT).show();
        }else{
            adap.setFilterList(filteredList);
        }
    }

}
