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

public class ListaTodos extends Fragment {
    private SearchView txtBuscar;
    private RecyclerView rvtodos;
    private ListaPersonasAdapter adap;
    private ArrayList<Persona> personas;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_todos, container, false);
        FileUtils fileUtils = new FileUtils();
        String id = fileUtils.readFile(getContext(), "config.txt");
        rvtodos = view.findViewById(R.id.rvtodos);
        txtBuscar = view.findViewById(R.id.txtBuscartodos);
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
        rvtodos.setLayoutManager(layoutManager);
        personas = new ArrayList<>();
        adap = new ListaPersonasAdapter(getContext(), personas,"Chat",id,false);
        rvtodos.setAdapter(adap);
        cargarTodos(id);

        return view;
    }
    private void cargarTodos(String id) {
        personas.clear();
        adap.notifyDataSetChanged();
       String url = "http://192.168.1.116:3005/listarPacientes";
       // String url = "http://" + "172.20.10.3" + ":3005/listarPacientesChat";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray lista = response.getJSONArray("lista");
                            for (int i = 0; i < lista.length(); i++) {
                                JSONObject cObj = lista.getJSONObject(i);
                                Persona p = new Persona();
                                p.setNombre(cObj.getString("nombre"));
                                p.setId(cObj.getString("id"));
                                p.setimg(R.drawable.av);
                                personas.add(p);
                            }
                            // Notificar al adaptador que hay nuevos datos
                            adap.notifyDataSetChanged();
                        } else {
                          //  Toast.makeText(getContext(), "No se encontraron pacientes", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("PA", "Error al cargar pacientes: ", error);
                   // Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }
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
