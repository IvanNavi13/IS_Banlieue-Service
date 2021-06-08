package com.example.banlieueservice.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.banlieueservice.R;
import com.example.banlieueservice.cliente.PanelClienteActivity;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.repartidor.PanelRepartidorActivity;
import com.example.banlieueservice.usuario.PanelUsuarioActivity;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;

import java.util.LinkedList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private AppCompatActivity mainAct;
    private TextView tvRegistrar;
    private Button btnEntrar;
    private EditText etCorreo, etContra;
    private Mensaje mje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainAct= this;
        setContentView(R.layout.activity_main);
        initComponents();
        mje= new Mensaje(this);


        tvRegistrar.setText(
                Html.fromHtml("<a href='#'>¿Todavía no tienes cuenta? ¡Regístrate!</a>")
        );
    }

    @Override
    public void onClick(View view){
        int pressed= view.getId();
        switch(pressed){
            case R.id.btnEntrar:
                //Zona para pruebas/////////////////
                /*Intent intentp= new Intent(MainActivity.this, PanelUsuarioActivity.class);
                //Intent intentp= new Intent(MainActivity.this, MapaActivity.class);
                intentp.putExtra("correo", "dvd");
                etContra.setText("");
                etCorreo.setText("");
                startActivity(intentp);
                finish();*/
                ////////////////////////////////////

                if(obtenerDatosET().contains(""))
                    mje.mostrarToast("Ingrese correo y contraseña", 'c');
                else {
                    //Para iniciar la sesión: primero se crea un JSON con el correo y contraseña ingresados...
                    JSON json = new JSON();
                    json.agregarDato("contrasena", etContra.getText().toString());
                    json.agregarDato("correo", etCorreo.getText().toString());
                    //... el servidor verifica la validez de esos datos y regresa una respuesta...
                    ServicioWeb.obtenerInstancia(this).iniciarSesion(json.strJSON(), new VolleyCallBack() {
                        @Override
                        public void onSuccess(String result) {

                        }

                        //...aquí (es en forma JSON porque regresa varias cosas)...
                        @Override
                        public void onJsonSuccess(String jsonResult) {
                            //...entonces se verifica la respuesta de acceso para llevar a un panel específico...
                            Map<String, String> info = json.obtenerDatos(jsonResult);
                            if(info.get("ini").equals("acceso")){
                                Intent intent=null;
                                switch(info.get("tipoPersona")){
                                    case "usr": //Abrir panel de usuario
                                        intent= new Intent(MainActivity.this, PanelUsuarioActivity.class);
                                        break;

                                    case "cli": //Abrir panel de cliente
                                        intent= new Intent(MainActivity.this, PanelClienteActivity.class);
                                        break;

                                    case "rep": //Abrir panel de repartidor
                                        intent= new Intent(MainActivity.this, PanelRepartidorActivity.class);
                                        break;

                                    default: //Algo anda mal en el servidor
                                        mje.mostrarDialog("Servidor corrupto", "Banlieue Service", mainAct);
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
                            else
                                mje.mostrarDialog(info.get("ini"), "Iniciar", mainAct);

                        }

                        @Override
                        public void onError(String result) {
                            mje.mostrarDialog(result, "Banlieue Service", mainAct);
                        }
                    });
                }

                break;

            case R.id.tvRegistrar:
                Intent intent= new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(intent);
                break;
        }
    }

    private LinkedList<String> obtenerDatosET(){
        LinkedList<String> ret= new LinkedList<>();

        ret.add(etCorreo.getText().toString());
        ret.add(etContra.getText().toString());

        return ret;
    }

    private void initComponents(){
        tvRegistrar= (TextView) findViewById(R.id.tvRegistrar);
        tvRegistrar.setOnClickListener(this);

        btnEntrar= (Button) findViewById(R.id.btnEntrar);
        btnEntrar.setOnClickListener(this);

        etContra= (EditText) findViewById(R.id.etContra);
        etCorreo= (EditText) findViewById(R.id.etCorreo);
    }


}