package com.example.tfg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.tfg.Chatbot.Chatbot;
import com.example.tfg.paciente.AjustesPaciente;
import com.example.tfg.paciente.ChatPaciente;
import com.example.tfg.paciente.Comida;
import com.example.tfg.paciente.DatosPaciente;
import com.example.tfg.paciente.Ejercicio;
import com.example.tfg.paciente.HomePaciente;
import com.example.tfg.paciente.MisCitas;
import com.example.tfg.paciente.PedirCita;
import com.example.tfg.paciente.informesP.Informes;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity_Pacientes extends AppCompatActivity {
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNav;

    NavigationView nv_side;

    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_pacientes);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        drawerLayout = findViewById(R.id.main2);
        nv_side = findViewById(R.id.nv_side2);
        bottomNav = findViewById(R.id.bottom_nav2);
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

                if (item.getItemId() == R.id.snv_logout)
                {
                    try {
                        OutputStreamWriter outputStreamWriter =
                                new OutputStreamWriter(getApplicationContext().openFileOutput("config.txt", Context.MODE_PRIVATE));
                        outputStreamWriter.write(""); // Vacía el archivo
                        outputStreamWriter.close();
                        OutputStreamWriter op =
                                new OutputStreamWriter(getApplicationContext().openFileOutput("tipo.txt", Context.MODE_PRIVATE));
                        op.write(""); // Vacía el archivo
                        op.close();
                    } catch (IOException e) {
                        Log.e("Exception", "File write failed: " + e);
                    }
                    finishAffinity();
                } else if (item.getItemId()== R.id.snv_homep) {
                    replaceFragment(new HomePaciente());

                }else if (item.getItemId()== R.id.snv_ejercicio) {
                    Intent intent = new Intent(MainActivity_Pacientes.this, Ejercicio.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if (item.getItemId()== R.id.snv_comida) {
                    Intent intent = new Intent(MainActivity_Pacientes.this, Comida.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if (item.getItemId()== R.id.snv_pedircita) {
                    replaceFragment(new PedirCita());
                }else if (item.getItemId()== R.id.snv_miscitas) {
                    replaceFragment(new MisCitas());
                }else if (item.getItemId()== R.id.snv_asis) {
                    Intent intent = new Intent(MainActivity_Pacientes.this, Chatbot.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if (item.getItemId()== R.id.snv_chatm) {
                    replaceFragment(new ChatPaciente());
                }else if (item.getItemId()== R.id.snv_graficos) {
                    Intent intent = new Intent(MainActivity_Pacientes.this, Informes.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if (item.getItemId()== R.id.snv_datos) {
                    replaceFragment(new DatosPaciente());
                }
                else if (item.getItemId()== R.id.snv_settings) {
                    replaceFragment(new AjustesPaciente());
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
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.mn_home2) {
                    replaceFragment(new HomePaciente());
                } else if (item.getItemId() == R.id.mn_chat2) {
                    replaceFragment(new ChatPaciente());

                } else if (item.getItemId() == R.id.mn_ia2) {
                    Intent intent = new Intent(MainActivity_Pacientes.this, Comida.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
                return true;
            }
        });
        replaceFragment(new HomePaciente());

    }
    private void replaceFragment(Fragment fragment) {
       /* FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container2, fragment);

        fragmentTransaction.commit();*/
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2,fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}