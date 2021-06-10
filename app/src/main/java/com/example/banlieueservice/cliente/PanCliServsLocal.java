package com.example.banlieueservice.cliente;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.banlieueservice.R;
import com.example.banlieueservice.herramientas.AdaptadorLista;
import com.example.banlieueservice.herramientas.ElementoLista;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.interfaces.FragmentCommunicator;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PanCliServsLocal extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, FragmentCommunicator {
    private Context ctx;
    private FragmentActivity act;
    private Mensaje mje;
    private Button btn;
    private CheckBox checkElimServ, checkModifServ;
    private ListView listaServicios;
    private LinkedList<Map<String, String>> infoServicios;
    private String idEstablecimiento, nombreEstablecimiento, idProdserv;

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {

        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);

        return li.inflate(R.layout.fragment_cliservslocal, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        initComponents();
    }

    @Override
    public void onClick(View view){
        int pressed=view.getId();

        switch (pressed){
            case R.id.btn:
                //Parametros: nombre del negocio, id de negocio
                new PanCliNvoServicio(idEstablecimiento, nombreEstablecimiento).show(act.getSupportFragmentManager(), "Servicio");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ElementoLista el= (ElementoLista) parent.getItemAtPosition(position);

        idProdserv= infoServicios.get(position).get("idProdserv");

        String precioStr= infoServicios.get(position).get("precio");
        if( precioStr.contains(".") ){
            precioStr= precioStr.concat("0");
        }

        if(checkElimServ.isChecked()){
            JSON json = new JSON();
            json.agregarDato("idProdserv", idProdserv);
            ServicioWeb.obtenerInstancia(getActivity()).eliminarServicio(json.strJSON(), new VolleyCallBack() {
                @Override
                public void onSuccess(String result) {
                    mje.mostrarToast(result, 'l');
                }

                @Override
                public void onJsonSuccess(String jsonResult) {

                }

                @Override
                public void onError(String result) {
                    mje.mostrarDialog(result, "Banlieue Service", (AppCompatActivity) getActivity());
                }
            });
        }
        else if(checkModifServ.isChecked()){
            String[] muestraDatosServ= new String[]{
                    infoServicios.get(position).get("nombre"),
                    infoServicios.get(position).get("descripcion"),
                    infoServicios.get(position).get("precio")
            };
            new PanCliModifServicio(idProdserv, nombreEstablecimiento, muestraDatosServ).show(act.getSupportFragmentManager(), "Servicio");
        }
        else
            mje.mostrarDialog(el.obtDescripcion()+"\n\nPor solo $"+precioStr, el.obtNombre(), (AppCompatActivity)act);
    }

    @Override
    public void sendData(Map<String, String> data){
        idEstablecimiento= data.get("idEst");
        nombreEstablecimiento= data.get("nombre");
        cargarInfoServicio();
    }
    @Override
    public void sendSingleData(Object data){

    }

    private void cargarInfoServicio(){
        JSON json= new JSON();
        json.agregarDato("idEst", idEstablecimiento);

        ServicioWeb.obtenerInstancia(ctx).infoServicios(json.strJSON(), new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onJsonSuccess(String jsonResult) {
                infoServicios= json.obtenerDatosArreglo(jsonResult, "listaServicios");

                LinkedList<ElementoLista> servicios= new LinkedList<>();
                for(int i=0; i<infoServicios.size(); i++) {
                    servicios.add(
                            new ElementoLista(
                                    R.drawable.icono,
                                    infoServicios.get(i).get("nombre"),
                                    infoServicios.get(i).get("descripcion")
                            )
                    );
                }
                System.out.println(infoServicios.size());
                System.out.println(servicios.size());
                for(ElementoLista el: servicios){
                    System.out.println(el.obtDescripcion());
                }
                crearLista(servicios);
            }

            @Override
            public void onError(String result) {
                mje.mostrarDialog(result, "Banlieue Service", (AppCompatActivity)act);
            }
        });
    }

    private void crearLista(LinkedList<ElementoLista> listaElementos){
        //Adaptar el elemento de la lista con el contenedor elemento_lista
        listaServicios.setAdapter(new AdaptadorLista(ctx, R.layout.elemento_lista, listaElementos) {
            @Override
            public void onEntrada(Object o, View v) {
                ElementoLista obj= (ElementoLista)o;
                if(obj!=null){
                    TextView titulo= (TextView) v.findViewById(R.id.listaNombre);
                    titulo.setText( obj.obtNombre() );
                    TextView precio= (TextView) v.findViewById(R.id.listaPrecio);
                    precio.setText( obj.obtDescripcion() );
                    ImageView imagen= (ImageView) v.findViewById(R.id.listaImagen);
                    imagen.setImageResource( obj.obtImagen() );
                }
            }
        });
    }


    private void initComponents(){
        listaServicios= (ListView) getView().findViewById(R.id.lvListaServicios);
        listaServicios.setOnItemClickListener(this);

        btn= (Button) getView().findViewById(R.id.btn);
        btn.setOnClickListener(this);

        checkElimServ= (CheckBox) getView().findViewById(R.id.checkServElim);
        checkElimServ.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkModifServ.isChecked())
                    checkElimServ.setChecked(false);
            }
        });
        checkModifServ= (CheckBox) getView().findViewById(R.id.checkServModif);
        checkModifServ.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkElimServ.isChecked())
                    checkModifServ.setChecked(false);
            }
        });
    }
}
