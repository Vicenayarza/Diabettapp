package com.example.tfg;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class BD extends Worker {
    private static final String ROUTE = "http://192.168.1.116:3005";
   //private static final String ROUTE = "http://172.20.10.3:3005";
    public BD (@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    public Result doWork(){
        String action = getInputData().getString("param");
        assert action != null;
        switch (action){
            case "existeUsuarioRegis":{
                HttpURLConnection urlConnection;
                String dni =  getInputData().getString("dni");
                String dir = ROUTE + "/existeUsuarioRegis?dni=" + dni;

                try {
                    URL dest =new URL(dir);
                    urlConnection = (HttpURLConnection) dest.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestMethod("GET");
                    Log.i("JSON", "statuscode: " + urlConnection.getResponseCode());
                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == 200) {
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            result.append(line);
                        }
                        inputStream.close();

                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(result.toString());
                        Log.i("JSON", "doWork: " + json);

                        Boolean success = (Boolean) json.get("success");
                        Boolean busqueda = (Boolean) json.get("busq");
                        Data.Builder b = new Data.Builder();
                        b.putBoolean("existe",success);
                        b.putBoolean("puede",busqueda);
                        return Result.success(b.build());
                    }
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
            }
            case "Registrar":{
                String dir = ROUTE + "/crearUsu";
                HttpURLConnection urlConnection;

                String nombre = getInputData().getString("nombre");
                String apellidos = getInputData().getString("apellidos");
                String dni = getInputData().getString("dni");
                String nacimiento = getInputData().getString("fecha");
                String contraseña = getInputData().getString("contra");
                String correo =  getInputData().getString("correo");
                String altura =  getInputData().getString("altura");
                String peso =  getInputData().getString("peso");
                int primera = 1;
                try {
                    URL dest =new URL(dir);
                    urlConnection = (HttpURLConnection) dest.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type","application/json");
                    JSONObject paramJson = new JSONObject();
                    paramJson.put("Nombre", nombre);
                    paramJson.put("Apellidos", apellidos);
                    paramJson.put("DNI",dni);
                    paramJson.put("Nacimiento",nacimiento);
                    paramJson.put("Contraseña",contraseña);
                    paramJson.put("Correo",correo);
                    paramJson.put("Altura",altura);
                    paramJson.put("Peso",peso);
                    paramJson.put("Primera",primera);

                    PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                    out.print(paramJson.toString());
                    out.close();
                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == 200) {
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            result.append(line);
                        }
                        inputStream.close();

                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(result.toString());
                        Log.i("JSON", "doWork: " + json);

                        Boolean success = (Boolean) json.get("success");
                        Data.Builder b = new Data.Builder();
                        return Result.success(b.putBoolean("exito", success).build());
                    }
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
            }
            case "existeUsu":{
                HttpURLConnection urlConnection;
                String usu =  getInputData().getString("usuario");
                String contra = getInputData().getString("contraseña");
                int tipo = getInputData().getInt("tipo",0);
                String dir = ROUTE + "/existeUsu?usuario=" + usu+ "&contra=" +contra+ "&tipo=" +tipo;

                try {
                    URL dest =new URL(dir);
                    urlConnection = (HttpURLConnection) dest.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestMethod("GET");
                    int statusCode = urlConnection.getResponseCode();
                    Log.d("code",String.valueOf(statusCode));
                    if (statusCode == 200) {
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            result.append(line);
                        }
                        inputStream.close();

                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(result.toString());
                        Log.i("JSON", "doWork: " + json);

                        Boolean success = (Boolean) json.get("success");
                        Long prim = (Long) json.get("Primera");
                        int primera = prim.intValue();
                        Data.Builder b = new Data.Builder();
                        b.putBoolean("existe",success);
                        b.putInt("primera",primera);
                        return Result.success(b.build());
                    }
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
            }
            case "modificarContra":{
                HttpURLConnection urlConnection;
                String dir = ROUTE + "/modificarContra";
                String usu =  getInputData().getString("usuario");
                String contra = getInputData().getString("contra");
                int tipo = getInputData().getInt("tipo",0);
                int primera = 1;

                try {
                    URL dest =new URL(dir);
                    urlConnection = (HttpURLConnection) dest.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type","application/json");
                    JSONObject paramJson = new JSONObject();
                    paramJson.put("id", usu);
                    paramJson.put("contra", contra);
                    paramJson.put("tipo", tipo);
                    paramJson.put("primera", primera);
                    Log.d("Cambio contra Prueba",""+ paramJson);
                    PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                    out.print(paramJson.toString());
                    out.close();
                    int statusCode = urlConnection.getResponseCode();
                    String code =String.valueOf(statusCode);
                    Log.d("prueba modificar",code);
                    if (statusCode == 200) {
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            result.append(line);
                        }
                        inputStream.close();
                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(result.toString());
                        Log.i("JSON", "doWork: " + json);

                        Data.Builder b = new Data.Builder();
                        return Result.success(b.putBoolean("exito",(boolean) json.get("success")).build());
                    }
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
            }
            case "buscarCorreo":{
                HttpURLConnection urlConnection;
                String id =  getInputData().getString("id");
                int tipo = getInputData().getInt("tipo",0);
                String dir = ROUTE + "/buscarCorreo?id=" +id+ "&tipo" +tipo;
                try {
                    URL dest =new URL(dir);
                    urlConnection = (HttpURLConnection) dest.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setRequestMethod("GET");
                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == 200) {
                        BufferedInputStream inputStream =
                                new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream,
                                        "UTF-8"));
                        String line;
                        StringBuilder result = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            result.append(line);
                        }
                        inputStream.close();

                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(result.toString());
                        Log.i("JSON", "doWork: " + json);

                        Boolean success = (Boolean) json.get("success");
                        String mail = (String) json.get("mail");
                        Data.Builder b = new Data.Builder();
                        b.putBoolean("existe",success);
                        b.putString("mail",mail);
                        return Result.success(b.build());
                    }
                } catch (Exception e) {
                    Log.e("EXCEPTION", "doWork: ", e);
                    return Result.failure();
                }
                break;
            }



        }
        return Result.success();
    }
}
