package com.example.banlieueservice.usuario;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.banlieueservice.R;

import com.example.banlieueservice.actividades.MapaActivity;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.repartidor.PanRepInicio;
import com.google.android.material.tabs.TabLayout;

import java.util.LinkedList;
import java.util.Map;

public class PanUsInicio extends Fragment {
    private Button btnSat;
    private Mensaje mje;
    private Context ctx;
    private FragmentActivity act;
    private Map<String, String> datosUsuario;

    private PanRepInicio.AdaptadorDePestanas adaptadorDePestanas;
    private ViewPager viewPager;

    public PanUsInicio(Map<String, String> datosUsuario){
        this.datosUsuario= datosUsuario;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);
        return li.inflate(R.layout.fragment_panusinicio, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);

        LinkedList<String> nombres= new LinkedList<>();
        LinkedList<Fragment> contPestanas= new LinkedList<>();

        nombres.add("Mapa");
        MapaActivity mapaActivity= new MapaActivity();
        contPestanas.add( mapaActivity );

        nombres.add("Establecimientos Banlieue");
        PanUsSelectorLugar selectorDeLugar= new PanUsSelectorLugar(mapaActivity);
        selectorDeLugar.sendData(datosUsuario);
        contPestanas.add( selectorDeLugar );

        adaptadorDePestanas = new PanRepInicio.AdaptadorDePestanas(getChildFragmentManager(), nombres, contPestanas);
        viewPager = getView().findViewById(R.id.vpPager);
        //viewPager.setBackground(new ColorDrawable(Color.rgb(254, 205, 26))); //BanAmarillo en decimal
        viewPager.setAdapter(adaptadorDePestanas);
        TabLayout tabLayout = getView().findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackground(new ColorDrawable(Color.rgb(17, 29, 94))); //ban cyan
        tabLayout.setSelectedTabIndicatorColor(Color.rgb(3, 196, 161)); //ban amarillo

    }
}
