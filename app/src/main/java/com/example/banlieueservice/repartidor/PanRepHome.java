package com.example.banlieueservice.repartidor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.banlieueservice.R;
import com.example.banlieueservice.actividades.MapaActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.tabs.TabLayout;

import java.util.LinkedList;

public class PanRepHome extends Fragment{
    private AdaptadorDePestanas adaptadorDePestanas;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        return li.inflate(R.layout.fragment_panrephome, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        LinkedList<String> nombres= new LinkedList<>();
        LinkedList<Fragment> contPestanas= new LinkedList<>();

        nombres.add("Pedidos disponibles");
        contPestanas.add(new PanRepPedidosDisp());
        nombres.add("Pedidos realizados");
        contPestanas.add(new PanRepPedidosReal());

        adaptadorDePestanas = new AdaptadorDePestanas(getChildFragmentManager(), nombres, contPestanas);
        viewPager = getView().findViewById(R.id.vpPager);
        //viewPager.setBackground(new ColorDrawable(Color.rgb(254, 205, 26))); //BanAmarillo en decimal
        viewPager.setAdapter(adaptadorDePestanas);
        TabLayout tabLayout = getView().findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackground(new ColorDrawable(Color.rgb(17, 29, 94))); //ban cyan
        tabLayout.setSelectedTabIndicatorColor(Color.rgb(3, 196, 161)); //ban amarillo
    }



    //PARA CREAR PESTAÑAS
    // Since this is an object collection, use a FragmentStatePagerAdapter,
    // and NOT a FragmentPagerAdapter.
    public static class AdaptadorDePestanas extends FragmentStatePagerAdapter {
        //Títulos de las pestañas
        private LinkedList<String> nombres;
        private LinkedList<Fragment> contPestanas;

        public AdaptadorDePestanas(FragmentManager fm, LinkedList<String> nombres, LinkedList<Fragment> contPestanas) {
            super(fm);
            this.nombres= nombres;
            this.contPestanas= contPestanas;
        }

        @Override
        public Fragment getItem(int i) {
            return contPestanas.get(i);
        }

        @Override
        public int getCount() {
            return contPestanas.size(); //Cantidad de pestañas
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return nombres.get(position);
        }
    }

}
