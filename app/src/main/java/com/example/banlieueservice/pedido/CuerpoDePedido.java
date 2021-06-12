package com.example.banlieueservice.pedido;

import com.example.banlieueservice.web.JSON;

import java.util.LinkedList;

public class CuerpoDePedido{
    private LinkedList<ElementoDePedido> cuerpoPedido;

    public CuerpoDePedido(LinkedList<ElementoDePedido> cuerpoPedido){
        this.cuerpoPedido= cuerpoPedido;
    }

    public LinkedList<ElementoDePedido> elementos(){
        return cuerpoPedido;
    }

    public int numDeProdServ(){
        return cuerpoPedido.size();
    }

    public boolean estaVacio(){
        return cuerpoPedido.isEmpty();
    }

    public String toJSONString(){
        JSON jsonElemento;
        JSON jsonPedido= new JSON();

        for(ElementoDePedido e: cuerpoPedido){
            jsonElemento= new JSON();
            jsonElemento.agregarDato("idProdserv", e.obtIdProdServ());
            jsonElemento.agregarDato("cantidad", e.obtCantidad());
            jsonPedido.agregarObjeto(jsonElemento);
        }

        return jsonPedido.strArregloJSON();
    }

    @Override
    public String toString(){
        StringBuilder sb= new StringBuilder("");
        for(ElementoDePedido e: cuerpoPedido)
            sb.append(e.toString()).append("\n");
        return sb.toString();
    }
}
