package com.example.banlieueservice.pedido;

import java.util.Map;

//Encapsulamiento de los elementos de las listas con información (ListView) de servicios, o alguna otra lista
public class ElementoListaServiciosPedidos {
    private String servicio, cantidad, precio;

    public ElementoListaServiciosPedidos(String servicio, String cantidad, String precio) {
        this.servicio=servicio;
        this.cantidad=cantidad;
        this.precio=precio;
    }

    public String obtServicio(){
        return servicio;
    }

    public String obtCantidad(){
        return cantidad;
    }

    public String obtPrecioTotal(){
        double total= Double.parseDouble(cantidad) * Double.parseDouble(precio);
        String ret= String.valueOf(total);

        if(ret.contains("."))
            ret= ret.concat("0");

        return "Total: $"+ret;
    }

}
