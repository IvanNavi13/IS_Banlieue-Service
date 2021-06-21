package com.example.banlieueservice.actividades;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import java.util.LinkedList;
import java.util.Map;

public class RegistroRepActivity extends AppCompatActivity {
    private AppCompatActivity regrepAct;
    private Context ctx;
    private Mensaje mje;
    private EditText etVehiculo, etPlaca, etCurp;
    private Button btnRegRep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        regrepAct= this;
        ctx=this;
        setContentView(R.layout.activity_regrepartidor);

        initComponents();
        mje= new Mensaje(this);
    }

    private LinkedList<String> obtenerDatosET(){
        LinkedList<String> ret= new LinkedList<>();

        ret.add(etVehiculo.getText().toString());
        ret.add(etPlaca.getText().toString());
        ret.add(etCurp.getText().toString());

        return ret;
    }

    private void initComponents(){
        etVehiculo= (EditText) findViewById(R.id.etRegVehiculo);
        etPlaca= (EditText) findViewById(R.id.etRegPlaca);
        etCurp= (EditText) findViewById(R.id.etRegCURP);

        btnRegRep= (Button) findViewById(R.id.btnRegistrarRep);
        btnRegRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSON infoJson= new JSON();
                Map<String, String> datosCompletos= infoJson.obtenerDatos( getIntent().getExtras().getString("json") );
                LinkedList<String> etDatos= obtenerDatosET();

                //Verificar que todos los campos estén llenos
                if(etDatos.contains("")){
                    mje.mostrarToast("Faltan datos", 'c');
                }
                else{
                    StringBuilder sb= new StringBuilder(datosCompletos.get("fechanac"));
                    sb.delete(0, 2);

                    if( new Utilidad().edad(datosCompletos.get("fechanac")) < 18 ){
                        //Sustituir esto por una verificación de CURP, no de fecha de nacimiento
                        mje.mostrarDialog("No puede ser repartidor de Banlieue Service alguien menor de edad.", "Banlieue Service", regrepAct);
                    }
                    else {
                        if( !etCurp.getText().toString().contains(sb.toString().replace("-", "")) ) {
                            mje.mostrarDialog("La fecha de nacimiento ingresada no coincide con la del CURP.", "BanlieueService", regrepAct);
                        }
                        else {
                            if(etCurp.getText().toString().length()<18) {
                                mje.mostrarDialog("La CURP debe contener 18 caracteres.", "Banlieue Service", regrepAct);
                            }
                            else{
                                datosCompletos.put("tipoVe", etDatos.pop());
                                datosCompletos.put("placa", etDatos.pop());
                                datosCompletos.put("CURP", etDatos.pop());
                                infoJson.agregarDatos(datosCompletos);
                                ServicioWeb.obtenerInstancia(ctx).nuevaPersona(infoJson.strJSON(), new VolleyCallBack() {
                                    @Override
                                    public void onSuccess(String result) {
                                        //Mensaje de éxito del servidor
                                        mje.mostrarToast(result, 'l');

                                        //Inicio de sesión automático
                                        Intent intent = null;
                                        switch (datosCompletos.get("tipoPersona")) {
                                            case "usr": //Abrir panel de usuario
                                                intent = new Intent(RegistroRepActivity.this, PanelUsuarioActivity.class);
                                                break;

                                            case "cli": //Abrir panel de cliente
                                                intent = new Intent(RegistroRepActivity.this, PanelClienteActivity.class);
                                                break;

                                            case "rep": //Abrir panel de repartidor
                                                intent = new Intent(RegistroRepActivity.this, PanelRepartidorActivity.class);
                                                break;

                                            default: //Algo anda mal en el servidor
                                                mje.mostrarDialog("Servidor corrupto", "Banlieue Service", regrepAct);
                                                break;
                                        }
                                        //... Finalmente, se carga el correo verificado en el intent para que
                                        //el respectivo panel cargue la información completa en su información completa
                                        intent.putExtra("correo", datosCompletos.get("correo"));
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onJsonSuccess(String jsonResult) {

                                    }

                                    @Override
                                    public void onError(String result) {
                                        mje.mostrarDialog(result, "Banlieue Service", regrepAct);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }
}
