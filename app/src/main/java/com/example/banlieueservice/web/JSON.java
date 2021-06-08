package com.example.banlieueservice.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JSON {
    private JSONObject json;
    private static JSONArray array;
    private static int indiceArray;


    public JSON(){
        indiceArray=0;
        json= new JSONObject();
        array= new JSONArray();
    }

    //Agregar datos al objeto JSON
    public void agregarDato(String llave, String valor){
        try {
            json.put(llave, valor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void agregarDatos(Map<String, String> datos){
        try {

            for(String llave: datos.keySet())
                json.put(llave, datos.get(llave));

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public Map<String, String> obtenerDatos(String jsonStr){
        Map<String, String> mapaDatos= new HashMap<>();
        JSONObject jo;

        try {
            jo= new JSONObject(jsonStr);
            Iterator<?> campo = jo.keys();
            String llave;
            while (campo.hasNext()) {
                llave = (String) campo.next();
                mapaDatos.put(llave, jo.getString(llave));
                //System.out.println(llave + " => " + jo.getString(llave));
            }
        }catch(JSONException ex){
            ex.printStackTrace();
        }

        return mapaDatos;
    }

    public String strJSON(){
        return json.toString();
    }

    //Agregar objetos JSON al arreglo
    public static void agregarObjeto(JSON obj){
        array.put(obj.json);
    }

    public static String obtArregloJSON(){
        return array.toString();
    }
}
