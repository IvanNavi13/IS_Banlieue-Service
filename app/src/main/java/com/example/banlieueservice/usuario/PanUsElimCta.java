package com.example.banlieueservice.usuario;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
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

public class PanUsElimCta extends Fragment implements View.OnClickListener {
    private Mensaje mje;
    private Context ctx;
    private FragmentActivity act;
    private Map<String, String> datosActuales;
    private Button btnElim;
    private EditText etComent;


    public PanUsElimCta(Map<String, String> datosActuales){
        this.datosActuales= datosActuales;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {

        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);

        return li.inflate(R.layout.fragment_panuselimcta, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        initComponents();
        mje.mostrarDialog("Si elimina su cuenta, todos los datos referentes a la misma serán eliminados, " +
                        "si desea regresar a Banlieue Service deberá registrarse nuevamente.",
                "Banlieue Service",
                (AppCompatActivity)act
        );
    }

    @Override
    public void onClick(View view){
        if(view.getId()==R.id.btnEliminar){
            JSON json = new JSON();
            json.agregarDato("tipoPersona", "usr"); //Clave de usuario
            json.agregarDato("idPersona", datosActuales.get("idPersona"));
            json.agregarDato("idPartic", datosActuales.get("idPartic"));
            json.agregarDato("comentarios", etComent.getText().toString());

            ServicioWeb.obtenerInstancia(ctx).eliminarPersona(json.strJSON(), new VolleyCallBack() {
                @Override
                public void onSuccess(String result) {
                    mje.mostrarToast(result,'l');
                    Intent intent= new Intent(act, MainActivity.class);
                    act.startActivity(intent);
                    act.finish();
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

    private void initComponents(){
        etComent= (EditText) getActivity().findViewById(R.id.etComentarios);

        btnElim= (Button) getActivity().findViewById(R.id.btnEliminar);
        btnElim.setOnClickListener(this);
    }
}
