package com.example.tfg.paciente.informesP;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tfg.Adapters.ViewPagerAdapter4;
import com.example.tfg.MainActivity_Pacientes;
import com.example.tfg.R;
import com.example.tfg.util.FileUtils;
import com.google.android.material.tabs.TabLayout;

public class Informes extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ViewPagerAdapter4 adapter;
    private TabLayout t;
    LottieAnimationView l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informes);
        FileUtils fileUtils = new FileUtils();
        String id = fileUtils.readFile(this, "config.txt");
        String idPaciente = id;
        l= findViewById(R.id.atrasIA);
        viewPager = findViewById(R.id.view_pager4);
        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new ViewPagerAdapter4(fragmentManager, getLifecycle(),idPaciente);
        viewPager.setAdapter(adapter);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity_Pacientes.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
    }
}