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

public class PanRepModifDatosServ extends Fragment implements View.OnClickListener {
    private Context ctx;
    private FragmentActivity act;
    private EditText etVehiculo, etPlaca, etCurp;
    private Button btnModif;
    private Mensaje mje;
    private Map<String, String> datosActuales;

    public PanRepModifDatosServ(Map<String, String> datosActuales){
        this.datosActuales= datosActuales;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {

        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);

        return li.inflate(R.layout.fragment_panrepmodifdatosserv, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        initComponents();

        //Colocar datos actuales en EditText
        etVehiculo.setText(datosActuales.get("vehiculo"));
        etPlaca.setText(datosActuales.get("placa"));
        etCurp.setText(datosActuales.get("CURP"));
    }

    @Override
    public void onClick(View v){
        if(v.getId()==R.id.btnModificarServ){

            String vehiculo="", placa="", curp="";
            vehiculo= etVehiculo.getText().toString();
            placa= etPlaca.getText().toString();
            curp= etCurp.getText().toString();

            StringBuilder sb= new StringBuilder(datosActuales.get("fechanac"));
            sb.delete(0, 2);
            if (!curp.contains(sb.toString().replace("-", ""))) {
                mje.mostrarDialog("La fecha de nacimiento ingresada no coincide con la del CURP.", "BanlieueService", (AppCompatActivity)act);
            } else {
                if (curp.length() < 18) {
                    mje.mostrarDialog("La CURP debe contener 18 caracteres.", "Banlieue Service", (AppCompatActivity)act);
                } else {
                    if( vehiculo.equals("") || placa.equals("") || curp.equals("") )
                        mje.mostrarToast("Debe ingresar todos los datos necesarios", 'l');
                    else{
                        JSON json = new JSON();
                        json.agregarDato("datos", "serv"); //Clave para modificar datos personales
                        json.agregarDato("idrep", datosActuales.get("idPartic"));
                        json.agregarDato("idve", datosActuales.get("idVe"));
                        json.agregarDato("vehiculo", vehiculo);
                        json.agregarDato("placa", placa);
                        json.agregarDato("CURP", curp);

                        ServicioWeb.obtenerInstancia(ctx).modificarDatosServicio(json.strJSON(), new VolleyCallBack() {
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
        }
    }

    //Reemplazo temporal de datos.
    //Si se invoca este método, los datos se cambiaron correctamente pero no se reflejan aún,
    //para evitar confución en el usuario se modifican temporalmente en el mapa de datos actuales.
    //Para ver la modificación directa de la base, se debe iniciar sesión en banlieue de nuevo.
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void modificarDatosLocalmente(){
        datosActuales.replace("vehiculo", etVehiculo.getText().toString());
        datosActuales.replace("placa", etPlaca.getText().toString());
        datosActuales.replace("CURP", etCurp.getText().toString());
    }


    @SuppressLint("ResourceAsColor")
    private void initComponents(){
        btnModif= (Button) getView().findViewById(R.id.btnModificarServ);
        btnModif.setOnClickListener(this);

        etVehiculo= (EditText) getView().findViewById(R.id.etModifVehiculo);
        etPlaca= (EditText) getView().findViewById(R.id.etModifPlaca);
        etCurp= (EditText) getView().findViewById(R.id.etModifCurp);

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
