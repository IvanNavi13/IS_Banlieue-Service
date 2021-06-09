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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.banlieueservice.R;
import com.example.banlieueservice.herramientas.Mensaje;

import java.util.Map;

public class PanCliDatosLocal extends Fragment implements View.OnClickListener {
    private Context ctx;
    private FragmentActivity act;
    private Mensaje mje;
    private Button btnModificar;
    private EditText etNombre, etDireccion;
    private Spinner spHoraAp, spMinAp, spHoraCi, spMinCi, spGiro;

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
                mje.mostrarToast("fdf", 'c');
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

    public void desplegarDatos(Map<String, String> datosLocal){
        mje.mostrarToast("sdfsdf", 'c');
        /*etNombre.setText(datosLocal.get("nombre"));
        etDireccion.setText(datosLocal.get("direccion"));

        //Para mostrar el giro del negocio
        int index= Integer.parseInt( datosLocal.get("giro").split("@")[0] );
        spGiro.setSelection(index);*/

        //Para mostrar la hora registrada de apertura y cierre
    }
}
