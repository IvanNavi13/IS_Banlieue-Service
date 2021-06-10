package com.example.banlieueservice.herramientas;

//Encapsulamiento de los elementos de las listas con información (ListView) de servicios, o alguna otra lista
public class ElementoLista {
    private int idDrawableImagen;
    private String nombre;
    private String descripcion;

    public ElementoLista (int idDrawableImagen, String nombre, String descripcion) {
        this.idDrawableImagen = idDrawableImagen;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    public String obtNombre() {
        return nombre;
    }

    public String obtDescripcion() {
        return descripcion;
    }

    //Devuelve el id del drawable
    public int obtImagen() {
        return idDrawableImagen;
    }
}
