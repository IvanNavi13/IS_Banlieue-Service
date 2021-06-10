package com.example.banlieueservice.cliente;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.banlieueservice.R;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;

public class PanCliModifServicio extends AppCompatDialogFragment {
    private String idServicio, nombreNegocio;
    private String[] datosServicio;
    private Mensaje mje;
    private EditText etNombre, etDescripcion, etPrecio;
    private TextView tvNegocioNvoServ;
    private PanCliServsLocal serviciosDelLocal;

    public PanCliModifServicio(String idServicio, String nombreNegocio, String[] datosServicio, PanCliServsLocal serviciosDelLocal){
        this.nombreNegocio= nombreNegocio;
        this.idServicio= idServicio;
        this.datosServicio= datosServicio;
        this.serviciosDelLocal=serviciosDelLocal;
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
        tvNegocioNvoServ.setText("Modificar servicio de "+nombreNegocio);

        etNombre.setText(datosServicio[0]);
        etDescripcion.setText(datosServicio[1]);
        etPrecio.setText(datosServicio[2]);

        builder.setView(view);
        builder.setTitle("Modificar");

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
                    json.agregarDato("idProdserv", idServicio); //Enviar al servidor clave de indicación de Usuario (para saber qué procedure llamar)
                    json.agregarDato("nombre", nombre);
                    json.agregarDato("descripcion", desc);
                    json.agregarDato("precio", precio);
                    ServicioWeb.obtenerInstancia(getActivity()).modificarServicioEst(json.strJSON(), new VolleyCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            mje.mostrarToast(result, 'l');
                            serviciosDelLocal.cargarInfoServicio(); //Para actualizar la lista a la vista del usuario
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
