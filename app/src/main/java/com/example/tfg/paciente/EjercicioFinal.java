package com.example.tfg.paciente;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tfg.R;

import java.util.Locale;

public class EjercicioFinal extends AppCompatActivity {
String deporte,minutos;
LottieAnimationView lot;
TextView dep, countdownText, pulsaciones,glucosa;
int min;
private CountDownTimer timer;
private long timeLeftInMillis;
Button start, reset;
private boolean timerRunning;
private long startTimeInMillis;
LottieAnimationView l;
private BroadcastReceiver bluetoothReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicio_final);
        deporte = getIntent().getStringExtra("deporte");
        minutos = getIntent().getStringExtra("minutos");
        min = Integer.parseInt(minutos);
        startTimeInMillis = min * 60 * 1000; // minutos a milisegundos
        timeLeftInMillis = startTimeInMillis;
        dep =findViewById(R.id.sport);
        dep.setText(deporte);
        lot = findViewById(R.id.ddp);
        l= findViewById(R.id.ia3);
        start= findViewById(R.id.start_pause);
        reset = findViewById(R.id.reset);
        countdownText = findViewById(R.id.countdown_timer);
        pulsaciones = findViewById(R.id.pulsaciones);
        glucosa = findViewById(R.id.gluco);
        switch (deporte) {
            case "Caminar":
                lot.setAnimation(R.raw.andar);
                break;
            case "Ciclismo":
                lot.setAnimation(R.raw.bici);
                break;
            case "Correr":
                lot.setAnimation(R.raw.run);
                break;
            case "Padel":
                lot.setAnimation(R.raw.tenis);
                break;
            case "Natacion":
                lot.setAnimation(R.raw.nadar);
                break;
        }
        lot.playAnimation(); // Iniciar animación
        updateCountDownText(); // Mostrar tiempo inicial
        start.setOnClickListener(v -> {
            if (timerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        // Botón Reset
        reset.setOnClickListener(v -> resetTimer());
        bluetoothReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int num1 = intent.getIntExtra("num1", 0);
                int num2 = intent.getIntExtra("num2", 0);
                String g = String.valueOf(num1);
                String p = String.valueOf(num2);
                glucosa.setText(g + "mg/dL");
                pulsaciones.setText(p);
            }
        };
        registerReceiver(bluetoothReceiver, new IntentFilter("BLUETOOTH_DATA"));
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Ejercicio.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothReceiver != null) {
            unregisterReceiver(bluetoothReceiver);
        }
    }

    private void startTimer() {
        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                start.setText("Start");
                start.setVisibility(View.INVISIBLE);
                reset.setVisibility(View.VISIBLE);
                countdownText.setText("00:00:00");
                lot.pauseAnimation(); // detener animación
            }
        }.start();

        timerRunning = true;
        start.setText("Pause");
        reset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer() {
        timer.cancel();
        timerRunning = false;
        start.setText("Resume");
        reset.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        timeLeftInMillis = startTimeInMillis;
        updateCountDownText();
        reset.setVisibility(View.INVISIBLE);
        start.setVisibility(View.VISIBLE);
        start.setText("Start");
    }

    private void updateCountDownText() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        countdownText.setText(timeFormatted);
    }

}