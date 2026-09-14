package com.example.tfg.paciente;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tfg.Adapters.CitaAdapter;
import com.example.tfg.Modelos.Cita;
import com.example.tfg.R;
import com.example.tfg.util.FileUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class MisCitas extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Cita> arrayList;
    CitaAdapter myAdapter;
    MaterialCalendarView calendarView;
    private HashSet<CalendarDay> fechasConCita = new HashSet<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_citas, container, false);
        FileUtils fileUtils = new FileUtils();
        String id = fileUtils.readFile(getContext(), "config.txt");
        recyclerView = view.findViewById(R.id.rvc);
        arrayList = new ArrayList<>();
        myAdapter = new CitaAdapter(getContext(), arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(myAdapter);
        cargarCitas(id);
        return view;
    }

    public void cargarCitas(String id){
        String url = "http://192.168.1.116:3005/citasPaciente";
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
                            arrayList.clear();
                            fechasConCita.clear();
                            JSONArray a = response.getJSONArray("lista");
                            for (int i = 0; i < a.length(); i++) {
                                JSONObject cObj = a.getJSONObject(i);
                                Cita c = new Cita();
                                String nom =  "Dr: " + cObj.getString("nombre");
                                c.setNombre(nom); // <- debe venir del backend
                                c.setFecha(cObj.getString("fecha"));
                                c.setimg(R.drawable.medic);
                                arrayList.add(c);
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
}
