package com.example.banlieueservice.cliente;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.ArrayRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.banlieueservice.R;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.repartidor.PanRepHome;
import com.example.banlieueservice.repartidor.PanRepPedidosDisp;
import com.example.banlieueservice.repartidor.PanRepPedidosReal;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;
import com.google.android.material.tabs.TabLayout;

import android.widget.AdapterView.OnItemSelectedListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PanCliAdminNegocios extends Fragment implements View.OnClickListener, OnItemSelectedListener {
    private Context ctx;
    private FragmentActivity act;
    private Spinner spNomNeg;
    private Mensaje mje;
    private String idCliente;
    private LinkedList<Map<String, String>> infoLocales;
    private PanRepHome.AdaptadorDePestanas adaptadorDePestanas;
    private ViewPager viewPager;
    private PanCliDatosLocal panCliDatosLocal;
    private PanCliServsLocal panCliServsLocal;

    public PanCliAdminNegocios(String idCliente){
        this.idCliente= idCliente;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {

        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);
        panCliDatosLocal= new PanCliDatosLocal();
        panCliServsLocal= new PanCliServsLocal();
        cargarLocales();

        return li.inflate(R.layout.fragment_pancliadminneg, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        initComponents();


        //PESTAÑAS DE VISUALIZACIÓN
        LinkedList<String> titulos= new LinkedList<>();
        LinkedList<Fragment> contPestanas= new LinkedList<>();

        titulos.add("Información de local");
        contPestanas.add(new PanCliDatosLocal());
        titulos.add("Servicios que ofrece");
        contPestanas.add(new PanCliServsLocal());

        adaptadorDePestanas = new PanRepHome.AdaptadorDePestanas(getChildFragmentManager(), titulos, contPestanas);
        viewPager = getView().findViewById(R.id.vpPager);
        viewPager.setAdapter(adaptadorDePestanas);
        TabLayout tabLayout = getView().findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackground(new ColorDrawable(Color.rgb(17, 29, 94))); //ban azul
        tabLayout.setSelectedTabIndicatorColor(Color.rgb(3, 196, 161)); //ban cyan


    }

    @Override
    public void onClick(View view){
        Map<String, String> datos;

        int pressed= view.getId();
        switch(pressed){

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> av, View v, int i, long l){
        if(i>0){
            StringBuilder sb= new StringBuilder("");
            sb.append(infoLocales.get(i-1).get("idEst")).append("\n");
            sb.append(infoLocales.get(i-1).get("idCli")).append("\n");
            sb.append(infoLocales.get(i-1).get("nombre")).append("\n");
            sb.append(infoLocales.get(i-1).get("giro")).append("\n");
            sb.append(infoLocales.get(i-1).get("direccion")).append("\n");
            sb.append(infoLocales.get(i-1).get("apertura")).append("\n");
            sb.append(infoLocales.get(i-1).get("cierre")).append("\n");
            mje.mostrarDialog(sb.toString(), "", (AppCompatActivity) act);
            //panCliDatosLocal.desplegarDatos(infoLocales.get(i-1));
        }
        else{ //Opciones por default
            Map<String, String> defecto= new HashMap<>();
            defecto.put("nombre", "");
            defecto.put("direccion", "");
            defecto.put("giro", "0@x");
            defecto.put("apertura", "00:00");
            defecto.put("cierre", "00:00");
            //panCliDatosLocal.desplegarDatos(defecto);
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void initComponents(){
        spNomNeg= (Spinner) getView().findViewById(R.id.spNomNeg);
        spNomNeg.setOnItemSelectedListener(this);
    }

    private void cargarLocales(){
        JSON json= new JSON();
        json.agregarDato("idCliente", idCliente);
        ServicioWeb.obtenerInstancia(ctx).infoNegocios(json.strJSON(), new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onJsonSuccess(String jsonResult) {
                infoLocales= json.obtenerDatosArreglo(jsonResult);
                /*int i=0;
                for(Map<String, String> mapa: infoLocales){
                    System.out.println("<-----------------------------------  "+i+"  ----------------------------------->");
                    System.out.println(mapa.get("idEst"));
                    System.out.println(mapa.get("idCli"));
                    System.out.println(mapa.get("nombre"));
                    System.out.println(mapa.get("giro"));
                    System.out.println(mapa.get("direccion"));
                    System.out.println(mapa.get("apertura"));
                    System.out.println(mapa.get("cierre"));
                    i+=1;
                }*/
                //SPINNER DE SELECCIÓN
                LinkedList<String> nombres= new LinkedList<>();
                nombres.add("SELECCIONA UN NEGOCIO");
                for(Map<String, String> mapa: infoLocales)
                    nombres.add(mapa.get("nombre"));
                myContenidoSpinner(spNomNeg, nombres, R.layout.spinner_tipous_item);
            }

            @Override
            public void onError(String result) {
                mje.mostrarDialog(result, "Banlieue Service", (AppCompatActivity)act);
            }
        });
    }

    private void myContenidoSpinner(Spinner spinner, List elementos, @LayoutRes int recurso){
        ArrayAdapter<CharSequence> adapter;

        /////COLOCAR ELEMENTOS EN EL SPINNER PARA LE TIPO DE USUARIO
        // Crear ArrayAdapter desde algún recurso
        adapter = new MyArrayAdapter(ctx, recurso, elementos);
        // Especificación de modelo a mostrar en el spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //Fondo Cyan
        spinner.setPopupBackgroundDrawable(new ColorDrawable(Color.rgb(3, 196, 161)));

    }

    public class MyArrayAdapter extends ArrayAdapter{
        Context c;
        int res;
        public MyArrayAdapter(Context c, int res, List objs){
            super(c, res, objs);
            this.c=c;
            this.res=res;
        }
    }
}
