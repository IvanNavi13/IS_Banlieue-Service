package com.example.banlieueservice.cliente;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.banlieueservice.R;
import com.example.banlieueservice.herramientas.Mensaje;

public class PanCliServsLocal extends Fragment {
    private Context ctx;
    private FragmentActivity act;
    private Mensaje mje;

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {

        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);

        return li.inflate(R.layout.fragment_cliservslocal, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        initComponents();

    }

    private void initComponents(){

    }
}
