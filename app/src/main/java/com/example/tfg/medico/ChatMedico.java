package com.example.tfg.medico;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tfg.Adapters.ViewPagerAdapter2;
import com.example.tfg.R;
import com.google.android.material.tabs.TabLayout;

public class ChatMedico extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter2 adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_medico, container, false);
        tabLayout = view.findViewById(R.id.tb);
        viewPager2 = view.findViewById(R.id.vp);
        tabLayout.addTab(tabLayout.newTab().setText("Chats"));
        tabLayout.addTab(tabLayout.newTab().setText("Mis Pacientes"));
        FragmentManager fragmentManager = getChildFragmentManager();;
        adapter = new ViewPagerAdapter2(fragmentManager, getLifecycle());
        viewPager2.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
        return view;}

}

