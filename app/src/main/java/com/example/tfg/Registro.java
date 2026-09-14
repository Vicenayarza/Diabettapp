package com.example.tfg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.tfg.InicioSesion.InicioSesion;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {
    private TabLayout tabLayout;
    private EditText etNombre,etApellidos,etDni,etFecha,etContra1,etContra2,etCorreo;
    private EditText etAltura,etPeso;
    private Button btnAtras,btnGuardar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        ViewFlipper viewFlipper = findViewById(R.id.view_flipper);
        LayoutInflater inflater = LayoutInflater.from(this);
        viewFlipper.addView(inflater.inflate(R.layout.datospersonales, null));
        viewFlipper.addView(inflater.inflate(R.layout.cuenta, null));
        viewFlipper.addView(inflater.inflate(R.layout.datosfisicos, null));
        tabLayout = findViewById(R.id.tab_layout2);
        tabLayout.addTab(tabLayout.newTab().setText("Personales"));
        tabLayout.addTab(tabLayout.newTab().setText("Cuenta"));
        tabLayout.addTab(tabLayout.newTab().setText("Fisicos"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewFlipper.setDisplayedChild(tab.getPosition());;
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        etNombre = findViewById(R.id.nombre);
        etApellidos = findViewById(R.id.apellidos);
        etDni = findViewById(R.id.Dni);
        etFecha = findViewById(R.id.fecha);
        etCorreo = findViewById(R.id.correo);
        etContra1 = findViewById(R.id.contra);
        etContra2 = findViewById(R.id.contra2);
        etAltura = findViewById(R.id.altura);
        etPeso = findViewById(R.id.peso);
        btnGuardar= findViewById(R.id.btnguar);
        btnAtras = findViewById(R.id.guard);
        btnAtras.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), InicioSesion.class);
            startActivity(intent);
        });
        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            String apellidos = etApellidos.getText().toString();
            String Dni = etDni.getText().toString();
            String fecha = etFecha.getText().toString();
            String correo = etCorreo.getText().toString();
            String contra =  etContra1.getText().toString();
            String contra2 = etContra2.getText().toString();
            String altura = etAltura.getText().toString();
            String peso = etPeso.getText().toString();
            StringBuilder errorMessage = new StringBuilder();
            if(nombre.isEmpty() || apellidos.isEmpty() || Dni.isEmpty() || fecha.isEmpty() || correo.isEmpty()
                    || contra.isEmpty() || contra2.isEmpty() || altura.isEmpty() || peso.isEmpty()){
                Toast.makeText(getApplicationContext(), "Deben estar todos los " +
                        "campos rellenados", Toast.LENGTH_LONG).show();

            }else{
                if(!esPaciente(Dni)){errorMessage.append("Ingrese un número de DNI válido.\n"); }
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if(!pattern.matcher(correo).matches()){
                    errorMessage.append("Ingrese un mail válido.\n");
                }
                if(contra.length() <8 || !validarContra(contra)){
                    errorMessage.append("La contraseña debe tener mínimo 8 caracteres,números y letras.\n");
                }
                if(!contra.equals(contra2)){
                    errorMessage.append("Las contraseñas no son iguales.\n");
                }
                if (errorMessage.length() > 0) {
                    Toast.makeText(getApplicationContext(), errorMessage.toString(), Toast.LENGTH_LONG).show();
                } else {
                    existeUsuario(nombre,apellidos,Dni,fecha,contra,correo,altura,peso);
                }


            }

        });

    }public void existeUsuario(String nombre,String apellidos,String Dni,String fecha,String contra,String correo,String altura,String peso){
        Data param = new Data.Builder()
                .putString("param", "existeUsuarioRegis")
                .putString("dni", Dni).build();
        OneTimeWorkRequest oneTimeWorkRequest =
                new OneTimeWorkRequest.Builder(BD.class).setInputData(param).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        if (workInfo.getState() != WorkInfo.State.SUCCEEDED) {
                            Toast.makeText(getApplicationContext(), "Problema",
                                    Toast.LENGTH_LONG).show();

                        } else {
                            Data d = workInfo.getOutputData();
                            boolean b = d.getBoolean("existe", false);
                            boolean c = d.getBoolean("puede", false);
                            if (b) {
                                Toast.makeText(getApplicationContext(), "existe un " +
                                        "usuario", Toast.LENGTH_LONG).show();
                            } else {
                                if(!c){
                                    Toast.makeText(getApplicationContext(), "No estás registrado en el sistema como " +
                                            "diabético", Toast.LENGTH_LONG).show();

                                }else{
                                    guardarUsuario(nombre,apellidos,Dni, fecha,contra,
                                            correo,altura,peso);
                                    Toast.makeText(getApplicationContext(),
                                            "Usuario valido",
                                            Toast.LENGTH_LONG).show();
                                }

                            }

                        }
                    }

                });

        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);

    }
    public void guardarUsuario(String nombre,String apellidos,String Dni,String fecha,String contra,
                               String correo, String altura,String peso){
        Data param = new Data.Builder()
                .putString("param", "Registrar")
                .putString("nombre", nombre)
                .putString("apellidos", apellidos)
                .putString("dni", Dni)
                .putString("fecha", fecha)
                .putString("contra", contra)
                .putString("correo",correo)
                .putString("altura",altura)
                .putString("peso", peso).build();
        OneTimeWorkRequest oneTimeWorkRequest =
                new OneTimeWorkRequest.Builder(BD.class).setInputData(param).build();
        WorkManager.getInstance(Registro.this).enqueue(oneTimeWorkRequest);
        WorkManager.getInstance(Registro.this).getWorkInfoByIdLiveData(oneTimeWorkRequest.getId()).observe(Registro.this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo != null && workInfo.getState().isFinished()) {
                    if (workInfo.getState() != WorkInfo.State.SUCCEEDED) {
                        Toast.makeText(getApplicationContext(), "Error",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Data d = workInfo.getOutputData();
                        boolean b = d.getBoolean("exito", false);
                        if (b) {
                            Intent intent = new Intent(getApplicationContext(),
                                    InicioSesion.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Registrado " +
                                    "exitosamente", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Error",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });


    }
    public boolean validarContra(String password) {
        boolean numeros = false;
        boolean letras = false;
        for (int x = 0; x < password.length(); x++) {
            char c = password.charAt(x);
            // Si no está entre a y z, ni entre A y Z, ni es un espacio
            if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == 'ñ' || c == 'Ñ'
                    || c == 'á' || c == 'é' || c == 'í' || c == 'ó' || c == 'ú'
                    || c == 'Á' || c == 'É' || c == 'Í' || c == 'Ó' || c == 'Ú')) {
                letras = true;
            }
            if ((c >= '0' && c <= '9')) {
                numeros = true;
            }

        }
        return numeros && letras;
    }
   /* public boolean validarContra(String password) {
        if (password == null || password.length() < 8) {
            return false; // longitud mínima
        }

        boolean tieneMayuscula = false;
        boolean tieneMinuscula = false;
        boolean tieneNumero = false;
        boolean tieneEspecial = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (Character.isUpperCase(c)) {
                tieneMayuscula = true;
            } else if (Character.isLowerCase(c)) {
                tieneMinuscula = true;
            } else if (Character.isDigit(c)) {
                tieneNumero = true;
            } else if ("!@#$%^&*()-_=+[]{}|;:',.<>?/`~".indexOf(c) >= 0) {
                tieneEspecial = true;
            }
        }

        return tieneMayuscula && tieneMinuscula && tieneNumero && tieneEspecial;
    }*/
    private String twoDigits(int n) {
        return (n <= 9) ? ("0" + n) : String.valueOf(n);
    }
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker,
                                                                         year, month,
                                                                         day) -> {
            // +1 ya que Enero es 0
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, day);


        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public boolean esPaciente(String usuario) {
        return usuario.matches("\\d{8}");
    }


}