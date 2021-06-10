package com.example.banlieueservice.cliente;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.banlieueservice.R;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;

import java.util.LinkedList;

public class PanCliNvoServicio extends AppCompatDialogFragment {
    private String nombreNegocio, idNegocio;
    private Mensaje mje;
    private EditText etNombre, etDescripcion, etPrecio;
    private TextView tvNegocioNvoServ;

    public PanCliNvoServicio(String idNegocio, String nombreNegocio){
        this.nombreNegocio= nombreNegocio;
        this.idNegocio= idNegocio;
    }

    @Override
    public Dialog onCreateDialog(Bundle b){
        mje= new Mensaje(getActivity());
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());

        LayoutInflater inflater= getActivity().getLayoutInflater();
        View view= inflater.inflate(R.layout.dialogfragment_nvoserv, null);

        etNombre= (EditText) view.findViewById(R.id.etServNombre);
        etDescripcion= (EditText) view.findViewById(R.id.etServDesc);
        etPrecio= (EditText) view.findViewById(R.id.etServPrecio);
        tvNegocioNvoServ= (TextView) view.findViewById(R.id.tvNegocioNvoServ);
        tvNegocioNvoServ.setText("Nuevo servicio de "+nombreNegocio);

        builder.setView(view);
        builder.setTitle("Nuevo");

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mje.mostrarToast("Ok", 'c');
            }
        });

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre= etNombre.getText().toString();
                String desc= etDescripcion.getText().toString();
                String precio= etPrecio.getText().toString();
                if(nombre.equals("") || desc.equals("") || precio.equals("")){
                    mje.mostrarToast("Llene todos los campos", 'c');
                }
                else{
                    JSON json = new JSON();
                    json.agregarDato("idEst", idNegocio); //Enviar al servidor clave de indicación de Usuario (para saber qué procedure llamar)
                    json.agregarDato("nombre", nombre);
                    json.agregarDato("descripcion", desc);
                    json.agregarDato("precio", precio);
                    ServicioWeb.obtenerInstancia(getActivity()).nuevoServicioEst(json.strJSON(), new VolleyCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            mje.mostrarToast(result, 'l');
                        }

                        @Override
                        public void onJsonSuccess(String jsonResult) {

                        }

                        @Override
                        public void onError(String result) {
                            mje.mostrarDialog(result, "Banlieue Service", (AppCompatActivity) getActivity());
                        }
                    });
                }
            }
        });

        return builder.create();
    }

}
