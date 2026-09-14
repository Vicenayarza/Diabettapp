package com.example.tfg.medico.informes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.tfg.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TiempoRangosM extends Fragment {
    HorizontalBarChart barChart;
    private String idPaciente;
    TextView tvPromedio;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_tiempo_rangos_m, container, false);
        barChart = view.findViewById(R.id.barChart);
        db = FirebaseFirestore.getInstance();
        tvPromedio = view.findViewById(R.id.tvPromedio);
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
    public void cargarDatos(int dias) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -dias + 1);
        Date fechaInicio = cal.getTime();

        db.collection("mediciones")
                .whereEqualTo("dni", idPaciente)
                .whereGreaterThanOrEqualTo("timestamp", fechaInicio)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int[] contadores = new int[5]; // <54, 54-69, 70-180, 181-250, >250
                    int total = 0;

                    // Almacena los días únicos para mostrar en el mensaje
                    ArrayList<String> diasConDatos = new ArrayList<>();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Number valor = doc.getLong("dato1");
                        Date fecha = doc.getDate("timestamp");
                        if (valor != null && fecha != null) {
                            float g = valor.floatValue();
                            total++;

                            if (g < 54) contadores[0]++;
                            else if (g < 70) contadores[1]++;
                            else if (g <= 180) contadores[2]++;
                            else if (g <= 250) contadores[3]++;
                            else contadores[4]++;

                            String dia = sdf.format(fecha);
                            if (!diasConDatos.contains(dia)) {
                                diasConDatos.add(dia);
                            }
                        }
                    }

                    ArrayList<BarEntry> entries = new ArrayList<>();
                    ArrayList<Integer> colores = new ArrayList<>();
                    String[] labels = { "<54", "54-69", "70-180", "181-250", ">250" };
                    int[] coloresRangos = {
                            ContextCompat.getColor(requireContext(), R.color.rojo_intenso),
                            ContextCompat.getColor(requireContext(), R.color.rojo_suave),
                            ContextCompat.getColor(requireContext(), R.color.verde),
                            ContextCompat.getColor(requireContext(), R.color.amarillo),
                            ContextCompat.getColor(requireContext(), R.color.naranja)
                    };

                    for (int i = 0; i < contadores.length; i++) {
                        float porcentaje = total > 0 ? (contadores[i] * 100f / total) : 0;
                        entries.add(new BarEntry(i, porcentaje));
                        colores.add(coloresRangos[i]);
                    }

                    BarDataSet dataSet = new BarDataSet(entries, "");
                    dataSet.setColors(colores);
                    dataSet.setValueTextSize(12f);
                    dataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return String.format(Locale.getDefault(), "%.0f %%", value);
                        }
                    });

                    BarData data = new BarData(dataSet);
                    barChart.setData(data);
                    barChart.getXAxis().setDrawLabels(false);
                    barChart.getAxisRight().setEnabled(false);
                    barChart.getAxisLeft().setAxisMinimum(0f);
                    barChart.getAxisLeft().setAxisMaximum(100f);
                    barChart.getXAxis().setGranularity(1f);
                    barChart.getDescription().setEnabled(false);
                    barChart.getLegend().setEnabled(false);
                    barChart.setScaleEnabled(false);
                    barChart.invalidate();



                });
    }
}