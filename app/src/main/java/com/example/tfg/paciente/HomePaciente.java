package com.example.tfg.paciente;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tfg.BluetoothService;
import com.example.tfg.R;
import com.example.tfg.util.FileUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class HomePaciente extends Fragment {
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private TextView textNumero1;

    private FirebaseFirestore db;
    private LineChart lineChart;
    private String dniPaciente;
    private BroadcastReceiver bluetoothReceiver;
    ConstraintLayout layout ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_paciente, container, false);
        textNumero1 = view.findViewById(R.id.txtglucosa);
        layout = view.findViewById(R.id.fondoglucosa);
        lineChart = view.findViewById(R.id.lineChartDiario);

        FileUtils fileUtils = new FileUtils();
        dniPaciente = fileUtils.readFile(requireContext(), "config.txt");
        db = FirebaseFirestore.getInstance();

        checkPermissions();

        bluetoothReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int num1 = intent.getIntExtra("num1", 0);
                int num2 = intent.getIntExtra("num2", 0);
                String a = String.valueOf(num1);
                textNumero1.setText(a + "mg/dL" );
                if (num1 < 80 || num1 >= 240) {
                    layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorError));
                } else if (num1 >= 80 && num1 < 180 ) {
                    layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                } else {
                    layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dorado));
                }
                cargarGraficoDiario();
                guardarGlucosa(num1);
            }
        };

        requireContext().registerReceiver(bluetoothReceiver, new IntentFilter("BLUETOOTH_DATA"));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bluetoothReceiver != null) {
            requireContext().unregisterReceiver(bluetoothReceiver);
        }
    }

    private void checkPermissions() {
        List<String> permissions = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }

        if (!permissions.isEmpty()) {
            requestPermissions(permissions.toArray(new String[0]), REQUEST_BLUETOOTH_PERMISSIONS);
        } else {
            startBluetoothService();
        }
    }

    private void startBluetoothService() {
        Intent serviceIntent = new Intent(requireContext(), BluetoothService.class);
        requireContext().startForegroundService(serviceIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            if (granted) {
                startBluetoothService();
            } else {
                textNumero1.setText("Permiso denegado");
            }
        }
    }

    private void cargarGraficoDiario() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date inicioDia = cal.getTime();

        db.collection("mediciones")
                .whereEqualTo("dni", dniPaciente)
                .whereGreaterThanOrEqualTo("timestamp", inicioDia)
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Agrupar por minuto exacto (HH:mm)
                    TreeMap<String, List<Float>> datosPorMinuto = new TreeMap<>();
                    SimpleDateFormat sdfMinuto = new SimpleDateFormat("HH:mm", Locale.getDefault());

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Date fecha = doc.getDate("timestamp");
                        Number dato = doc.getLong("dato1");

                        if (fecha != null && dato != null) {
                            String minutoClave = sdfMinuto.format(fecha);
                            datosPorMinuto.putIfAbsent(minutoClave, new ArrayList<>());
                            datosPorMinuto.get(minutoClave).add(dato.floatValue());
                        }
                    }

                    ArrayList<Entry> entries = new ArrayList<>();
                    ArrayList<String> etiquetas = new ArrayList<>();
                    int index = 0;

                    for (Map.Entry<String, List<Float>> entry : datosPorMinuto.entrySet()) {
                        List<Float> valores = entry.getValue();
                        float suma = 0f;
                        for (Float v : valores) suma += v;
                        float promedio = suma / valores.size();

                        entries.add(new Entry(index, promedio));
                        etiquetas.add(entry.getKey());
                        index++;
                    }

                    LineDataSet dataSet = new LineDataSet(entries, "Glucosa (Promedio por minuto)");
                    dataSet.setColor(Color.BLUE);
                    dataSet.setCircleColor(Color.BLUE);
                    dataSet.setLineWidth(2f);
                    dataSet.setCircleRadius(3f);
                    dataSet.setValueTextSize(9f);

                    lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            int i = (int) value;
                            return (i >= 0 && i < etiquetas.size()) ? etiquetas.get(i) : "";
                        }
                    });
                    lineChart.getXAxis().setGranularity(1f);
                    lineChart.getXAxis().setLabelRotationAngle(45f);

                    LineData lineData = new LineData(dataSet);
                    lineChart.setData(lineData);
                    lineChart.invalidate();
                });
    }


    public void guardarGlucosa(int num){
        String url = "http://" + "192.168.1.116" + ":3005/actualizarGlucosa";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", dniPaciente);
            requestBody.put("glucosa", num);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        if (response.getBoolean("success")) {

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
