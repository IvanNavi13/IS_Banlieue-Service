package com.example.banlieueservice.cliente;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.banlieueservice.R;
import com.google.android.gms.maps.GoogleMap;

public class PanCliHome extends Fragment{
    private GoogleMap mMap;
    private Bundle bundle;
    private Context ctx;

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        return li.inflate(R.layout.fragment_panclihome, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);

    }

}
