package com.example.banlieueservice.usuario;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.banlieueservice.R;

import com.example.banlieueservice.actividades.MapaActivity;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.repartidor.PanRepHome;
import com.example.banlieueservice.repartidor.PanRepPedidosDisp;
import com.example.banlieueservice.repartidor.PanRepPedidosReal;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class PanUsHome extends Fragment{
    private Button btnSat;
    private Mensaje mje;
    private Context ctx;
    private FragmentActivity act;

    private PanRepHome.AdaptadorDePestanas adaptadorDePestanas;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);
        return li.inflate(R.layout.fragment_panushome, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);

        LinkedList<String> nombres= new LinkedList<>();
        LinkedList<Fragment> contPestanas= new LinkedList<>();

        nombres.add("Mapa");
        contPestanas.add( new MapaActivity() );

        adaptadorDePestanas = new PanRepHome.AdaptadorDePestanas(getChildFragmentManager(), nombres, contPestanas);
        viewPager = getView().findViewById(R.id.vpPager);
        //viewPager.setBackground(new ColorDrawable(Color.rgb(254, 205, 26))); //BanAmarillo en decimal
        viewPager.setAdapter(adaptadorDePestanas);
        TabLayout tabLayout = getView().findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackground(new ColorDrawable(Color.rgb(17, 29, 94))); //ban cyan
        tabLayout.setSelectedTabIndicatorColor(Color.rgb(3, 196, 161)); //ban amarillo


        btnSat= (Button) getView().findViewById(R.id.btnVistaSat);
        btnSat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mje.mostrarToast("Mapa listo", 'c');
            }
        });
    }

}
