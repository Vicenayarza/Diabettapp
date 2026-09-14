package com.example.tfg;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.tfg.util.FileUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BluetoothService extends Service {

    private static final String TAG = "BluetoothService";
    private static final String DEVICE_NAME = "HC-05";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String CHANNEL_ID = "BluetoothServiceChannel";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private FirebaseFirestore db;
    private boolean isRunning = true;
    private String dniPaciente ;

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseFirestore.getInstance();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        FileUtils fileUtils = new FileUtils();
        dniPaciente = fileUtils.readFile(this, "config.txt");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permisos de Bluetooth no concedidos");
            stopSelf();
            return;
        }

        startForegroundServiceWithNotification();
        new Thread(this::connectBluetooth).start();
    }

    private void startForegroundServiceWithNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Bluetooth Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Servicio Bluetooth")
                .setContentText("Recibiendo datos del dispositivo HC-05...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();

        startForeground(1, notification);
    }

    private void connectBluetooth() {
        try {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            BluetoothDevice device = null;

            for (BluetoothDevice d : pairedDevices) {
                if (d.getName() != null && d.getName().equals(DEVICE_NAME)) {
                    device = d;
                    break;
                }
            }

            if (device == null) {
                Log.e(TAG, "Dispositivo HC-05 no emparejado");
                stopSelf();
                return;
            }

            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            inputStream = bluetoothSocket.getInputStream();

            byte[] buffer = new byte[1024];
            int bytes;
            StringBuilder stringBuilder = new StringBuilder();

            while (isRunning) {
                bytes = inputStream.read(buffer);
                String readMessage = new String(buffer, 0, bytes);
                stringBuilder.append(readMessage);

                int endOfLineIndex;
                while ((endOfLineIndex = stringBuilder.indexOf("\n")) != -1) {
                    String completeMessage = stringBuilder.substring(0, endOfLineIndex).trim();
                    stringBuilder.delete(0, endOfLineIndex + 1);

                    String[] nums = completeMessage.split(",");
                    if (nums.length == 2) {
                        try {
                            int num1 = Integer.parseInt(nums[0].trim());
                            int num2 = Integer.parseInt(nums[1].trim());

                            guardarDatos(num1, num2);
                            if (num1 < 80) {
                                lanzarAlarma("¡Alerta de glucosa baja!", "Tu nivel de glucosa es " + num1 + " mg/dL");
                            }

                            Intent intent = new Intent("BLUETOOTH_DATA");
                            intent.putExtra("num1", num1);
                            intent.putExtra("num2", num2);
                            sendBroadcast(intent);

                        } catch (NumberFormatException e) {
                            Log.w(TAG, "Datos mal formateados: " + completeMessage);
                        }
                    } else {
                        Log.w(TAG, "Mensaje incompleto o mal formado: " + completeMessage);
                    }
                }

                try {
                    Thread.sleep(10000);  // Esperar 10 segundos antes de la siguiente lectura
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (SecurityException e) {
            Log.e(TAG, "Permiso de Bluetooth denegado", e);
        } catch (Exception e) {
            Log.e(TAG, "Error en la conexión Bluetooth", e);
        } finally {
            stopSelf();
        }
    }

    private void guardarDatos(int num1, int num2) {
        Map<String, Object> data = new HashMap<>();
        data.put("dni", dniPaciente);
        data.put("dato1", num1);
        data.put("dato2", num2);
        data.put("timestamp", new Date());

        db.collection("mediciones")
                .add(data)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Datos guardados"))
                .addOnFailureListener(e -> Log.e(TAG, "Error al guardar en Firestore", e));
    }
    public void lanzarAlarma(String titulo, String mensaje) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(2, builder.build());
        }
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        try {
            if (inputStream != null) inputStream.close();
            if (bluetoothSocket != null) bluetoothSocket.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al cerrar la conexión", e);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
