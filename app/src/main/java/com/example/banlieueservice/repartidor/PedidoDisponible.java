package com.example.banlieueservice.repartidor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
    private ImageView imgLugar;
    private String pedido; //valores: real, disp, pend
    private Object instanciaFuente;
    private String bnd;

    public PedidoDisponible(Object obj, String bnd, String idPedido){
        this.idPedido= idPedido;
        instanciaFuente= obj;
        this.bnd= bnd;
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
        imgLugar= (ImageView) view.findViewById(R.id.dispImgLugar);
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
                            if(bnd.equals("disp"))
                                ((PanRepPedidosDisp) instanciaFuente).cargarPedidos();
                            else if(bnd.equals("pend"))
                                ((PanRepPedidosPend) instanciaFuente).cargarPedidos();
                            else if(bnd.equals("real"))
                                ((PanRepPedidosReal) instanciaFuente).cargarPedidos();
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
        json.agregarDato("idPedido", idPedido); //Original, método- pedidos
        ServicioWeb.obtenerInstancia(ctx).lugarPedidos(json.strJSON(), new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {
                //mje.mostrarDialog(result, "", (AppCompatActivity)act);
                datosLugarDePedido= obtenerDatos(result);

                nomLugar.setText( datosLugarDePedido.get("lugar") );
                dirLugar.setText( datosLugarDePedido.get("dirLugar") );
                StringBuilder sb= new StringBuilder(datosLugarDePedido.get("horarioLugar"));
                sb.delete(5, 8);
                sb.delete(16, 19);
                horarioLugar.setText( sb.toString() );
            }

            @Override
            public void onJsonSuccess(String jsonResult) {
                //mje.mostrarDialog(jsonResult, "", (AppCompatActivity)act);
                /*datosLugarDePedido= json.obtenerDatos(jsonResult);

                nomLugar.setText( datosLugarDePedido.get("lugar") );
                dirLugar.setText( datosLugarDePedido.get("dirLugar") );
                StringBuilder sb= new StringBuilder(datosLugarDePedido.get("horarioLugar"));
                sb.delete(5, 8);
                sb.delete(16, 19);
                horarioLugar.setText( sb.toString() );*/
            }

            @Override
            public void onError(String result) {
                mje.mostrarDialog(result, "Banlieue Service", (AppCompatActivity)act);
            }
        });
    }

    private Map<String, String> obtenerDatos(String infoCombinada){
        Map<String, String> ret= new HashMap<>();
        String[] infoPartes= infoCombinada.split("@");
        ret.put("idPed", infoPartes[0]);
        ret.put("lugar", infoPartes[1]);
        ret.put("dirLugar", infoPartes[2]);
        ret.put("horarioLugar", infoPartes[3]);

        byte[] imgByte= Base64.decode(infoPartes[4], Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);

        setImage(resizeBitmap(bitmap, 1024), imgLugar);

        return ret;
    }

    //Este método envía una imagen en un bitmap a un ImageView
    private void setImage(Bitmap bitmap, ImageView imageView){
        ByteArrayOutputStream baos= new ByteArrayOutputStream();

        //Compresión del bitmap
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        //Decodificar
        Bitmap decodificado= BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
        imageView.setImageBitmap(decodificado);
    }

    //Este método reescala un bitmap a una dimensión maxSize específica
    private Bitmap resizeBitmap(Bitmap bitmap, int maxSize){
        int width= bitmap.getWidth();
        int height= bitmap.getHeight();

        //Si el ancho y alto del bitmap son menores al máximo tamaño, entonces no se escala
        if(width<=maxSize && height<=maxSize){
            return bitmap;
        }

        float bmRatio= (float)width / (float) height;
        if(bmRatio>1){ //Si el ancho es mayor que el alto
            width= maxSize;
            height= (int)(width/bmRatio);
        }
        else{
            width= (int)(height*bmRatio);
            height= maxSize;
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

}
