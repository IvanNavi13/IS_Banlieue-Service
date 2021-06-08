package com.example.banlieueservice.repartidor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.banlieueservice.R;
import com.example.banlieueservice.herramientas.Mensaje;

import java.util.Map;

public class PanRepPedidosReal extends Fragment {
    private Context ctx;
    private FragmentActivity act;
    private Button btn;
    private Mensaje mje;
    private Map<String, String> datosActuales;

    public PanRepPedidosReal(/*Map<String, String> datosActuales*/){
        //this.datosActuales= datosActuales;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {

        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);

        return li.inflate(R.layout.fragment_reppedidosreal, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);

        btn= (Button) getView().findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mje.mostrarToast("Pedidos realizados B)", 'c');
            }
        });
    }

}
