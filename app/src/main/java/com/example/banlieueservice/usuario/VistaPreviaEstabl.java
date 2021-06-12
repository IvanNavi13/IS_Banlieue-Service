package com.example.banlieueservice.usuario;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.banlieueservice.R;
import com.example.banlieueservice.cliente.PanCliServsLocal;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.interfaces.FragmentCommunicator;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;

import java.util.Map;

public class VistaPreviaEstabl extends AppCompatDialogFragment implements FragmentCommunicator {
    private Mensaje mje;
    private FragmentActivity act;
    private TextView tvNombre, tvDireccion, tvGiro, tvApertura, tvCierre;
    private Map<String, String> informacion;
    private Map<String, String> datosUsuario;

    public VistaPreviaEstabl(Map<String, String> informacion){
        this.informacion= informacion;
    }

    @Override
    public Dialog onCreateDialog(Bundle b){
        act= getActivity();
        mje= new Mensaje(act);

        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());

        LayoutInflater inflater= getActivity().getLayoutInflater();
        View view= inflater.inflate(R.layout.dialogfragment_vistapreviaestabl, null);

        tvNombre= (TextView) view.findViewById(R.id.tvVpnegNombre);
        tvGiro= (TextView) view.findViewById(R.id.tvVpnegGiro);
        tvDireccion= (TextView) view.findViewById(R.id.tvVpnegDireccion);
        tvApertura= (TextView) view.findViewById(R.id.tvVpnegApertura);
        tvCierre= (TextView) view.findViewById(R.id.tvVpnegCierre);

        tvNombre.setText(informacion.get("nombre"));
        //Giro sin el truco del índice: N@
        tvGiro.setText( (new StringBuilder(informacion.get("giro"))).delete(0, 2) );
        tvDireccion.setText(informacion.get("direccion"));
        //Quirar los últimos 2 ceros (los segundos) que vienen de la consulta a la base
        tvApertura.setText( "Horario de apertura: "+(new StringBuilder(informacion.get("apertura"))).delete(5, informacion.get("apertura").length()) );
        tvCierre.setText("Horario de cierre: "+(new StringBuilder(informacion.get("cierre"))).delete(5, informacion.get("cierre").length()));


        builder.setView(view);
        builder.setTitle("Información");

        builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton("Pedir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent= new Intent(act, CreadorDePedidos.class);
                Bundle bundle= new Bundle();
                bundle.putString("idEst", informacion.get("idEst"));
                bundle.putString("correo", datosUsuario.get("correo"));
                bundle.putString("idUsuario", datosUsuario.get("idPartic"));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });



        return builder.create();
    }

    @Override
    public void sendData(Map<String, String> data) {
        datosUsuario=data;
    }
    @Override
    public void sendSingleData(Object data) {

    }
}
