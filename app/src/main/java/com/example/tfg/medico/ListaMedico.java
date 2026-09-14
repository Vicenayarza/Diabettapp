package com.example.tfg.medico;

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

public class ListaMedico extends Fragment {

    RecyclerView recyclerView;
    SearchView searchView;
    ListaPersonasAdapter myAdapter;
    ArrayList<Persona> arrayList;
    String idP;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_medico, container, false);
        FileUtils fileUtils = new FileUtils();
        String id = fileUtils.readFile(getContext(), "config.txt");
        recyclerView = view.findViewById(R.id.recycle);
        searchView = view.findViewById(R.id.txtBuscartodos);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        myAdapter = new ListaPersonasAdapter(getContext(),arrayList,"Lista",idP,true);
        recyclerView.setAdapter(myAdapter);
        cargarPacientes(id);

        return view;
    }

    private void cargarPacientes(String id) {

        String url = "http://" + "192.168.1.116" + ":3005/listarPacientes";
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
                                idP = id;
                                p.setimg(R.drawable.av);
                                arrayList.add(p);
                            }
                            myAdapter.notifyDataSetChanged();

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
        for(Persona persona : arrayList){
            if(persona.getNombre().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(persona);

            }
        }
        if(filteredList.isEmpty()){
            //Toast.makeText(getContext(),"No user found",Toast.LENGTH_SHORT).show();
        }else{
            myAdapter.setFilterList(filteredList);
        }
    }
    /*public void getUser(){
        for(int i = 0;i < pacientes.length; i++ ){
            Persona paciente = new Persona();
            paciente.setNombre(pacientes[i]);
            paciente.setimg(imagenes[0]);
            arrayList.add(paciente);
        }

    }*/

}
