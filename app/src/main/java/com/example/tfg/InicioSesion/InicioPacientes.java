package com.example.tfg.InicioSesion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.tfg.BD;
import com.example.tfg.MainActivity_Pacientes;
import com.example.tfg.R;
import com.example.tfg.RandomTextGenerator;
import com.example.tfg.Registro;
import com.example.tfg.util.Appdata;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class InicioPacientes extends Fragment {

    private RandomTextGenerator randomTextGenerator;
    private String token;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio_pacientes, container, false);
        EditText usuario = view.findViewById(R.id.usuariopaciente);
        EditText contrasena = view.findViewById(R.id.passpaciente);
        TextView registrar = view.findViewById(R.id.cuenta);
        TextView olvidar = view.findViewById(R.id.olvidopaciente);
        Button inicio = view.findViewById(R.id.loginpaciente);

        inicio.setOnClickListener(v -> {
            String usu = usuario.getText().toString();
            String contra = contrasena.getText().toString();
            obtenerPaciente(usu,contra);
        });
        registrar.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Registro.class);
            startActivity(intent);

        });
        olvidar.setOnClickListener(v -> {
            mostrarDialogoolvido();
        });
        return view;
    } public void obtenerPaciente(String usu, String contra) {
        Data param = new Data.Builder()
                .putString("param", "existeUsu")
                .putString("usuario",usu)
                .putString("contraseña", contra)
                .putInt("tipo",0).build();
        Log.d("Prueba inicio", "" + param);
        OneTimeWorkRequest oneTimeWorkRequest =
                new OneTimeWorkRequest.Builder(BD.class).setInputData(param).build();
        WorkManager.getInstance(getContext()).enqueue(oneTimeWorkRequest);
        WorkManager.getInstance(getContext())
                .getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                .observe(getViewLifecycleOwner(), workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        if (workInfo.getState() != WorkInfo.State.SUCCEEDED) {
                           /* Toast.makeText(getContext(), "ERROR",
                                    Toast.LENGTH_LONG).show();*/
                        } else {
                            Data d = workInfo.getOutputData();
                            boolean b = d.getBoolean("existe", false);
                            int prim = d.getInt("primera",0);
                            if (b) {
                                if(prim == 0){
                                    mostrarDialogocambio(usu);
                                }else{
                                  /*  Toast.makeText(getContext(), "existe un " +
                                            "usuario", Toast.LENGTH_LONG).show();*/
                                    Intent intent;
                                    guardarToken(usu);
                                    intent = new Intent(getContext(),
                                            MainActivity_Pacientes.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    saveSession(usu);
                                    startActivity(intent);
                                }

                            } else {
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(getContext());
                                builder.setTitle("Usuario o contraseña incorrectos");
                                builder.setMessage("Introduce el usuario o contraseña " +
                                        "correctamente o registrese en caso de no tener" +
                                        " un usuario creado");
                                builder.setPositiveButton("Volver", (dialogInterface,
                                                                     i) -> {
                                    Intent intent = new Intent(getContext(),
                                            InicioSesion.class);
                                    startActivity(intent);
                                });
                                AlertDialog alert = builder.create();
                                alert.show();


                            }
                        }
                    }
                });
    }

    public void saveSession(String id) {
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(requireContext().openFileOutput("config.txt",
                            Context.MODE_PRIVATE));
            outputStreamWriter.write(id);
            outputStreamWriter.close();
            OutputStreamWriter outputStreamWriter2 =
                    new OutputStreamWriter(requireContext().openFileOutput("tipo.txt",
                            Context.MODE_PRIVATE));
            outputStreamWriter2.write("0");
            outputStreamWriter2.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e);
        }
    }
    public void mostrarDialogocambio(String usuario){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Cambiar Contraseña");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        // Configurar los botones del diálogo
        builder.setPositiveButton("Cambiar", (dialog, which) -> {
            String nuevaContra = input.getText().toString();
            if(nuevaContra.isEmpty() || !validarContra(nuevaContra) || nuevaContra.length() < 8){
               /* Toast.makeText(getContext(), "Introduce una contraseña " +
                        "válida", Toast.LENGTH_LONG).show();*/
            }else{
                cambiarContraseña(usuario, nuevaContra);
                Intent intent;
                intent = new Intent(getContext(),
                        MainActivity_Pacientes.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                saveSession(usuario);
                startActivity(intent);
            }

        });

        builder.show();
    }
    public void cambiarContraseña(String usuario, String nuevaContra){
        Data param = new Data.Builder()
                .putString("param", "modificarContra")
                .putString("usuario", usuario)
                .putString("contra", nuevaContra)
                .putInt("tipo",0)
                .build();
        Log.d("Prueba Contra", "" + param);
        OneTimeWorkRequest oneTimeWorkRequest =
                new OneTimeWorkRequest.Builder(BD.class).setInputData(param).build();
        WorkManager.getInstance(getContext()).enqueue(oneTimeWorkRequest);
        WorkManager.getInstance(getContext()).getWorkInfoByIdLiveData(oneTimeWorkRequest.getId()).observe(getViewLifecycleOwner(), new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo != null && workInfo.getState().isFinished()) {
                    if (workInfo.getState() != WorkInfo.State.SUCCEEDED) {
                      /*  Toast.makeText(getContext(), "Error",
                                Toast.LENGTH_LONG).show();*/
                    } else {
                        Data d = workInfo.getOutputData();
                        boolean b = d.getBoolean("exito", false);
                        if (b) {
                          /*  Toast.makeText(getContext(), "contra " +
                                    "cambiada", Toast.LENGTH_LONG).show();*/

                        } else {
                           /* Toast.makeText(getContext(), "Error",
                                    Toast.LENGTH_LONG).show();*/
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
    public void mostrarDialogoolvido(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Introduce tu identificador");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        // Configurar los botones del diálogo
        builder.setPositiveButton("Enviar", (dialog, which) -> {
            String id = input.getText().toString();
           buscarCorreo(id);

        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();


    }
    public void enviarMensaje(String mail,String id){
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", Appdata.Gmail_Host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth","true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Appdata.Sender_Email_Address,Appdata.Sender_Email_Password);
            }
        });
        MimeMessage message = new MimeMessage(session);
        try{
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(mail));
            message.setSubject(Appdata.asunto);
            randomTextGenerator = new RandomTextGenerator();
            String contra = randomTextGenerator.generateRandomText(12);
            message.setText("Su nueva contraseña es  "+contra );
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Transport.send(message);
                    }catch(MessagingException e){
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            cambiarContraseña(id,contra);
            Intent intent;
            intent = new Intent(getContext(),
                    InicioSesion.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch(MessagingException e){
            throw new RuntimeException(e);
        }


    }
    public void buscarCorreo(String id){
        Data param = new Data.Builder()
                .putString("param", "buscarCorreo")
                .putString("id", id)
                .putInt("tipo",0)
                .build();

        OneTimeWorkRequest oneTimeWorkRequest =
                new OneTimeWorkRequest.Builder(BD.class).setInputData(param).build();
        WorkManager.getInstance(getContext()).getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                .observe(getViewLifecycleOwner(), workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        if (workInfo.getState() != WorkInfo.State.SUCCEEDED) {
                           /* Toast.makeText(getContext(), "Problema",
                                    Toast.LENGTH_LONG).show();*/
                        } else {
                            Data d = workInfo.getOutputData();
                            boolean b = d.getBoolean("existe", false);
                            String mail = d.getString("mail");
                            Log.d("Prueba Mail", "" + mail);
                            if (b) {

                                enviarMensaje(mail,id);
                            }

                        }


                    }

                });

        WorkManager.getInstance(getContext()).enqueue(oneTimeWorkRequest);

    }

    public void guardarToken(String usuario){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("ERR_TOKEN", "onCreate"
                                , task.getException());
                        return;
                    }
                    token = task.getResult();
                });
        FirebaseFirestore.getInstance().collection("tokens")
                .document(usuario) // ID único del usuario
                .update("token", token);


    }





}