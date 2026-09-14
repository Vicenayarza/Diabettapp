package com.example.tfg.paciente.informesP;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.tfg.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class MaxminP extends Fragment {
    BarChart barChart;
    private String idPaciente;

    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_maxmin_p, container, false);
        barChart = view.findViewById(R.id.barChart);
        db = FirebaseFirestore.getInstance();


        if (getArguments() != null) {
            idPaciente = getArguments().getString("idPaciente");
        }

        view. findViewById(R.id.btn7dias).setOnClickListener(v -> cargarDatos(7));
        view.findViewById(R.id.btn14dias).setOnClickListener(v -> cargarDatos(14));
        view.findViewById(R.id.btn30dias).setOnClickListener(v -> cargarDatos(30));
        view.findViewById(R.id.btn90dias).setOnClickListener(v -> cargarDatos(90));
        cargarDatos(7);

        return view;
    }
    private void cargarDatos(int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -dias + 1);
        Date fechaInicio = calendar.getTime();

        db.collection("mediciones")
                .whereEqualTo("dni", idPaciente)
                .whereGreaterThanOrEqualTo("timestamp", fechaInicio)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Map<String, List<Integer>> datosPorDia = new TreeMap<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Date fecha = doc.getDate("timestamp");
                        if (fecha == null) continue;

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(fecha);
                        String clave = new SimpleDateFormat("EEE", new Locale("es", "ES")).format(cal.getTime());

                        Long valorLong = doc.getLong("dato1");
                        if (valorLong == null) continue;
                        int valor = valorLong.intValue();
                        if (valor <= 0) continue;

                        datosPorDia.computeIfAbsent(clave, k -> new ArrayList<>()).add(valor);
                    }

                    List<BarEntry> entries = new ArrayList<>();
                    List<String> labels = new ArrayList<>();
                    int index = 0;

                    for (Map.Entry<String, List<Integer>> entry : datosPorDia.entrySet()) {
                        List<Integer> valores = entry.getValue();
                        if (valores.isEmpty()) continue;

                        int min = Collections.min(valores);
                        int max = Collections.max(valores);
                        int diff = max - min;
                        if (diff == 0) diff = 1;

                        entries.add(new BarEntry(index, new float[]{min, diff}));
                        labels.add(entry.getKey());
                        index++;
                    }

                    BarDataSet dataSet = new BarDataSet(entries, "");
                    dataSet.setColors(new int[]{Color.RED, Color.YELLOW});
                    dataSet.setStackLabels(new String[]{"Mínimo", "Máximo"});
                    dataSet.setValueTextSize(12f);
                    dataSet.setValueTextColor(Color.BLACK);

                    BarData data = new BarData(dataSet);
                    data.setBarWidth(0.5f);

                    barChart.setData(data);
                    barChart.setFitBars(true);
                    barChart.getDescription().setEnabled(false);
                    barChart.getLegend().setEnabled(true);

                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setGranularity(1f);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setDrawGridLines(false);
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                    xAxis.setTextSize(12f);

                    barChart.getAxisRight().setEnabled(false);
                    barChart.getAxisLeft().setAxisMinimum(0f);
                    barChart.invalidate();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener datos", e));
    }
}