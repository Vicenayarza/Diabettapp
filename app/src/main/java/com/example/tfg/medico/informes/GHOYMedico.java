package com.example.tfg.medico.informes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.tfg.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GHOYMedico extends Fragment {

    private LineChart lineChart; // ← CAMBIADO a LineChart
    private String idPaciente;
    private TextView tvPromedio;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_g_h_o_y_medico, container, false);
        lineChart = view.findViewById(R.id.barChart); // ← CAMBIADO a lineChart
        db = FirebaseFirestore.getInstance();
        tvPromedio = view.findViewById(R.id.tvPromedio);
        if (getArguments() != null) {
            idPaciente = getArguments().getString("idPaciente");
        }
        cargarDatos(idPaciente);
        return view;
    }

    private void cargarDatos(String idPaciente) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date inicioDia = cal.getTime();

        db.collection("mediciones")
                .whereEqualTo("dni", idPaciente)
                .whereGreaterThanOrEqualTo("timestamp", inicioDia)
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    float[] sumasPorMinuto = new float[1440]; // 24 * 60
                    int[] conteosPorMinuto = new int[1440];

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Date fecha = doc.getDate("timestamp");
                        Number dato = doc.getLong("dato1");

                        if (fecha != null && dato != null) {
                            Calendar calMin = Calendar.getInstance();
                            calMin.setTime(fecha);
                            int hora = calMin.get(Calendar.HOUR_OF_DAY);
                            int minuto = calMin.get(Calendar.MINUTE);
                            int indice = hora * 60 + minuto;

                            sumasPorMinuto[indice] += dato.floatValue();
                            conteosPorMinuto[indice]++;
                        }
                    }

                    ArrayList<Entry> entries = new ArrayList<>();
                    ArrayList<String> etiquetasX = new ArrayList<>();
                    float sumaGeneral = 0;
                    int totalMedidas = 0;

                    for (int i = 0; i < 1440; i++) {
                        if (conteosPorMinuto[i] > 0) {
                            float promedio = sumasPorMinuto[i] / conteosPorMinuto[i];
                            entries.add(new Entry(i, promedio));
                            sumaGeneral += promedio;
                            totalMedidas++;
                        }
                        etiquetasX.add(String.format(Locale.getDefault(), "%02d:%02d", i / 60, i % 60));
                    }

                    float promedioTotal = (totalMedidas > 0) ? sumaGeneral / totalMedidas : 0;
                    tvPromedio.setText(String.format(Locale.getDefault(), "Promedio: %.1f", promedioTotal));

                    LineDataSet dataSet = new LineDataSet(entries, "Glucosa promedio por minuto");
                    dataSet.setColor(ContextCompat.getColor(requireContext(), R.color.teal_700));
                    dataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.teal_700));
                    dataSet.setLineWidth(1.5f);
                    dataSet.setCircleRadius(2.5f);
                    dataSet.setValueTextSize(8f);
                    dataSet.setDrawValues(false); // Oculta valores por punto para no saturar

                    lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            int index = (int) value;
                            if (index >= 0 && index < etiquetasX.size()) {
                                return etiquetasX.get(index);
                            } else {
                                return "";
                            }
                        }
                    });

                    lineChart.getXAxis().setGranularity(60f); // etiqueta cada 60 minutos
                    lineChart.getXAxis().setLabelRotationAngle(45f);
                    lineChart.getDescription().setEnabled(false);
                    lineChart.getXAxis().setDrawLabels(true);
                    lineChart.getXAxis().setDrawGridLines(false);
                    lineChart.getXAxis().setLabelCount(6, true); // reduce cantidad de etiquetas visibles

                    LineData lineData = new LineData(dataSet);
                    lineChart.setData(lineData);
                    lineChart.invalidate();
                });
    }

}