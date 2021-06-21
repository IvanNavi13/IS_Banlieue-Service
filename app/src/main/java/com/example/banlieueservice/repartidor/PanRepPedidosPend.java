package com.example.banlieueservice.repartidor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.banlieueservice.R;
import com.example.banlieueservice.herramientas.AdaptadorLista;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.interfaces.FragmentCommunicator;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.pedido.ElementoListaPedidos;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;

import java.util.LinkedList;
import java.util.Map;

public class PanRepPedidosPend extends Fragment implements FragmentCommunicator, AdapterView.OnItemClickListener {
    private Context ctx;
    private FragmentActivity act;
    private Button btn;
    private Mensaje mje;
    private Map<String, String> datosRepartidor;
    private LinkedList<Map<String, String>> listaPedidosDisponibles;
    private ListView listviewPedidosDisponibles;

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {

        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);

        return li.inflate(R.layout.fragment_reppedidosdisp, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        cargarPedidos();
        initComponents();

    }

    @Override
    public void sendData(Map<String, String> data) {
        datosRepartidor=data;
    }
    @Override
    public void sendSingleData(Object data) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PedidoDisponible pedidoDisponible= new PedidoDisponible(this, "pend", listaPedidosDisponibles.get(position).get("idPed"));
        pedidoDisponible.sendData(datosRepartidor);
        pedidoDisponible.sendSingleData("pend"); //se avisa que es pedido pendiente
        pedidoDisponible.show(act.getSupportFragmentManager(), "Disponible");
    }




    private void crearLista(LinkedList<ElementoListaPedidos> listaElementos){
        //Adaptar el elemento de la lista con el contenedor elemento_lista
        listviewPedidosDisponibles.setAdapter(new AdaptadorLista(ctx, R.layout.elemento_lista_pedidos, listaElementos) {
            @Override
            public void onEntrada(Object o, View v) {
                ElementoListaPedidos obj= (ElementoListaPedidos)o;
                if(obj!=null){
                    TextView persona= (TextView) v.findViewById(R.id.listapedPersona);
                    persona.setText( obj.obtPersona() );
                    TextView direccion= (TextView) v.findViewById(R.id.listapedDirEntrega);
                    direccion.setText( obj.obtDireccionEntrega() );
                    TextView telefono= (TextView) v.findViewById(R.id.listapedTelefonoPer);
                    telefono.setText( obj.obtTelefonoPersona() );
                    TextView hora= (TextView) v.findViewById(R.id.listapedHora);
                    hora.setText( obj.obtHoraPedido() );
                    TextView fecha= (TextView) v.findViewById(R.id.listapedFecha);
                    fecha.setText( obj.obtFechaPedido() );
                }
            }
        });
    }

    public void cargarPedidos(){
        JSON json= new JSON();
        json.agregarDato("pedido", "pend");
        json.agregarDato("idRepartidor", datosRepartidor.get("idPartic"));
        ServicioWeb.obtenerInstancia(ctx).pedidos(json.strJSON(), new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onJsonSuccess(String jsonResult) {
                listaPedidosDisponibles= json.obtenerDatosArreglo(jsonResult, "listaPedidosTomados");

                LinkedList<ElementoListaPedidos> servicios= new LinkedList<>();
                for(int i=0; i<listaPedidosDisponibles.size(); i++) {
                    servicios.add( new ElementoListaPedidos(listaPedidosDisponibles.get(i)) );
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
        listviewPedidosDisponibles= (ListView) getView().findViewById(R.id.lvListaPedidos);
        listviewPedidosDisponibles.setOnItemClickListener(this);
    }
}
