package com.example.banlieueservice.repartidor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.banlieueservice.R;
import com.example.banlieueservice.herramientas.AdaptadorLista;
import com.example.banlieueservice.herramientas.ElementoLista;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.interfaces.FragmentCommunicator;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.pedido.ElementoListaServiciosPedidos;
import com.example.banlieueservice.usuario.CreadorDePedidos;
import com.example.banlieueservice.usuario.PanelUsuarioActivity;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

public class PedidoDisponible extends AppCompatDialogFragment implements FragmentCommunicator {
    private Mensaje mje;
    private Context ctx;
    private FragmentActivity act;
    private String idPedido;
    private LinkedList<Map<String, String>> listaServiciosPedidos;
    private Map<String, String> datosRepartidor, datosLugarDePedido; //Datos de la consulta de infoRepartidor en la BDD
    private ListView listviewServiciosDePedido;
    private TextView nomLugar, dirLugar, horarioLugar;
    private String pedido; //valores: real, disp, pend

    public PedidoDisponible(String idPedido){
        this.idPedido= idPedido;

    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

    }

    @Override
    public Dialog onCreateDialog(Bundle b){
        act= getActivity();
        ctx= getContext();
        mje= new Mensaje(act);

        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());

        LayoutInflater inflater= getActivity().getLayoutInflater();
        View view= inflater.inflate(R.layout.dialogfragment_pedidodisponible, null);

        /////////
        listviewServiciosDePedido= (ListView) view.findViewById(R.id.dispListaServicios);
        nomLugar= (TextView) view.findViewById(R.id.dispLugar);
        dirLugar= (TextView) view.findViewById(R.id.dispDirLugar);
        horarioLugar= (TextView) view.findViewById(R.id.dispHorarioLugar);
        /////////

        cargarCuerpoDePedido();
        cargarLugarDePedido();

        builder.setView(view);
        builder.setTitle("Información");

        if(!pedido.equals("real")) {
            builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }


        String letreroAcep="";
        if(pedido.equals("disp")) letreroAcep="Tomar pedido";
        else if(pedido.equals("pend")) letreroAcep="Marcar realizado";
        else if(pedido.equals("real")) letreroAcep="Ok";
        builder.setPositiveButton(letreroAcep, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!pedido.equals("real")) {
                    JSON json = new JSON();
                    json.agregarDato("idRepartidor", datosRepartidor.get("idPartic")); //Clave de usuario
                    json.agregarDato("idPedido", idPedido);
                    if (pedido.equals("disp"))
                        json.agregarDato("pedido", "tomar");
                    else if (pedido.equals("pend"))
                        json.agregarDato("pedido", "marcar");

                    ServicioWeb.obtenerInstancia(ctx).tomarPedido(json.strJSON(), new VolleyCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            mje.mostrarToast(result, 'l');
                        }

                        @Override
                        public void onJsonSuccess(String jsonResult) {

                        }

                        @Override
                        public void onError(String result) {
                            mje.mostrarDialog(result, "Banlieue Service", (AppCompatActivity) act);
                        }
                    });
                }
            }
        });



        return builder.create();
    }

    @Override
    public void sendData(Map<String, String> data) {
        datosRepartidor=data;
    }
    @Override
    public void sendSingleData(Object data) {
        pedido= (String)data;
    }

    private void crearLista(LinkedList<ElementoListaServiciosPedidos> listaElementos){
        //Adaptar el elemento de la lista con el contenedor elemento_lista
        listviewServiciosDePedido.setAdapter(new AdaptadorLista(ctx, R.layout.elemento_lista_servicios_pedidos, listaElementos) {
            @Override
            public void onEntrada(Object o, View v) {
                ElementoListaServiciosPedidos obj= (ElementoListaServiciosPedidos)o;
                if(obj!=null){
                    TextView servicio= (TextView) v.findViewById(R.id.dispservServicio);
                    servicio.setText( obj.obtServicio() );
                    TextView cantidad= (TextView) v.findViewById(R.id.dispservCantidad);
                    cantidad.setText( obj.obtCantidad() );
                    TextView precTotal= (TextView) v.findViewById(R.id.dispservTotal);
                    precTotal.setText( obj.obtPrecioTotal() );
                }
            }
        });
    }

    public void cargarCuerpoDePedido(){
        JSON json= new JSON();
        json.agregarDato("pedido", "cuerpo");
        json.agregarDato("idPedido", idPedido);
        ServicioWeb.obtenerInstancia(ctx).pedidos(json.strJSON(), new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onJsonSuccess(String jsonResult) {
                listaServiciosPedidos= json.obtenerDatosArreglo(jsonResult, "cuerpoDelPedido");

                LinkedList<ElementoListaServiciosPedidos> servicios= new LinkedList<>();
                for(int i=0; i<listaServiciosPedidos.size(); i++) {
                    servicios.add(
                        new ElementoListaServiciosPedidos(
                            listaServiciosPedidos.get(i).get("prod_serv"),
                            listaServiciosPedidos.get(i).get("cantidad"),
                            listaServiciosPedidos.get(i).get("precio")
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

    public void cargarLugarDePedido(){
        JSON json= new JSON();
        json.agregarDato("pedido", "lugar");
        json.agregarDato("idPedido", idPedido);
        ServicioWeb.obtenerInstancia(ctx).pedidos(json.strJSON(), new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onJsonSuccess(String jsonResult) {
                datosLugarDePedido= json.obtenerDatos(jsonResult);

                nomLugar.setText( datosLugarDePedido.get("lugar") );
                dirLugar.setText( datosLugarDePedido.get("dirLugar") );
                StringBuilder sb= new StringBuilder(datosLugarDePedido.get("horarioLugar"));
                sb.delete(5, 8);
                sb.delete(16, 19);
                horarioLugar.setText( sb.toString() );
            }

            @Override
            public void onError(String result) {
                mje.mostrarDialog(result, "Banlieue Service", (AppCompatActivity)act);
            }
        });
    }
}
