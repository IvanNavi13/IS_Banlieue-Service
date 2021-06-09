package com.example.banlieueservice.cliente;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.ArrayRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.banlieueservice.R;
import com.example.banlieueservice.actividades.RegistroRepActivity;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.herramientas.Utilidad;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.repartidor.PanelRepartidorActivity;
import com.example.banlieueservice.usuario.PanelUsuarioActivity;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PanCliNvoNegocio extends Fragment implements OnClickListener {
    private Context ctx;
    private FragmentActivity act;
    private Button btnRegistrar;
    private EditText etNombre, etDireccion;
    private Spinner spHoraAp, spMinAp, spHoraCi, spMinCi, spGiro;
    private Mensaje mje;
    private String idCliente;

    public PanCliNvoNegocio(String idCliente){
        this.idCliente= idCliente;
    }


    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {

        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);

        return li.inflate(R.layout.fragment_panclinvonegocio, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        initComponents();

        contenidoSpinner(spHoraAp, R.array.hora24, R.layout.spinner_tipous_item);
        contenidoSpinner(spHoraCi, R.array.hora24, R.layout.spinner_tipous_item);
        contenidoSpinner(spMinAp, R.array.minuto, R.layout.spinner_tipous_item);
        contenidoSpinner(spMinCi, R.array.minuto, R.layout.spinner_tipous_item);
        contenidoSpinner(spGiro, R.array.giro_negocio, R.layout.spinner_tipous_item);
    }


    @Override
    public void onClick(View view){
        Map<String, String> datos;

        int pressed= view.getId();
        switch(pressed){
            case R.id.btnRegistrar:
                String nombre= etNombre.getText().toString();
                String direc= etDireccion.getText().toString();
                String apertura= spHoraAp.getSelectedItem()+":"+spMinAp.getSelectedItem();
                String cierre= spHoraCi.getSelectedItem()+":"+spMinCi.getSelectedItem();

                if(nombre.equals("") || direc.equals(""))
                    mje.mostrarToast("Ingrese todos los datos", 'c');
                else{
                    JSON json= new JSON();
                    json.agregarDato("idCliente", idCliente);
                    json.agregarDato("nombre", nombre);
                    json.agregarDato("giro", String.valueOf(spGiro.getSelectedItemPosition())+"@"+spGiro.getSelectedItem());
                    json.agregarDato("direccion", direc);
                    json.agregarDato("apertura", apertura);
                    json.agregarDato("cierre", cierre);

                    ServicioWeb.obtenerInstancia(ctx).nuevoEstablecimiento(json.strJSON(), new VolleyCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            mje.mostrarDialog(result, "Banlieue Service", (AppCompatActivity)act);
                            etDireccion.setText("");
                            etNombre.setText("");
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
                break;
        }
    }



    private void initComponents(){
        spHoraAp= (Spinner) getView().findViewById(R.id.spHoraAper);
        spMinAp= (Spinner) getView().findViewById(R.id.spMinAper);
        spHoraCi= (Spinner) getView().findViewById(R.id.spHoraCierre);
        spMinCi= (Spinner) getView().findViewById(R.id.spMinCierre);
        spGiro= (Spinner) getView().findViewById(R.id.spReglocalGiro);

        etNombre= (EditText) getView().findViewById(R.id.etReglocalNombre);
        etDireccion= (EditText) getView().findViewById(R.id.etReglocalDireccion);

        btnRegistrar= (Button) getView().findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(this);
    }

    private void contenidoSpinner(Spinner spinner, @ArrayRes int arreglo, @LayoutRes int recurso){
        ArrayAdapter<CharSequence> adapter;

        /////COLOCAR ELEMENTOS EN EL SPINNER PARA LE TIPO DE USUARIO
        // Crear ArrayAdapter desde algún recurso
        adapter = ArrayAdapter.createFromResource(ctx, arreglo, recurso);
        // Especificación de modelo a mostrar en el spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //Fondo Cyan
        spinner.setPopupBackgroundDrawable(new ColorDrawable(Color.rgb(3, 196, 161)));

    }

}
