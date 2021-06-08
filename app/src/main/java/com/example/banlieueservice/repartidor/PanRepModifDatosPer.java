package com.example.banlieueservice.repartidor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.ArrayRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.banlieueservice.R;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.herramientas.Utilidad;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;

import java.util.Map;

public class PanRepModifDatosPer extends Fragment implements View.OnClickListener {
    private Context ctx;
    private FragmentActivity act;
    private EditText etNombre, etApaterno, etAmaterno, etTelefono;
    private TextView tvNombre, tvEdad;
    private CheckBox checkModNombre;
    private Button btnModif;
    private Spinner spDia, spMes, spAnio;
    private Mensaje mje;
    private Map<String, String> datosActuales;

    public PanRepModifDatosPer(Map<String, String> datosActuales){
        this.datosActuales= datosActuales;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {

        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);

        return li.inflate(R.layout.fragment_panusmodifdatosper, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        initComponents();

        //Cargar contenido de los Spinner
        contenidoSpinner(spDia, R.array.dia, R.layout.spinner_fecha_item);
        contenidoSpinner(spMes, R.array.mes, R.layout.spinner_fecha_item);
        contenidoSpinner(spAnio, R.array.anio, R.layout.spinner_fecha_item);

        //Colocar datos actuales en TextView
        tvNombre.setText(datosActuales.get("nombre"));
        tvEdad.setText(new Utilidad().edad(datosActuales.get("fechanac"))+" años");

        //Colocar datos actuales en EditText
        etTelefono.setText(datosActuales.get("telefono"));
        //seleccionar datos actuales en Spinner
        String[] fechanac= datosActuales.get("fechanac").split("-"); //aaaa-mm-dd
        spDia.setSelection(Integer.parseInt(fechanac[2])-1);
        spMes.setSelection(Integer.parseInt(fechanac[1])-1);
        spAnio.setSelection(Integer.parseInt(fechanac[0])-1940);
    }

    @Override
    public void onClick(View v){
        if(v.getId()==R.id.btnModificarPer){

            String nombres="", apaterno="", amaterno="", fechanac="", telefono="", opcNombre="no";
            telefono= etTelefono.getText().toString();
            if(checkModNombre.isChecked()){
                opcNombre="mod"; //Clave para modificar el nombre
                nombres= etNombre.getText().toString();
                apaterno= etApaterno.getText().toString();
                amaterno= etAmaterno.getText().toString();
            }

            //Formatear fecha de nacimiento
            fechanac=
                    spAnio.getSelectedItem().toString()+"-"+
                    new Utilidad().mesAnumero(spMes.getSelectedItem().toString()) +"-"+
                    spDia.getSelectedItem().toString()
            ;

            if( (checkModNombre.isChecked() && (nombres.equals("") || apaterno.equals("") || amaterno.equals("")) ) || telefono.equals(""))
                mje.mostrarToast("Debe ingresar todos los datos necesarios", 'l');
            else{
                JSON json = new JSON();
                json.agregarDato("datos", "per"); //Clave para modificar datos personales
                json.agregarDato("opc", opcNombre);
                json.agregarDato("id", datosActuales.get("idPersona"));
                json.agregarDato("fechanac", fechanac);
                json.agregarDato("telefono", telefono);
                json.agregarDato("nombres", nombres);
                json.agregarDato("apaterno", apaterno);
                json.agregarDato("amaterno", amaterno);

                ServicioWeb.obtenerInstancia(ctx).modificarDatosPersonales(json.strJSON(), new VolleyCallBack() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(String result) {
                        mje.mostrarDialog(result, "Banlieue Service", (AppCompatActivity)act);
                        modificarDatosLocalmente();
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

    //Reemplazo temporal de datos.
    //Si se invoca este método, los datos se cambiaron correctamente pero no se reflejan aún,
    //para evitar confución en el usuario se modifican temporalmente en el mapa de datos actuales.
    //Para ver la modificación directa de la base, se debe iniciar sesión en banlieue de nuevo.
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void modificarDatosLocalmente(){
        if(checkModNombre.isChecked()){
            datosActuales.replace("nombre", etNombre.getText().toString() +" "+ etApaterno.getText().toString() +" "+ etAmaterno.getText().toString());
        }
        datosActuales.replace("telefono", etTelefono.getText().toString());
        datosActuales.replace("fechanac",
                spAnio.getSelectedItem().toString()+"-"+
                new Utilidad().mesAnumero(spMes.getSelectedItem().toString()) +"-"+
                spDia.getSelectedItem().toString()
        );
    }


    @SuppressLint("ResourceAsColor")
    private void initComponents(){
        btnModif= (Button) getView().findViewById(R.id.btnModificarPer);
        btnModif.setOnClickListener(this);

        tvEdad= (TextView) getView().findViewById(R.id.tvEdad);
        tvEdad.setBackgroundColor(R.color.banCyan);
        tvNombre= (TextView) getView().findViewById(R.id.tvNombre);
        tvNombre.setBackgroundColor(R.color.banCyan);
        etNombre= (EditText) getView().findViewById(R.id.etModifNombre);
        etNombre.setEnabled(false);
        etApaterno= (EditText) getView().findViewById(R.id.etModifApaterno);
        etApaterno.setEnabled(false);
        etAmaterno= (EditText) getView().findViewById(R.id.etModifAmaterno);
        etAmaterno.setEnabled(false);
        etTelefono= (EditText) getView().findViewById(R.id.etModifTelefono);

        spDia= (Spinner) getView().findViewById(R.id.spModifDia);
        spMes= (Spinner) getView().findViewById(R.id.spModifMes);
        spAnio= (Spinner) getView().findViewById(R.id.spModifAnio);

        checkModNombre= (CheckBox) getView().findViewById(R.id.checkModifNombre);
        checkModNombre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etNombre.setEnabled(isChecked);
                etApaterno.setEnabled(isChecked);
                etAmaterno.setEnabled(isChecked);
            }
        });
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
