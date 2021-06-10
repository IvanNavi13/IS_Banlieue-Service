package com.example.banlieueservice.herramientas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.LinkedList;

//Adaptador encapsulado para los elementos de la lista
public abstract class AdaptadorLista extends BaseAdapter {
    private LinkedList<?> elementos;
    private int idLayoutContenedor;
    private Context ctx;

    public AdaptadorLista(Context ctx, int idLayoutContenedor, LinkedList<?> elementos){
        super();
        this.ctx=ctx;
        this.idLayoutContenedor=idLayoutContenedor;
        this.elementos=elementos;
    }

    @Override
    public View getView(int i, View v, ViewGroup vg) {
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(idLayoutContenedor, null);
        }
        onEntrada(elementos.get(i), v);
        return v;
    }

    @Override
    public int getCount() {
        return elementos.size();
    }
    @Override
    public Object getItem(int j) {
        return elementos.get(j);
    }
    @Override
    public long getItemId(int k) {
        return k;
    }

    public abstract void onEntrada (Object o, View v);
}
