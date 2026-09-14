package com.example.tfg.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tfg.medico.chat.ListaChat;
import com.example.tfg.medico.chat.ListaTodos;

public class ViewPagerAdapter2 extends FragmentStateAdapter {
    public ViewPagerAdapter2(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1){
            return new ListaTodos();
        }
        return new ListaChat();
    }
    @Override
    public int getItemCount() {
        return 2;
    }
}