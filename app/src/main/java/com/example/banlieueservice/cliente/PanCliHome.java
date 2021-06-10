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
import com.example.banlieueservice.interfaces.FragmentCommunicator;
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

public class PanCliHome extends Fragment implements OnItemSelectedListener, FragmentCommunicator {
    private Context ctx;
    private FragmentActivity act;
    private Spinner spNomNeg;
    private Mensaje mje;
    private LinkedList<Map<String, String>> infoLocales;
    private Map<String, String> datosCliente;
    private PanRepHome.AdaptadorDePestanas adaptadorDePestanas;
    private ViewPager viewPager;
    private PanCliDatosLocal panCliDatosLocal;
    private PanCliServsLocal panCliServsLocal;

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {

        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);

        return li.inflate(R.layout.fragment_panclihome, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        initComponents();


        //PESTAÑAS DE VISUALIZACIÓN
        LinkedList<String> titulos= new LinkedList<>();
        LinkedList<Fragment> contPestanas= new LinkedList<>();
        panCliDatosLocal= new PanCliDatosLocal();
        panCliServsLocal= new PanCliServsLocal();

        titulos.add("Información de local");
        contPestanas.add(panCliDatosLocal);
        titulos.add("Productos/Servicios que ofrece");
        contPestanas.add(panCliServsLocal);

        adaptadorDePestanas = new PanRepHome.AdaptadorDePestanas(getChildFragmentManager(), titulos, contPestanas);
        viewPager = getView().findViewById(R.id.vpPager);
        viewPager.setAdapter(adaptadorDePestanas);
        TabLayout tabLayout = getView().findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackground(new ColorDrawable(Color.rgb(17, 29, 94))); //ban azul
        tabLayout.setSelectedTabIndicatorColor(Color.rgb(3, 196, 161)); //ban cyan


    }

    @Override
    public void onItemSelected(AdapterView<?> av, View v, int i, long l){
        if(i>0){
            panCliDatosLocal.sendData(infoLocales.get(i-1));
            panCliDatosLocal.sendSingleData(true);
            Map<String, String> infoParticular= new HashMap<>();
            infoParticular.put("idEst", infoLocales.get(i-1).get("idEst"));
            infoParticular.put("nombre", infoLocales.get(i-1).get("nombre"));
            panCliServsLocal.sendData(infoParticular);
        }
        else{ //Opciones por default
            Map<String, String> defecto= new HashMap<>();
            defecto.put("nombre", "");
            defecto.put("direccion", "");
            defecto.put("giro", "0@x");
            defecto.put("apertura", "00:00");
            defecto.put("cierre", "00:00");
            panCliDatosLocal.sendData(defecto);
            panCliDatosLocal.sendSingleData(false);
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void sendData(Map<String, String> data){

    }
    @Override
    public void sendSingleData(Object data) {
        cargarInfoCliente( (String) data );
    }

    private void initComponents(){
        spNomNeg= (Spinner) getView().findViewById(R.id.spNomNeg);
        spNomNeg.setOnItemSelectedListener(this);
    }

    private void cargarLocales(){
        JSON json= new JSON();
        json.agregarDato("idCliente", datosCliente.get("idPartic"));
        ServicioWeb.obtenerInstancia(ctx).infoNegocios(json.strJSON(), new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onJsonSuccess(String jsonResult) {
                infoLocales= json.obtenerDatosArreglo(jsonResult, "listaNegocios");

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

    private void cargarInfoCliente(String correo){
        JSON json = new JSON();
        json.agregarDato("tipoPersona", "cli"); //Enviar al servidor clave de indicación de Usuario (para saber qué procedure llamar)
        json.agregarDato("correo", correo);
        ServicioWeb.obtenerInstancia(ctx).infoPersona(json.strJSON(), new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onJsonSuccess(String jsonResult) {
                datosCliente= json.obtenerDatos(jsonResult);
                cargarLocales();
            }

            @Override
            public void onError(String result) {
                mje.mostrarDialog(result, "Banlieue Service", (AppCompatActivity) act);
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

    public static class MyArrayAdapter extends ArrayAdapter{
        public MyArrayAdapter(Context c, int res, List objs){
            super(c, res, objs);
        }
    }
}
