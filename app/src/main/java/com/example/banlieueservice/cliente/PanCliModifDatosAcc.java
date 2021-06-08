package com.example.banlieueservice.cliente;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.banlieueservice.R;
import com.example.banlieueservice.actividades.MainActivity;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;

import java.util.Map;

public class PanCliModifDatosAcc extends Fragment implements View.OnClickListener {
    private Context ctx;
    private FragmentActivity act;
    private EditText etCorreo, etContraAct, etContraNva, etConfContraNva;
    private Button btnModif;
    private CheckBox soloCorreo;
    private Mensaje mje;
    private Map<String, String> datosActuales;

    public PanCliModifDatosAcc(Map<String, String> datosActuales){
        this.datosActuales= datosActuales;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {

        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);

        mje.mostrarToast("Sea cuidadoso al modificar esta información", 'c');

        return li.inflate(R.layout.fragment_panusmodifdatosacc, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        initComponents();
        mje.mostrarDialog("Después de modificar esta información, deberá iniciar sesión nuevamente, " +
                "es recomendable que la resguarde en otro lugar si le es difícil recordar el cambio.",
                "Banlieue Service",
                (AppCompatActivity)act
        );

        //Colocar datos actuales en EditText
        etCorreo.setText(datosActuales.get("correo"));
    }

    @Override
    public void onClick(View v){
        if(v.getId()==R.id.btnModificarAcc){
            String correo=etCorreo.getText().toString();
            String contraAct=etContraAct.getText().toString();
            String contraNva= etContraNva.getText().toString();
            String contraNvaVerif= etConfContraNva.getText().toString();

            if( correo.equals("") || contraAct.equals("") || contraNva.equals("") || contraNvaVerif.equals("") )
                mje.mostrarToast("Por seguridad, todos los campos deben llenarse", 'c');
            else{
                if(validarContrasena(contraAct, contraNva, contraNvaVerif)) {
                    JSON json = new JSON();
                    json.agregarDato("datos", "acc"); //Clave para modificar datos de acceso
                    json.agregarDato("id", datosActuales.get("idPersona"));
                    json.agregarDato("correo", correo);
                    json.agregarDato("contrasena", contraNva);

                    ServicioWeb.obtenerInstancia(ctx).modificarDatosPersonales(json.strJSON(), new VolleyCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            mje.mostrarToast(result, 'l');
                            Intent intent= new Intent(act, MainActivity.class);
                            act.startActivity(intent);
                            act.finish();
                        }

                        @Override
                        public void onJsonSuccess(String jsonResult) {

                        }

                        @Override
                        public void onError(String result) {
                            mje.mostrarDialog(result, "Banlieue Service", (AppCompatActivity) act);
                        }
                    });
                }
            }
        }
    }

    private boolean validarContrasena(String actual, String nueva, String confNueva){
        boolean bndValido=true;

        if(!actual.equals(datosActuales.get("contrasena"))){
            mje.mostrarToast("La contraseña actual es incorrecta", 'c');
            bndValido=false;
        }
        else{
            if(!nueva.equals(confNueva)){
                mje.mostrarToast("La contraseña nueva no coincide", 'c');
                bndValido=false;
            }
        }

        return bndValido;
    }

    private void initComponents(){
        btnModif= (Button) getView().findViewById(R.id.btnModificarAcc);
        btnModif.setOnClickListener(this);

        etCorreo= (EditText) getView().findViewById(R.id.etModifCorreo);
        etContraNva= (EditText) getView().findViewById(R.id.etModifContra);
        etConfContraNva= (EditText) getView().findViewById(R.id.etModifVerifContra);
        etContraAct= (EditText) getView().findViewById(R.id.etModifContraActual);

        soloCorreo= (CheckBox) getView().findViewById(R.id.checkSoloCorreo);
        soloCorreo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    etContraAct.setText(datosActuales.get("contrasena"));
                    etContraNva.setText(datosActuales.get("contrasena"));
                    etConfContraNva.setText(datosActuales.get("contrasena"));
                }
                else{
                    etContraAct.setText("");
                    etContraNva.setText("");
                    etConfContraNva.setText("");
                }
            }
        });
    }
}
