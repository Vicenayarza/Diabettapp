package com.example.tfg.medico.informes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tfg.Adapters.ViewPagerAdapter3;
import com.example.tfg.MainActivity;
import com.example.tfg.R;
import com.google.android.material.tabs.TabLayout;

public class GraficosMedico extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ViewPagerAdapter3 adapter;
    private TabLayout t;
    LottieAnimationView l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficos_medico);
        String idPaciente = getIntent().getStringExtra("idPaciente");
        String nombrePaciente = getIntent().getStringExtra("nombrePaciente");
        viewPager = findViewById(R.id.view_pager3);
        t= findViewById(R.id.tab_layout3);
        l= findViewById(R.id.atrasIA);
        t.addTab(t.newTab().setText(nombrePaciente));
        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new ViewPagerAdapter3(fragmentManager, getLifecycle(),idPaciente);
        viewPager.setAdapter(adapter);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });


    }
}