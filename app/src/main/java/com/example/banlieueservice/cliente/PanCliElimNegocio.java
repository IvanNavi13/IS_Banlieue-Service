package com.example.banlieueservice.cliente;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.banlieueservice.R;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PanCliElimNegocio extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Mensaje mje;
    private Context ctx;
    private FragmentActivity act;
    private Map<String, String> datosActuales;
    private LinkedList<Map<String, String>> infoLocales;
    private Button btnElim;
    private EditText etComent;
    private Spinner spNomNeg;
    private String idDeNegocioAEliminar;


    public PanCliElimNegocio(Map<String, String> datosActuales){
        this.datosActuales= datosActuales;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {

        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);

        return li.inflate(R.layout.fragment_panclielimnegocio, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        initComponents();
        cargarLocales();
        mje.mostrarDialog("Si elimina su negocio, todos los datos referentes a éste serán eliminados.",
                "Banlieue Service",
                (AppCompatActivity)act
        );
    }

    @Override
    public void onClick(View view){
        if(view.getId()==R.id.btnEliminar){
            if(idDeNegocioAEliminar.equals("no"))
                mje.mostrarToast("Seleccione un local", 'c');
            else{
                JSON json= new JSON();
                json.agregarDato("idEst", idDeNegocioAEliminar);
                ServicioWeb.obtenerInstancia(ctx).eliminarNegocio(json.strJSON(), new VolleyCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        mje.mostrarDialog(result, "Banlieue Service", (AppCompatActivity) act);
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
    }

    @Override
    public void onItemSelected(AdapterView<?> av, View v, int i, long l){
        if(i>0){
            idDeNegocioAEliminar= infoLocales.get(i-1).get("idEst");
        }
        else{ //Opciones por default
            idDeNegocioAEliminar="no";
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    private void myContenidoSpinner(Spinner spinner, List elementos, @LayoutRes int recurso){
        ArrayAdapter<CharSequence> adapter;

        /////COLOCAR ELEMENTOS EN EL SPINNER PARA LE TIPO DE USUARIO
        // Crear ArrayAdapter desde algún recurso
        adapter = new PanCliInicio.MyArrayAdapter(ctx, recurso, elementos);
        // Especificación de modelo a mostrar en el spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //Fondo Cyan
        spinner.setPopupBackgroundDrawable(new ColorDrawable(Color.rgb(3, 196, 161)));

    }


    private void cargarLocales(){
        JSON json= new JSON();
        json.agregarDato("idCliente", datosActuales.get("idPartic"));
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

    private void initComponents(){
        etComent= (EditText) getActivity().findViewById(R.id.etComentarios);

        btnElim= (Button) getActivity().findViewById(R.id.btnEliminar);
        btnElim.setOnClickListener(this);

        spNomNeg= (Spinner) getView().findViewById(R.id.spNomNeg);
        spNomNeg.setOnItemSelectedListener(this);
    }
}
