package com.example.tfg.medico;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class AgendaMedico extends Fragment {

    public RecyclerView rv;
    public CitaAdapter myadapter;
    public ArrayList<Cita> c;
    public TextView tvSelectedDate, medico;
    private ImageButton buttonPreviousDay, buttonNextDay;
    private Calendar currentDate;
    LocalDate selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agenda_medico, container, false);
        FileUtils fileUtils = new FileUtils();
        String id = fileUtils.readFile(getContext(), "config.txt");

        tvSelectedDate = view.findViewById(R.id.dateText);
        medico = view.findViewById(R.id.doctor);
        rv = view.findViewById(R.id.rc);
        buttonPreviousDay = view.findViewById(R.id.imageButtonup);
        buttonNextDay = view.findViewById(R.id.imageButtondown);

        selectedDate = LocalDate.now();
        currentDate = Calendar.getInstance();

        obtenermedico(id);
        updateDateText();

        RecyclerView.LayoutManager layoutManagerCitas = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManagerCitas);
        c = new ArrayList<>();
        myadapter = new CitaAdapter(getContext(), c);
        rv.setAdapter(myadapter);

        obtenerCitasDesdeServidor(selectedDate, id);

        // Flecha día anterior
        buttonPreviousDay.setOnClickListener(v -> {
            if (!selectedDate.isEqual(LocalDate.now())) {
                selectedDate = selectedDate.minusDays(1);
                currentDate.add(Calendar.DAY_OF_MONTH, -1);
                updateDateText();
                obtenerCitasDesdeServidor(selectedDate, id);
            }
        });

        // Flecha día siguiente
        buttonNextDay.setOnClickListener(v -> {
            selectedDate = selectedDate.plusDays(1);
            currentDate.add(Calendar.DAY_OF_MONTH, 1);
            updateDateText();
            obtenerCitasDesdeServidor(selectedDate, id);
        });

        // Pulsar en la fecha para abrir DatePicker
        tvSelectedDate.setOnClickListener(v -> openDatePicker(id));

        return view;
    }

    private void openDatePicker(String id) {
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    currentDate.set(year1, month1, dayOfMonth);
                    selectedDate = LocalDate.of(year1, month1 + 1, dayOfMonth);
                    updateDateText();
                    obtenerCitasDesdeServidor(selectedDate, id);
                },
                year, month, day
        );

        // Bloquea selección de fechas pasadas
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }

    private void updateDateText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        tvSelectedDate.setText(selectedDate.format(formatter));

        // Desactiva botón "día anterior" si se está en el día actual
        if (selectedDate.isEqual(LocalDate.now())) {
            buttonPreviousDay.setEnabled(false);
            buttonPreviousDay.setAlpha(0.3f); // Apaga visualmente el botón
        } else {
            buttonPreviousDay.setEnabled(true);
            buttonPreviousDay.setAlpha(1f);
        }
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
                            String nom ="Dr: " + n;
                            medico.setText(nom);

                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Log.e("PA", "ERROR", error)
        );
        queue.add(request);
    }

    private void obtenerCitasDesdeServidor(LocalDate fecha, String id) {
        String fechaStr = fecha.toString();
        c.clear();
        myadapter.notifyDataSetChanged();
        String url = "http://" + "192.168.1.116" + ":3005/fechasAgenda";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", id);
            requestBody.put("fecha", fechaStr);
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
                                Cita cita = new Cita();
                                cita.setNombre(cObj.getString("nombre"));
                                cita.setFecha(cObj.getString("hora").substring(0, 5));
                                cita.setimg(R.drawable.av);
                                c.add(cita);
                            }
                            myadapter.notifyDataSetChanged();
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
