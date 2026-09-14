package com.example.tfg.paciente;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tfg.Adapters.LanguajeAdapter;
import com.example.tfg.BD;
import com.example.tfg.Modelos.LanguajeItem;
import com.example.tfg.R;
import com.example.tfg.util.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AjustesPaciente extends Fragment {
    public TextView mail,user,fecha,name;
    public EditText contr;
    private Spinner spinner;
    public Button guardar;
    ImageView img;
    private boolean firstCall = true;
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_LANGUAGE = "selected_language";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ajustes_paciente, container, false);
        FileUtils fileUtils = new FileUtils();
        String id = fileUtils.readFile(getContext(), "config.txt");
        name =  view.findViewById(R.id.ajp);
        user = view.findViewById(R.id.us);
        mail = view.findViewById(R.id.mail);
        fecha = view.findViewById(R.id.fecha);
        contr = view.findViewById(R.id.contraN);
        spinner = view.findViewById(R.id.spinneridiomaP);
        guardar = view.findViewById(R.id.guar);
        img = view.findViewById(R.id.imageViewvv);
        img.setImageResource(R.drawable.av);
        obtenerDatos(id);
        List<LanguajeItem> languageList = new ArrayList<>();
        languageList.add(new LanguajeItem(R.drawable.spain, "Español"));
        languageList.add(new LanguajeItem(R.drawable.eeuu, "English"));
        languageList.add(new LanguajeItem(R.drawable.france, "French"));

        LanguajeAdapter adapter = new LanguajeAdapter(requireContext(), languageList);
        spinner.setAdapter(adapter);

        // Carga el idioma guardado, si no existe carga el idioma actual del sistema
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedLang = prefs.getString(KEY_LANGUAGE, Locale.getDefault().getLanguage());

        // Establece el spinner según el idioma guardado
        int pos = getPositionByLangCode(savedLang, languageList);
        spinner.setSelection(pos);
        guardar.setOnClickListener(v -> {
            String c = contr.getText().toString();
            StringBuilder errorMessage = new StringBuilder();
            if(c.isEmpty()){
                Toast.makeText(getContext(), "Deben estar todos los " +
                        "campos rellenados", Toast.LENGTH_LONG).show();

            }else{
                if(c.length() <8 || !validarContra(c)){
                    errorMessage.append("La contraseña debe tener mínimo 8 caracteres,números y letras.\n");
                }if (errorMessage.length() > 0) {
                    Toast.makeText(getContext(), errorMessage.toString(), Toast.LENGTH_LONG).show();
                } else {
                    cambiarContraseña(id,c);
                }

            }

        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstCall) {
                    firstCall = false; // Ignora la primera llamada onItemSelected al establecer selección inicial
                    return;
                }

                LanguajeItem selected = languageList.get(position);
                String langCode = getLangCodeByName(selected.languageName);

                if (!langCode.equals(savedLang)) {
                    // Guarda la preferencia
                    prefs.edit().putString(KEY_LANGUAGE, langCode).apply();

                    // Cambia idioma
                    cambiarIdioma(langCode);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }
    public void obtenerDatos(String id){
        String url = "http://" + "172.20.10.3" + ":3005/obtenerDatosPaciente";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", id);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            String n = response.getString("nombre");
                            String c = response.getString("correo");
                            String f = response.getString("fecha");
                            name.setText(n);
                            user.setText(id);
                            mail.setText(c);
                            fecha.setText(f);

                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Log.e("PA", "ERROR", error)
        );
        queue.add(request);
    }
    private int getPositionByLangCode(String langCode, List<LanguajeItem> languages) {
        for (int i = 0; i < languages.size(); i++) {
            if (getLangCodeByName(languages.get(i).languageName).equals(langCode)) {
                return i;
            }
        }
        return 0; // Por defecto primera posición (Spanish)
    }

    private String getLangCodeByName(String languageName) {
        switch (languageName) {
            case "Spanish":
                return "es";
            case "English":
                return "en";
            case "French":
                return "fr";
            default:
                return "es";
        }
    }

    private void cambiarIdioma(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Context context = requireContext();
        Resources res = context.getResources();
        Configuration config = res.getConfiguration();

        config.setLocale(locale);

        Context newContext = context.createConfigurationContext(config);

        // Si quieres actualizar tu fragment o activity, recarga usando el contexto nuevo:
        if (getActivity() != null) {
            getActivity().recreate();
        }
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
                        Toast.makeText(getContext(), "Error",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Data d = workInfo.getOutputData();
                        boolean b = d.getBoolean("exito", false);
                        if (b) {
                            Toast.makeText(getContext(), "contra " +
                                    "cambiada", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getContext(), "Error",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }
}