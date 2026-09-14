package com.example.tfg.Adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tfg.medico.informes.TiempoRangosM;
import com.example.tfg.paciente.informesP.GProm;
import com.example.tfg.paciente.informesP.MaxminP;

public class ViewPagerAdapter4 extends FragmentStateAdapter {
    String idP;
    public ViewPagerAdapter4(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, @NonNull String id) {
        super(fragmentManager, lifecycle);
        this.idP = id;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment2;
        if (position == 1) {
            fragment2 = new MaxminP();
        } else if(position == 2)  {
            fragment2 = new TiempoRangosM();
        }else  {
            fragment2 = new GProm();
        }

        // Crear y asignar Bundle con idPaciente
        Bundle args = new Bundle();
        args.putString("idPaciente", idP);
        fragment2.setArguments(args);

        return fragment2;
    }
    @Override
    public int getItemCount() {
        return 3;
    }
}