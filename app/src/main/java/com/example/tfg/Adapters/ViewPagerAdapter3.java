package com.example.tfg.Adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tfg.medico.informes.DatosPacienteMedico;
import com.example.tfg.medico.informes.GHOYMedico;
import com.example.tfg.medico.informes.GPromedioMedicos;
import com.example.tfg.medico.informes.MaxMinDiarios;
import com.example.tfg.medico.informes.TiempoRangosM;

public class ViewPagerAdapter3 extends FragmentStateAdapter {
    String idP;
    public ViewPagerAdapter3(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, @NonNull String id) {
        super(fragmentManager, lifecycle);
        this.idP = id;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (position == 1) {
            fragment = new GHOYMedico();
        } else if(position == 2)  {
            fragment = new GPromedioMedicos();
        }else if(position == 3)  {
            fragment = new MaxMinDiarios();
        }else if(position == 4)  {
            fragment = new TiempoRangosM();
        }else  {
            fragment = new DatosPacienteMedico();
        }

        // Crear y asignar Bundle con idPaciente
        Bundle args = new Bundle();
        args.putString("idPaciente", idP);
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public int getItemCount() {
        return 5;
    }
}