package com.example.banlieueservice.pedido;

public class ElementoDePedido {
    private String idProdserv, nombre, precio, cantidad;

    public ElementoDePedido(String idProdserv, String nombre, String precio, String cantidad){
        this.idProdserv= idProdserv;
        this.nombre= nombre;
        this.precio= precio;
        this.cantidad= cantidad;
    }

    public String obtIdProdServ(){
        return idProdserv;
    }

    public String obtNombre(){
        return nombre;
    }

    public String obtPrecio(){
        return precio;
    }

    public String obtCantidad(){
        return cantidad;
    }

    public void defCantidad(String cantidad){
        this.cantidad=cantidad;
    }

    @Override
    public String toString(){
        return "ID "+idProdserv+": "+nombre+" => $"+precio+", piden "+cantidad;
    }


    public boolean equals(ElementoDePedido elementoDePedido){
        if(
                this.idProdserv.equals(elementoDePedido.idProdserv) &&
                this.nombre.equals(elementoDePedido.nombre) &&
                this.precio.equals(elementoDePedido.precio) &&
                this.cantidad.equals(elementoDePedido.cantidad)
        ){
            return true;
        }
        else{
            return false;
        }
    }
}
