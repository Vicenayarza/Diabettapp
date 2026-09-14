package com.example.tfg.paciente;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomePac extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private TextView textNumero1, textNumero2;
    private FirebaseFirestore db;
    private LineChart lineChart;
    private String dniPaciente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_pac);
        textNumero1 = findViewById(R.id.txtglu);
        ImageView img = findViewById(R.id.imagecirculo);
        FileUtils fileUtils = new FileUtils();
        dniPaciente = fileUtils.readFile(this, "config.txt");
        lineChart = findViewById(R.id.lineChartDiario);
        db = FirebaseFirestore.getInstance();
        checkPermissions();
        // Registrar BroadcastReceiver
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int num1 = intent.getIntExtra("num1", 0);
                int num2 = intent.getIntExtra("num2", 0);
                textNumero1.setText(String.valueOf(num1));
                if (num1 < 80 || num1 > 180 ){
                    img.setImageResource(R.drawable.circulorojo);
                }else{
                    img.setImageResource(R.drawable.circuloverde);
                }


                cargarGraficoDiario();
            }
        }, new IntentFilter("BLUETOOTH_DATA"));

    }

    private void checkPermissions() {
        List<String> permissions = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }

        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), REQUEST_BLUETOOTH_PERMISSIONS);
        } else {
            startBluetoothService();
        }
    }

    private void startBluetoothService() {
        Intent serviceIntent = new Intent(this, BluetoothService.class);
        startForegroundService(serviceIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                textNumero2.setText("");
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
                    ArrayList<Entry> entries = new ArrayList<>();
                    ArrayList<String> horas = new ArrayList<>();
                    SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    int index = 0;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Date fecha = doc.getDate("timestamp");
                        Number dato = doc.getLong("dato1");
                        if (fecha != null && dato != null) {
                            entries.add(new Entry(index, dato.floatValue()));
                            horas.add(sdfHora.format(fecha));
                            index++;
                        }
                    }

                    LineDataSet dataSet = new LineDataSet(entries, "Dato1 diario");
                    lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            int i = (int) value;
                            if (i >= 0 && i < horas.size()) {
                                return horas.get(i);
                            } else {
                                return "";
                            }
                        }
                    });
                    lineChart.getXAxis().setGranularity(1f);
                    lineChart.getXAxis().setLabelRotationAngle(45f);

                    LineData lineData = new LineData(dataSet);
                    lineChart.setData(lineData);
                    lineChart.invalidate();
                });
    }
}