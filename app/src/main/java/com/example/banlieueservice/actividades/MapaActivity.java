package com.example.banlieueservice.actividades;

import android.Manifest;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.banlieueservice.R;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.interfaces.ModoMapa;
import com.example.banlieueservice.repartidor.PanRepHome;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
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

public class MapaActivity extends Fragment /*implements OnMapReadyCallback, GoogleMap.OnMapClickListener*/ {
    private static MapaActivity mapa;
    private FragmentActivity act;
    private Context ctx;
    private Mensaje mje;
    private GoogleMap mMap;
    private double lat, lon;
    private boolean bndPrimeravez;
    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);
        bndPrimeravez= true;
        return li.inflate(R.layout.activity_mapa, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);

        //Solicitar permisos si es que no hay
        if
        (ActivityCompat.checkSelfPermission(ctx,
                Manifest.permission.ACCESS_FINE_LOCATION)
                !=
                PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(act,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }

        mapView= (MapView) getView().findViewById(R.id.mapView);
        mapView.onCreate(b);

        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*SupportMapFragment mapFragment = (SupportMapFragment)
                act.getSupportFragmentManager().findFragmentById(R.id.mapaGoogle);
        mapFragment.getMapAsync(this);*/

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                //mMap.setOnMapClickListener(this);
                // Add a marker in Sydney and move the camera
                LatLng sydney = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                //Atributos del mapa
                UiSettings ajustesMapa= mMap.getUiSettings();
                ajustesMapa.setRotateGesturesEnabled(true);
                //ajustesMapa.setMapToolbarEnabled(true);
                ajustesMapa.setCompassEnabled(true);
                ajustesMapa.setZoomGesturesEnabled(true);

                locationStart();


                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        try{

                            lat= latLng.latitude;
                            lon= latLng.longitude;
                            cambiarMarca(new LatLng(lat, lon));

                            List<Address> list = new Geocoder(ctx, Locale.getDefault()).getFromLocation(lat, lon, 1);
                            if (!list.isEmpty())
                                new Mensaje(ctx).mostrarToast("Direccion:\n" + list.get(0).getAddressLine(0), 'l');

                        }catch(IOException ex){
                            new Mensaje(ctx).mostrarToast("Algo salió mal:\n"+ex.getMessage(), 'l');
                        }
                    }
                });
            }
        });

        //mMap.setOnMapClickListener(this);
    }

    /*@Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_mapa);

        bndPrimeravez= true;

        if
        (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                !=
                PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapaGoogle);
        mapFragment.getMapAsync(this);
    }*/

    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //Atributos del mapa
        UiSettings ajustesMapa= mMap.getUiSettings();
        ajustesMapa.setRotateGesturesEnabled(true);
        ajustesMapa.setMapToolbarEnabled(true);
        ajustesMapa.setZoomGesturesEnabled(true);

        locationStart();
    }*/

    /*@Override
    public void onMapClick(LatLng latLng) {
        try{

            lat= latLng.latitude;
            lon= latLng.longitude;
            cambiarMarca(new LatLng(lat, lon));

            List<Address> list = new Geocoder(ctx, Locale.getDefault()).getFromLocation(lat, lon, 1);
            if (!list.isEmpty())
                new Mensaje(ctx).mostrarToast("Direccion:\n" + list.get(0).getAddressLine(0), 'l');

        }catch(IOException ex){
            new Mensaje(ctx).mostrarToast("Algo salió mal:\n"+ex.getMessage(), 'l');
        }
    }*/

    private void cambiarMarca(LatLng ll){
        mMap.clear();
        //new Mensaje(this).mostrarToast(ll.toString(), 'l');
        //MarkerOptions mo= new MarkerOptions().position(ll).title("Marker here");
        //mMap.addMarker(mo);
        mMap.addMarker(new MarkerOptions().position(ll).title("Marker here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
    }

    private void locationStart(){
        LocationManager lm = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
        Localizacion l = new Localizacion();
        l.setActivity(this);

        final boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if
        (ActivityCompat.checkSelfPermission(ctx,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(act,  new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            }, 1000);
            return;
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,  0, (LocationListener) l);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) l);
    }

    public void setLocation(Location loc) {
        lat = loc.getLatitude();
        lon = loc.getLongitude();
        if (lat != 0.0 && lon != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(lat, lon, 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    if (bndPrimeravez) {
                        cambiarMarca(new LatLng(lat, lon));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 90));
                        new Mensaje(ctx).mostrarToast("ACTUAL:\n" + DirCalle.getAddressLine(0), 'l');
                        bndPrimeravez = false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //CLASE ANIDADA
    public class Localizacion implements LocationListener {
        MapaActivity activity;
        public MapaActivity getActivity() {
            return activity;
        }
        public void setActivity(MapaActivity activity) {
            this.activity = activity;
        }
        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();
            String sLatitud = String.valueOf(loc.getLatitude());
            String sLongitud = String.valueOf(loc.getLongitude());
            activity.setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String proveedor) {
            //GPS desactivado
        }
        @Override
        public void onProviderEnabled(String proveedor) {
            //GPS activado
        }
        @Override
        public void onStatusChanged(String proveedor, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    //Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    //Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    //Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

}
