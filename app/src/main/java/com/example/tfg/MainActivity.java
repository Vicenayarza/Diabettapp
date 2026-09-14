package com.example.tfg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tfg.Chatbot.Chatbot;
import com.example.tfg.InicioSesion.InicioSesion;
import com.example.tfg.medico.AgendaMedico;
import com.example.tfg.medico.AjustesMedico;
import com.example.tfg.medico.ChatMedico;
import com.example.tfg.medico.CitarMedico;
import com.example.tfg.medico.HomeMedico;
import com.example.tfg.medico.ListaMedico;
import com.example.tfg.util.FileUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNav;

    NavigationView nv_side;

    ActionBarDrawerToggle toggle;

    private int userType = -1;

    private static final String DEFAULT_LANGUAGE = "default";
    private static final String DEFAULT_MODE = "default";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FileUtils fu = new FileUtils();
        if (!fu.sessionExists(this, "config.txt")) {
            Intent intent = new Intent(this, InicioSesion.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else {
            String tipo = fu.readFile(this, "tipo.txt");
            Log.d("tipo",tipo);
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            String idioma = sharedPreferences.getString("idioma", DEFAULT_LANGUAGE);
            String modo = sharedPreferences.getString("modo", DEFAULT_MODE);
            setLanguage(idioma);
            setModo(modo);
            String m = "0";
            if(tipo.equals(m)){
                Intent intent = new Intent(this, MainActivity_Pacientes.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else{
                replaceFragment(new HomeMedico());
            }

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            drawerLayout = findViewById(R.id.main);
            nv_side = findViewById(R.id.nv_side);
            bottomNav = findViewById(R.id.bottom_nav);
            toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            toggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, R.color.a1));
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.a1));
            getSupportActionBar().setTitle("Diabettapp");
            //getSupportActionBar().setLogo(R.drawable.diabetapp);

            nv_side.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    
                    if (item.getItemId() == R.id.snv_home)
                    {
                        replaceFragment(new HomeMedico());
                    } else if (item.getItemId()== R.id.snv_agenda) {
                        replaceFragment(new AgendaMedico());
                        
                    } else if (item.getItemId()==R.id.snv_citar) {
                        replaceFragment(new CitarMedico());

                    }else if (item.getItemId()==R.id.snv_pacientes) {
                        replaceFragment(new ListaMedico());

                    }else if (item.getItemId()==R.id.snv_chats) {
                        replaceFragment(new ChatMedico());

                    }else if (item.getItemId()==R.id.snv_asistente) {
                        Intent intent = new Intent(MainActivity.this, Chatbot.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }else if (item.getItemId()==R.id.snv_ajustes) {
                        replaceFragment(new AjustesMedico());

                    }else if (item.getItemId()==R.id.snv_cerrar) {
                        try {
                            OutputStreamWriter outputStreamWriter =
                                    new OutputStreamWriter(getApplicationContext().openFileOutput("config.txt", Context.MODE_PRIVATE));
                            outputStreamWriter.write(""); // Vacía el archivo
                            outputStreamWriter.close();
                        } catch (IOException e) {
                            Log.e("Exception", "File write failed: " + e);
                        }
                        finishAffinity();


                    }

                    drawerLayout.closeDrawers();
                    return true;
                }
            });

            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (drawerLayout.isOpen())
                    {
                        drawerLayout.closeDrawers();
                    }
                    else
                    {
                        finishAffinity();
                    }
                }
            });

        }
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.mn_home) {
                    replaceFragment(new HomeMedico());
                } else if (item.getItemId() == R.id.mn_chat) {
                    replaceFragment(new ChatMedico());

                } else if (item.getItemId() == R.id.mn_ia) {
                    Intent intent = new Intent(MainActivity.this, Chatbot.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
                return true;
            }
        });
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment);

        fragmentTransaction.commit();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        configuration.setLocale(locale);

        resources.updateConfiguration(configuration, displayMetrics);

        // requireActivity().recreate(); // Reinicia la actividad para que se aplique el nuevo idioma
    }

    public void setModo(String modo){
        if(modo.equals("oscuro")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (modo.equals("claro")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }

    }
    private int parseUserType(String tipo) {
        try {
            return Integer.parseInt(tipo.trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.e("MainActivity", "Error parsing user type: " + e.getMessage());
            return -1;  // Valor por defecto en caso de error
        }
    }


}