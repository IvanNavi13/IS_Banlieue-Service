package com.example.banlieueservice.cliente;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.interfaces.FragmentCommunicator;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;

import java.util.Map;

public class PanCliDatosLocal extends Fragment implements View.OnClickListener, FragmentCommunicator{
    private Context ctx;
    private FragmentActivity act;
    private Mensaje mje;
    private Button btnModificar;
    private EditText etNombre, etDireccion;
    private Spinner spHoraAp, spMinAp, spHoraCi, spMinCi, spGiro;
    private String idEstablecimiento;
    private boolean modificar;

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {

        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);
        modificar=false; //Por defecto no se pueden modificar datos del local

        return li.inflate(R.layout.fragment_panclinvonegocio, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        initComponents();

        btnModificar.setText("Modificar");
        contenidoSpinner(spHoraAp, R.array.hora24, R.layout.spinner_tipous_item);
        contenidoSpinner(spHoraCi, R.array.hora24, R.layout.spinner_tipous_item);
        contenidoSpinner(spMinAp, R.array.minuto, R.layout.spinner_tipous_item);
        contenidoSpinner(spMinCi, R.array.minuto, R.layout.spinner_tipous_item);
        contenidoSpinner(spGiro, R.array.giro_negocio, R.layout.spinner_tipous_item);


    }

    @Override
    public void onClick(View view){
        int pressed= view.getId();
        switch (pressed){
            case R.id.btnRegistrar:
                if(modificar){
                    String nombre= etNombre.getText().toString();
                    String direc= etDireccion.getText().toString();
                    String apertura= spHoraAp.getSelectedItem()+":"+spMinAp.getSelectedItem();
                    String cierre= spHoraCi.getSelectedItem()+":"+spMinCi.getSelectedItem();

                    if(nombre.equals("") || direc.equals(""))
                        mje.mostrarToast("Ingrese todos los datos", 'c');
                    else{
                        JSON json= new JSON();
                        json.agregarDato("idEst", idEstablecimiento);
                        json.agregarDato("nombre", nombre);
                        json.agregarDato("giro", String.valueOf(spGiro.getSelectedItemPosition())+"@"+spGiro.getSelectedItem());
                        json.agregarDato("direccion", direc);
                        json.agregarDato("apertura", apertura);
                        json.agregarDato("cierre", cierre);

                        ServicioWeb.obtenerInstancia(ctx).modificarEstablecimiento(json.strJSON(), new VolleyCallBack() {
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
                }
                else
                    mje.mostrarToast("Seleccione un negocio", 'c');
                break;
        }
    }

    @Override
    public void sendData(Map<String, String> data) {
        idEstablecimiento= data.get("idEst");
        etNombre.setText(data.get("nombre"));
        etDireccion.setText(data.get("direccion"));

        //Para mostrar el giro del negocio
        int index= Integer.parseInt( data.get("giro").split("@")[0] );
        spGiro.setSelection(index);

        //Para mostrar la hora registrada de apertura y cierre
        String[] apertura= data.get("apertura").split(":");
        String[] cierre= data.get("cierre").split(":");
        spHoraAp.setSelection(Integer.parseInt(apertura[0]));
        spMinAp.setSelection(Integer.parseInt(apertura[1])/10);
        spHoraCi.setSelection(Integer.parseInt(cierre[0]));
        spMinCi.setSelection(Integer.parseInt(cierre[1])/10);
    }
    @Override
    public void sendSingleData(Object data){
        modificar= (Boolean) data;
    }

    private void initComponents(){
        spHoraAp= (Spinner) getView().findViewById(R.id.spHoraAper);
        spMinAp= (Spinner) getView().findViewById(R.id.spMinAper);
        spHoraCi= (Spinner) getView().findViewById(R.id.spHoraCierre);
        spMinCi= (Spinner) getView().findViewById(R.id.spMinCierre);
        spGiro= (Spinner) getView().findViewById(R.id.spReglocalGiro);

        etNombre= (EditText) getView().findViewById(R.id.etReglocalNombre);
        etDireccion= (EditText) getView().findViewById(R.id.etReglocalDireccion);

        btnModificar= (Button) getView().findViewById(R.id.btnRegistrar);
        btnModificar.setOnClickListener(this);
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
