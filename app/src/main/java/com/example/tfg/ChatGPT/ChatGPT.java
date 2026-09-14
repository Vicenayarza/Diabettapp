package com.example.tfg.ChatGPT;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.tfg.Chatbot.ResponseCallBack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGPT {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final OkHttpClient client = new OkHttpClient();



    public static void getResponse(String query, ResponseCallBack callBack) {
        try {
            // Construir el JSON con el prompt
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", query);
            messages.put(userMessage);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("messages", messages);
            jsonBody.put("temperature", 0.7);

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json"));

            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + BuildConfiguracion.api)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    new Handler(Looper.getMainLooper()).post(() -> callBack.onError("Error de red: " + e.getMessage()));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body().string(); // debe llamarse solo una vez
                        Log.e("ChatGPT", "Código HTTP: " + response.code() + " - Cuerpo: " + errorBody);
                        new Handler(Looper.getMainLooper()).post(() -> callBack.onError("Error HTTP: " + response.code()));
                        return;
                    }

                    String respBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(respBody);
                        String reply = jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        new Handler(Looper.getMainLooper()).post(() -> callBack.onResponse(reply.trim()));

                    } catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(() -> callBack.onError("Error parseando JSON: " + e.getMessage()));
                    }
                }
            });

        } catch (Exception e) {
            callBack.onError("Error creando JSON: " + e.getMessage());
        }
    }
}