package com.example.tfg.paciente.informesP;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.tfg.R;
import com.github.mikephil.charting.charts.BarChart;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GProm extends Fragment {
    BarChart barChart;
    private String idPaciente;
    TextView tvPromedio;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_g_prom, container, false);
        barChart = view.findViewById(R.id.barChart);
        db = FirebaseFirestore.getInstance();
        tvPromedio = view.findViewById(R.id.tvPromedio);
        if (getArguments() != null) {
            idPaciente = getArguments().getString("idPaciente");
        }

        view. findViewById(R.id.btn7dias).setOnClickListener(v -> cargarDatos(7,idPaciente));
        view.findViewById(R.id.btn14dias).setOnClickListener(v -> cargarDatos(14,idPaciente));
        view.findViewById(R.id.btn30dias).setOnClickListener(v -> cargarDatos(30,idPaciente));
        view.findViewById(R.id.btn90dias).setOnClickListener(v -> cargarDatos(90,idPaciente));
        cargarDatos(7,idPaciente);

        return view;
    }
    private void cargarDatos(int dias, String idPaciente) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, -dias + 1);
        Date fechaInicio = cal.getTime();

        db.collection("mediciones")
                .whereEqualTo("dni", idPaciente)
                .whereGreaterThanOrEqualTo("timestamp", fechaInicio)
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    Map<String, List<Float>> agrupado = new LinkedHashMap<>();
                    SimpleDateFormat formatoClave;

                    if (dias == 1) {
                        formatoClave = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    } else {
                        formatoClave = new SimpleDateFormat("dd/MM", Locale.getDefault());
                    }

                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        Date fecha = doc.getDate("timestamp");
                        Number glucosa = doc.getLong("dato1");
                        if (fecha != null && glucosa != null) {
                            String clave = formatoClave.format(fecha);
                            agrupado.putIfAbsent(clave, new ArrayList<>());
                            agrupado.get(clave).add(glucosa.floatValue());
                        }
                    }

                    ArrayList<BarEntry> entries = new ArrayList<>();
                    ArrayList<Integer> colores = new ArrayList<>();
                    ArrayList<String> etiquetas = new ArrayList<>();
                    int index = 0;
                    float sumaTotal = 0;
                    int totalMedidas = 0;

                    for (Map.Entry<String, List<Float>> entry : agrupado.entrySet()) {
                        List<Float> valores = entry.getValue();
                        float promedio = 0;
                        for (float val : valores) promedio += val;
                        promedio /= valores.size();

                        sumaTotal += promedio;
                        totalMedidas++;

                        entries.add(new BarEntry(index, promedio));
                        etiquetas.add(entry.getKey());

                        // Asignar color según el valor
                        if (promedio < 80 || promedio >= 240) {
                            colores.add(ContextCompat.getColor(requireContext(), R.color.colorError));
                        } else if (promedio >= 80 && promedio < 180) {
                            colores.add(ContextCompat.getColor(requireContext(), R.color.green));
                        } else {
                            colores.add(ContextCompat.getColor(requireContext(), R.color.dorado));
                        }

                        index++;
                    }

                    float promedioGeneral = (totalMedidas > 0) ? sumaTotal / totalMedidas : 0;
                    tvPromedio.setText("Promedio: " + String.format(Locale.getDefault(), "%.1f", promedioGeneral));

                    BarDataSet dataSet = new BarDataSet(entries, "Glucosa");
                    dataSet.setColors(colores); // aplicar colores por barra
                    BarData data = new BarData(dataSet);
                    data.setBarWidth(0.9f);

                    barChart.getXAxis().setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            int i = (int) value;
                            return (i >= 0 && i < etiquetas.size()) ? etiquetas.get(i) : "";
                        }
                    });
                    barChart.getXAxis().setGranularity(1f);
                    barChart.getXAxis().setLabelRotationAngle(45f);
                    barChart.getXAxis().setDrawGridLines(false);
                    barChart.getDescription().setEnabled(false);
                    barChart.setFitBars(true);
                    barChart.setData(data);
                    barChart.invalidate();
                });
    }

}