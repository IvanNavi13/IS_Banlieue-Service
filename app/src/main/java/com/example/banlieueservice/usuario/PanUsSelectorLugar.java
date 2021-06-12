package com.example.banlieueservice.usuario;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.banlieueservice.R;
import com.example.banlieueservice.actividades.MapaActivity;
import com.example.banlieueservice.cliente.PanCliModifServicio;
import com.example.banlieueservice.herramientas.AdaptadorLista;
import com.example.banlieueservice.herramientas.ElementoLista;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.interfaces.FragmentCommunicator;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.repartidor.PanRepHome;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;
import com.google.android.material.tabs.TabLayout;

import java.util.LinkedList;
import java.util.Map;

public class PanUsSelectorLugar extends Fragment implements AdapterView.OnItemClickListener, FragmentCommunicator {
    private Mensaje mje;
    private Context ctx;
    private FragmentActivity act;
    private ListView listaLocales;
    private LinkedList<Map<String, String>> infoEstablecimientos;
    private MapaActivity mapaActivity;
    private Map<String, String> datosUsuario;

    public PanUsSelectorLugar(MapaActivity mapaActivity){
        this.mapaActivity= mapaActivity;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);
        return li.inflate(R.layout.fragment_panusselectorlugar, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        initComponents();
        cargarEstablecimientos();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        VistaPreviaEstabl vistaPrevia= new VistaPreviaEstabl(infoEstablecimientos.get(position));
        vistaPrevia.sendData(datosUsuario);
        vistaPrevia.show(act.getSupportFragmentManager(), "Establecimiento");
        mapaActivity.definirUbicacion(
                infoEstablecimientos.get(position).get("nombre"),
                infoEstablecimientos.get(position).get("direccion")
        );
    }

    @Override
    public void sendData(Map<String, String> data) {
        datosUsuario=data;
    }
    @Override
    public void sendSingleData(Object data) {

    }

    private void crearLista(LinkedList<ElementoLista> listaElementos){
        //Adaptar el elemento de la lista con el contenedor elemento_lista
        listaLocales.setAdapter(new AdaptadorLista(ctx, R.layout.elemento_lista, listaElementos) {
            @Override
            public void onEntrada(Object o, View v) {
                ElementoLista obj= (ElementoLista)o;
                if(obj!=null){
                    TextView titulo= (TextView) v.findViewById(R.id.listaNombre);
                    titulo.setText( obj.obtNombre() );
                    TextView desc= (TextView) v.findViewById(R.id.listaDesc);
                    desc.setText( obj.obtDescripcion() );
                    ImageView imagen= (ImageView) v.findViewById(R.id.listaImagen);
                    imagen.setImageResource( obj.obtImagen() );
                }
            }
        });
    }

    public void cargarEstablecimientos(){
        JSON json= new JSON();
        json.agregarDato("todo", "s"); //Seleccionar todos los establecimientos

        ServicioWeb.obtenerInstancia(ctx).infoNegocios(json.strJSON(), new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onJsonSuccess(String jsonResult) {
                infoEstablecimientos= json.obtenerDatosArreglo(jsonResult, "listaNegocios");

                LinkedList<ElementoLista> servicios= new LinkedList<>();
                for(int i=0; i<infoEstablecimientos.size(); i++) {
                    servicios.add(
                            new ElementoLista(
                                    R.drawable.icono, //Cambiar por imagen del local
                                    infoEstablecimientos.get(i).get("nombre"),
                                    infoEstablecimientos.get(i).get("direccion")
                            )
                    );
                }
                crearLista(servicios);
            }

            @Override
            public void onError(String result) {
                mje.mostrarDialog(result, "Banlieue Service", (AppCompatActivity)act);
            }
        });
    }



    private void initComponents(){
        listaLocales= (ListView) getView().findViewById(R.id.lvListaLocales);
        listaLocales.setOnItemClickListener(this);
    }
}
