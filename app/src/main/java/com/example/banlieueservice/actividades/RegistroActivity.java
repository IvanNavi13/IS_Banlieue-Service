package com.example.banlieueservice.actividades;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;


import androidx.annotation.ArrayRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.banlieueservice.R;
import com.example.banlieueservice.cliente.PanelClienteActivity;
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

public class RegistroActivity extends AppCompatActivity implements OnClickListener, OnItemSelectedListener {
    private RegistroActivity regAct;
    private EditText etNombre, etApaterno, etAmaterno, etTelefono, etCorreo, etContra, etConfcontra;
    private Button btnRegistrar;
    private Spinner spTipoUsuario, spDia, spMes, spAnio;
    private boolean bndReg, bndRep;

    private Mensaje mje;

    @SuppressLint({"NewApi", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        regAct= this;
        setContentView(R.layout.activity_registro);
        initComponents();
        mje= new Mensaje(this);

        contenidoSpinner(spTipoUsuario, R.array.tipo_de_usuario, R.layout.spinner_tipous_item);
        contenidoSpinner(spDia, R.array.dia, R.layout.spinner_fecha_item);
        contenidoSpinner(spMes, R.array.mes, R.layout.spinner_fecha_item);
        contenidoSpinner(spAnio, R.array.anio, R.layout.spinner_fecha_item);
    }

    @Override
    public void onClick(View view){
        Map<String, String> datos;
        bndReg=true; //Suponemos que se va a registrar
        int pressed= view.getId();
        switch(pressed){
            case R.id.btnRegistrar:
                LinkedList<String> etDatos=null;
                //Verificar que se haya especificado el tipo de usuario
                if(spTipoUsuario.getSelectedItemPosition()==0){
                    mje.mostrarToast("Especifique qué tipo de usuario es usted", 'l');
                    bndReg=false;
                }
                else{
                    //Verificar que todos los campos estén llenos
                    if(obtenerDatosET().contains("")){
                        mje.mostrarToast("Faltan datos", 'c');
                        bndReg=false;
                    }
                    else{
                        //Verificar que la contraseña coincida
                        etDatos= obtenerDatosET();
                        String confContra= etDatos.getLast();
                        if(!etDatos.get(etDatos.size()-2).equals(confContra)){
                            mje.mostrarToast("La contraseña no coincide", 'c');
                            bndReg=false;
                        }
                    }
                }

                if(bndReg){
                    StringBuilder fechanac= new StringBuilder("");
                    fechanac.append(spAnio.getSelectedItem().toString()).append("-");
                    fechanac.append( new Utilidad().mesAnumero(spMes.getSelectedItem().toString()) ).append("-");
                    fechanac.append(spDia.getSelectedItem().toString());

                    int tipoUs= spTipoUsuario.getSelectedItemPosition();
                    String tipoUsStr;
                    if(tipoUs==1) tipoUsStr="usr";
                    else if(tipoUs==2) tipoUsStr="cli";
                    else tipoUsStr="rep";

                    datos= new HashMap<>();
                    datos.put("tipoPersona", tipoUsStr);
                    datos.put("nombres", etDatos.pop());
                    datos.put("apaterno", etDatos.pop());
                    datos.put("amaterno", etDatos.pop());
                    datos.put("telefono", etDatos.pop());
                    datos.put("fechanac", fechanac.toString());
                    datos.put("correo", etDatos.pop());
                    datos.put("contrasena", etDatos.pop());

                    //Convertir datos a JSON
                    JSON json= new JSON();
                    json.agregarDatos(datos);

                    if(!bndRep) { //Si no es repartidor, registra directamente
                        ServicioWeb.obtenerInstancia(this).nuevaPersona(json.strJSON(), new VolleyCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                //Mensaje de éxito del servidor
                                mje.mostrarToast(result, 'l');

                                //Iniciar sesión automáticamente
                                Intent intent=null;
                                switch(datos.get("tipoPersona")){
                                    case "usr": //Abrir panel de usuario
                                        intent= new Intent(RegistroActivity.this, PanelUsuarioActivity.class);
                                        break;

                                    case "cli": //Abrir panel de cliente
                                        intent= new Intent(RegistroActivity.this, PanelClienteActivity.class);
                                        break;

                                    case "rep": //Abrir panel de repartidor
                                        intent= new Intent(RegistroActivity.this, PanelRepartidorActivity.class);
                                        break;

                                    default: //Algo anda mal en el servidor
                                        mje.mostrarDialog("Servidor corrupto", "Banlieue Service", regAct);
                                        break;
                                }
                                //... Finalmente, se carga el correo verificado en el intent para que
                                //el respectivo panel cargue la información completa en su información completa
                                intent.putExtra("correo", etCorreo.getText().toString());
                                etContra.setText("");
                                etCorreo.setText("");
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onJsonSuccess(String jsonResult) {

                            }

                            @Override
                            public void onError(String result) {
                                mje.mostrarDialog(result, "Banlieue Service", regAct);
                            }
                        });
                    }
                    else{ //Si lo es, ve a otra actividad para terminar el registro
                        Intent intent= new Intent(RegistroActivity.this, RegistroRepActivity.class);
                        Bundle bundle= new Bundle();
                        bundle.putString("json", json.strJSON());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    //Enviar a la pantalla principal
                }

                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> av, View v, int i, long l){
        switch(i){
            case 0:
            case 1:
            case 2:
                btnRegistrar.setText("Registrar");
                bndRep=false; //No es repartidor
                break;
            case 3:
                btnRegistrar.setText("Continuar");
                bndRep=true; //Es repartidor y hay que registrar más cosas
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private LinkedList<String> obtenerDatosET(){
        LinkedList<String> ret= new LinkedList<>();

        ret.add(etNombre.getText().toString());
        ret.add(etApaterno.getText().toString());
        ret.add(etAmaterno.getText().toString());
        ret.add(etTelefono.getText().toString());
        ret.add(etCorreo.getText().toString());
        ret.add(etContra.getText().toString());
        ret.add(etConfcontra.getText().toString());

        return ret;
    }

    private void initComponents(){
        btnRegistrar= (Button) findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(this);

        etNombre= (EditText) findViewById(R.id.etRegNombre);
        etApaterno= (EditText) findViewById(R.id.etRegApaterno);
        etAmaterno= (EditText) findViewById(R.id.etRegAmaterno);
        etTelefono= (EditText) findViewById(R.id.etRegTelefono);
        etCorreo= (EditText) findViewById(R.id.etRegCorreo);
        etContra= (EditText) findViewById(R.id.etRegContra);
        etConfcontra= (EditText) findViewById(R.id.etRegConfcontra);

        spTipoUsuario= (Spinner) findViewById(R.id.spRegTipoUsuario);
        spTipoUsuario.setOnItemSelectedListener(this);
        spDia= (Spinner) findViewById(R.id.spRegDia);
        spDia.setOnItemSelectedListener(this);
        spMes= (Spinner) findViewById(R.id.spRegMes);
        spMes.setOnItemSelectedListener(this);
        spAnio= (Spinner) findViewById(R.id.spRegAnio);
        spAnio.setOnItemSelectedListener(this);

    }

    private void contenidoSpinner(Spinner spinner, @ArrayRes int arreglo, @LayoutRes int recurso){
        ArrayAdapter<CharSequence> adapter;

        /////COLOCAR ELEMENTOS EN EL SPINNER PARA LE TIPO DE USUARIO
        // Crear ArrayAdapter desde algún recurso
        adapter = ArrayAdapter.createFromResource(this, arreglo, recurso);
        // Especificación de modelo a mostrar en el spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //Fondo Cyan
        spinner.setPopupBackgroundDrawable(new ColorDrawable(Color.rgb(3, 196, 161)));

    }

}
