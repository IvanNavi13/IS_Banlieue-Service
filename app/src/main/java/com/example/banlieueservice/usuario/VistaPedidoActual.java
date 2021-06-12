package com.example.banlieueservice.usuario;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.banlieueservice.R;
import com.example.banlieueservice.actividades.MainActivity;
import com.example.banlieueservice.herramientas.AdaptadorLista;
import com.example.banlieueservice.herramientas.ElementoLista;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.interfaces.FragmentCommunicator;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.pedido.CuerpoDePedido;
import com.example.banlieueservice.pedido.ElementoDePedido;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;

import java.util.LinkedList;
import java.util.Map;

public class VistaPedidoActual extends AppCompatDialogFragment implements FragmentCommunicator {
    private Mensaje mje;
    private FragmentActivity act;
    private CreadorDePedidos creador;
    private Context ctx;
    private CuerpoDePedido cuerpoDePedido;
    private ListView listaPedidoActual;
    private String idUsuario, correoUsuario, direccionEntrega;


    public VistaPedidoActual(CuerpoDePedido cuerpoDePedido, CreadorDePedidos creador){
        this.creador= creador;
        this.cuerpoDePedido= cuerpoDePedido;
    }

    @Override
    public Dialog onCreateDialog(Bundle b){
        act= getActivity();
        ctx= getContext();
        mje= new Mensaje(act);

        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());

        LayoutInflater inflater= getActivity().getLayoutInflater();
        View view= inflater.inflate(R.layout.dialogfragment_vistapedidoactual, null);

        listaPedidoActual= (ListView) view.findViewById(R.id.lvPedidoActual);
        crearLista(cuerpoDePedido.elementos());

        builder.setView(view);
        builder.setTitle("Información");

        builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton("Realizar pedido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(cuerpoDePedido.estaVacio()){
                    mje.mostrarToast("No se puede hacer un pedido vacío", 'c');
                }
                else{
                    JSON json = new JSON();
                    json.agregarDato("idUsuario", idUsuario); //Clave de usuario
                    json.agregarDato("direccion", direccionEntrega);
                    json.agregarDato("cuerpoPedido", cuerpoDePedido.toJSONString());

                    ServicioWeb.obtenerInstancia(ctx).nuevoPedido(json.strJSON(), new VolleyCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            mje.mostrarToast(result,'l');
                            Intent intent= new Intent(act, PanelUsuarioActivity.class);
                            //Intent intentp= new Intent(MainActivity.this, MapaActivity.class);
                            intent.putExtra("correo", correoUsuario);
                            creador.startActivity(intent);
                            creador.finish();
                        }

                        @Override
                        public void onJsonSuccess(String jsonResult) {

                        }

                        @Override
                        public void onError(String result) {
                            mje.mostrarDialog(result, "Banlieue Service", (AppCompatActivity)act);
                        }
                    });
                }

            }
        });

        return builder.create();
    }

    @Override
    public void sendData(Map<String, String> data) {

    }
    @Override
    public void sendSingleData(Object data) {
        //0->idUsuario, 1->correo, 2->dirección
        String[] dataPartes= ( (String)data ).split("\\|");
        idUsuario= dataPartes[0];
        correoUsuario= dataPartes[1];
        direccionEntrega= dataPartes[2];
    }


    private void crearLista(LinkedList<ElementoDePedido> listaElementos){
        //Adaptar el elemento de la lista con el contenedor elemento_lista
        listaPedidoActual.setAdapter(new AdaptadorLista(ctx, R.layout.elemento_pedido, listaElementos) {
            @Override
            public void onEntrada(Object o, View v) {
                ElementoDePedido obj= (ElementoDePedido) o;
                if(obj!=null){
                    ImageButton quitarElemento= (ImageButton) v.findViewById(R.id.listaEliminarElemento);
                    quitarElemento.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cuerpoDePedido.elementos().remove(obj);
                            crearLista(cuerpoDePedido.elementos());
                        }
                    });

                    TextView titulo= (TextView) v.findViewById(R.id.listaNombre);
                    titulo.setText( obj.obtNombre() );

                    TextView desc= (TextView) v.findViewById(R.id.listaTotal);
                    desc.setText( "$"+obj.obtPrecio() );

                    EditText cantidad= (EditText) v.findViewById(R.id.listaCantidad);
                    cantidad.setText( obj.obtCantidad() );

                    Button aumentoCant= (Button) v.findViewById(R.id.listaAumentaCant);
                    aumentoCant.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            double actual= Double.parseDouble(cantidad.getText().toString());
                            cantidad.setText(String.valueOf((int)actual+1));
                            obj.defCantidad(String.valueOf((int)actual+1));
                            desc.setText("$"+Double.parseDouble(obj.obtPrecio())*(actual+1) );
                        }
                    });

                    Button disminucionCant= (Button) v.findViewById(R.id.listaDisminuyeCant);
                    disminucionCant.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            double actual= Double.parseDouble(cantidad.getText().toString());
                            if(actual>1) {
                                cantidad.setText(String.valueOf((int)actual - 1));
                                obj.defCantidad(String.valueOf((int)actual+1));
                                desc.setText("$"+Double.parseDouble(obj.obtPrecio())*(actual+1) );
                            }
                            else{
                                desc.setText("$"+Double.parseDouble(obj.obtPrecio()));
                            }
                        }
                    });
                }
            }
        });
    }

}
