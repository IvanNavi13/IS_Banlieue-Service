package com.example.banlieueservice.herramientas;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Fecha {
    private SimpleDateFormat sdf;
    private Calendar fecha;

    //El constructor recibe un formato que acepta el constructor de la clase SimpleDateFormat
    public Fecha(String formato){
        fecha = Calendar.getInstance();
        sdf= new SimpleDateFormat(formato);
    }

    //Si no se ingresa el formato, se llama al contructor sin parámetros de la clase SimpleDateFormat
    public Fecha(){
        fecha = Calendar.getInstance();
        sdf= new SimpleDateFormat();
    }

    //Los siguientes 3 métodos devuelven una fecha que se especifique
    public Date getDFecha(int dia, int mes, int anio){
        fecha.set(anio, mes, dia);
        return fecha.getTime();
    }

    public String getSFecha(int dia, int mes, int anio){
        fecha.set(anio, mes, dia);
        return sdf.format(fecha.getTime());
    }

    public Calendar getCFecha(int dia, int mes, int anio){
        fecha.set(anio, mes, dia);
        return fecha;
    }

    //Los siguientes 3 métodos devuelven la fecha actual
    public Date getDFecha(){
        return fecha.getTime();
    }

    public String getSFecha(){
        return sdf.format(fecha.getTime());
    }

    public Calendar getCFecha(){
        return fecha;
    }
}