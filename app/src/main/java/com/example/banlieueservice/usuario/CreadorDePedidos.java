package com.example.banlieueservice.usuario;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.banlieueservice.R;
import com.example.banlieueservice.actividades.MainActivity;
import com.example.banlieueservice.herramientas.AdaptadorLista;
import com.example.banlieueservice.herramientas.ElementoLista;
import com.example.banlieueservice.herramientas.Fecha;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.herramientas.Utilidad;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.pedido.CuerpoDePedido;
import com.example.banlieueservice.pedido.ElementoDePedido;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CreadorDePedidos extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private Mensaje mje;
    private Map<String, String> datosEstablecimiento;
    private LinkedList<Map<String, String>> listaDatosServicios;
    private ListView listaServicios;
    private TextView tvNombreNeg;
    private CheckBox checkAgregarServ;
    private EditText etDireccionUsuario;
    private Button btnVerPedido;
    private LinkedList<ElementoDePedido> cuerpoDePedido;
    private String correoUsuario, idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creadorpedidos);
        initComponents();
        mje= new Mensaje(this);
        cuerpoDePedido= new LinkedList<>();
        //mje.mostrarDialog("Id de est: "+getIntent().getExtras().getString("idEst"), "", this);

        cargarInformacion(true);
        cargarInformacion(false);
        correoUsuario=getIntent().getExtras().getString("correo");
        idUsuario=getIntent().getExtras().getString("idUsuario");

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        //mje.mostrarToast(listaDatosServicios.get(position).get("nombre"), 'c');
        String precio= listaDatosServicios.get(position).get("precio");
        if(precio.contains("."))
            precio= precio.concat("0");
        ElementoDePedido elemPed= new ElementoDePedido(
                listaDatosServicios.get(position).get("idProdserv"),
                listaDatosServicios.get(position).get("nombre"),
                precio,
                "1" //Por defecto se pide 1
        );

        if(checkAgregarServ.isChecked()){
            boolean bnd=false; //Suponemos que no está en el pedido

            //Se verifica que no esté ene l pedido recorriendo el cuerpo del mismo
            for(ElementoDePedido e: cuerpoDePedido){
                if(e.equals(elemPed)){
                    mje.mostrarToast("Ya está en pedido", 'c');
                    bnd=true;
                    break;
                }
            }

            //Si no está en el pedido, se agrega
            if(!bnd){
                cuerpoDePedido.add(elemPed);
            }
        }
        else{
            mje.mostrarDialog(
                    listaDatosServicios.get(position).get("descripcion")+"\n\nPor solo $"+precio,
                    listaDatosServicios.get(position).get("nombre"),
                    this
            );
        }
    }

    @Override
    public void onClick(View view){
        int pressed= view.getId();
        switch (pressed){
            case R.id.btnVerPedido:
                if(etDireccionUsuario.getText().toString().equals(""))
                    mje.mostrarDialog("Especifique una dirección para que el repartidor sepa a donde llevar el pedido.", "Banlieue Service", this);
                else {
                    VistaPedidoActual vistaPedido= new VistaPedidoActual(new CuerpoDePedido(cuerpoDePedido), this);
                    vistaPedido.sendSingleData(idUsuario+"|"+correoUsuario+"|"+etDireccionUsuario.getText().toString());
                    vistaPedido.show(getSupportFragmentManager(), "Pedido");
                }
                break;
        }
    }


    //bnd=false => consultar datos de establecimiento,
    //bnd=true => consultar datos de productos/servicios.
    private void cargarInformacion(boolean bnd){
        JSON json= new JSON();
        VolleyCallBack vcb= new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onJsonSuccess(String jsonResult) {
                String precio= "";
                if(bnd){
                    listaDatosServicios= json.obtenerDatosArreglo(jsonResult, "listaServicios");
                    LinkedList<ElementoLista> servicios= new LinkedList<>();
                    for(int i=0; i<listaDatosServicios.size(); i++) {
                        precio= listaDatosServicios.get(i).get("precio");
                        if(precio.contains("."))
                            precio= precio.concat("0");
                        servicios.add(
                                new ElementoLista(
                                        R.drawable.icono, //Cambiar por imagen del local
                                        listaDatosServicios.get(i).get("nombre"),
                                        "$"+precio
                                )
                        );
                    }
                    crearLista(servicios);
                }
                else{
                    datosEstablecimiento= json.obtenerDatos(jsonResult);
                    tvNombreNeg.setText(datosEstablecimiento.get("nombre"));
                }
            }

            @Override
            public void onError(String result) {

            }
        };

        if(bnd){
            json.agregarDato("idEst", getIntent().getExtras().getString("idEst")); //Seleccionar todos los establecimientos
            ServicioWeb.obtenerInstancia(this).infoServicios(json.strJSON(), vcb);
        }
        else{
            json.agregarDato("est", "s"); //Seleccionar datos de un establecimiento
            json.agregarDato("idEst", getIntent().getExtras().getString("idEst"));
            ServicioWeb.obtenerInstancia(this).infoNegocios(json.strJSON(), vcb);
        }

    }

    private void crearLista(LinkedList<ElementoLista> listaElementos){
        //Adaptar el elemento de la lista con el contenedor elemento_lista
        listaServicios.setAdapter(new AdaptadorLista(this, R.layout.elemento_lista, listaElementos) {
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


    private void initComponents(){
        listaServicios= (ListView) findViewById(R.id.lvListaServicios);
        listaServicios.setOnItemClickListener(this);

        btnVerPedido= (Button) findViewById(R.id.btnVerPedido);
        btnVerPedido.setOnClickListener(this);

        tvNombreNeg= (TextView) findViewById(R.id.tvNombreNeg);

        checkAgregarServ= (CheckBox) findViewById(R.id.checkAgrgegarServ);
        checkAgregarServ.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mje.mostrarToast("Se agregarán los elementos que elijas", 'c');
                }
            }
        });

        etDireccionUsuario= (EditText) findViewById(R.id.etDireccionUsuario);
    }

}
