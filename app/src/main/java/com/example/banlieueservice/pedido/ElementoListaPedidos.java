package com.example.banlieueservice.pedido;

import java.util.Map;

//Encapsulamiento de los elementos de las listas con información (ListView) de servicios, o alguna otra lista
public class ElementoListaPedidos {
    private Map<String, String> datosPedido;

    public ElementoListaPedidos(Map<String, String> datosPedido) {
        this.datosPedido=datosPedido;
    }

    public String obtIdPedido(){
        return datosPedido.get("idPed");
    }

    public String obtPersona(){
        return datosPedido.get("persona");
    }

    public String obtTelefonoPersona(){
        return datosPedido.get("telefono");
    }

    public String obtDireccionEntrega(){
        return datosPedido.get("direccion");
    }

    public String obtFechaPedido(){
        return datosPedido.get("fecha");
    }

    public String obtHoraPedido(){
        StringBuilder sb= new StringBuilder(datosPedido.get("hora"));
        sb.delete(5, 8);
        return sb.toString();
    }
}
