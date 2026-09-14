package com.example.tfg.paciente;

import android.graphics.Color;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tfg.Adapters.HorasAdapter;
import com.example.tfg.R;
import com.example.tfg.util.FileUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class PedirCita extends Fragment {
    private MaterialCalendarView calendarView;
    private CardView cardEndo;

    private RecyclerView recyclerHoras;
    private HorasAdapter adapter;
    private String fechaSeleccionada = "";
    private List<String> listaHoras = new ArrayList<>();
    private List<String> horasOcupadas = new ArrayList<>();
    private final List<CalendarDay> diasNoDisponibles = new ArrayList<>();

    public String idPaciente;
    public String idMedico;
    public String nombrePaciente;
    TextView nombreList;
    public ImageView img;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_pedir_cita, container, false);
        FileUtils fileUtils = new FileUtils();
        idPaciente = fileUtils.readFile(getContext(), "config.txt");
        recyclerHoras = view.findViewById(R.id.rvcita);
        calendarView = view.findViewById(R.id.calendarView1);
        cardEndo = view.findViewById(R.id.cardE);
        nombreList = view.findViewById(R.id.ne);
        img = view.findViewById(R.id.imgendo);
        obtenerEndocrino(idPaciente);
        configurarCalendario();
        configurarRecyclerView();

        return view;
    }
    private void guardarCitaEnServidor(String fecha, String hora) {
        String url = "http://192.168.1.116:3005/guardarCita";
        String fechaCita = fecha + " " + hora;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", idPaciente);
            requestBody.put("fecha", fechaCita);
            requestBody.put("medico", idMedico);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                          //  Toast.makeText(getContext(), "Cita guardada", Toast.LENGTH_SHORT).show();
                            horasOcupadas.add(hora);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Log.e("PA", "ERROR", error)
        );

        queue.add(request);
    }
    public void obtenerEndocrino(String id) {
        String url = "http://192.168.1.116:3005/obtenerMedicoPaciente";
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
                            String nombre = response.getString("nombre");
                            idMedico = response.getString("medico");
                            img.setImageResource(R.drawable.medic);
                            nombreList.setText(nombre);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Log.e("PA", "ERROR", error)
        );

        queue.add(request);
    }
    private void configurarCalendario() {
        Calendar calendar = Calendar.getInstance();
        calendarView.state().edit().setMinimumDate(calendar).commit();

        // Festivos de España
        int[][] festivos = {
                {1, 1}, {6, 1}, {29, 3}, {1, 5}, {15, 8}, {12, 10},
                {1, 11}, {6, 12}, {8, 12}, {25, 12}
        };

        CalendarDay hoy = CalendarDay.today();
        Calendar hoyCal = Calendar.getInstance();
        hoyCal.setTime(hoy.getDate());

        // Recorre los próximos 365 días para deshabilitar fines de semana y festivos
        for (int i = 0; i < 365; i++) {
            Calendar cal = (Calendar) hoyCal.clone();
            cal.add(Calendar.DAY_OF_YEAR, i);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH) + 1;

            // Fin de semana
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                diasNoDisponibles.add(CalendarDay.from(cal));
                continue;
            }

            // Festivo
            for (int[] f : festivos) {
                if (day == f[0] && month == f[1]) {
                    diasNoDisponibles.add(CalendarDay.from(cal));
                    break;
                }
            }
        }

        calendarView.addDecorator(new PedirCita.DisabledDaysDecorator(diasNoDisponibles));

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            int year = date.getYear();
            int month = date.getMonth() + 1;
            int day = date.getDay();
            fechaSeleccionada = String.format("%04d-%02d-%02d", year, month, day);
            cargarHorasDisponibles(fechaSeleccionada);
        });
    }
    private void configurarRecyclerView() {
        recyclerHoras.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new HorasAdapter(listaHoras, horasOcupadas, hora -> mostrarDialogoConfirmacion(hora));
        recyclerHoras.setAdapter(adapter);
    }

    private List<String> generarHorasPosibles() {
        List<String> horas = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 8);
        c.set(Calendar.MINUTE, 30);

        while (c.get(Calendar.HOUR_OF_DAY) < 13) {
            horas.add(String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)));
            c.add(Calendar.MINUTE, 30);
        }

        return horas;
    }

    private void cargarHorasDisponibles(String fecha) {
        listaHoras.clear();
        horasOcupadas.clear();
        listaHoras.addAll(generarHorasPosibles());
        String url = "http://192.168.1.116:3005/horasocupadas ";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", idMedico);
            requestBody.put("fecha", fecha);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray a = response.getJSONArray("lista");
                            for (int i = 0; i < a.length(); i++) {
                                horasOcupadas.add(a.getString(i));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Log.e("PA", "ERROR", error)
        );

        queue.add(request);
    }

    private void mostrarDialogoConfirmacion(String hora) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar cita")
                .setMessage("¿Deseas reservar la cita con " + nombreList.getText() + " el día " + fechaSeleccionada + " a las " + hora + "?")
                .setPositiveButton("Confirmar", (dialog, which) -> guardarCitaEnServidor(fechaSeleccionada, hora))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ✅ Decorador para deshabilitar días
    private static class DisabledDaysDecorator implements DayViewDecorator {
        private final HashSet<CalendarDay> diasDeshabilitados;

        public DisabledDaysDecorator(List<CalendarDay> dias) {
            this.diasDeshabilitados = new HashSet<>(dias);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return diasDeshabilitados.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true);
            view.addSpan(new ForegroundColorSpan(Color.GRAY));
        }
    }
}