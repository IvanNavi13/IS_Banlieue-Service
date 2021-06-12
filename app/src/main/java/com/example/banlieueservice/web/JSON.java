package com.example.banlieueservice.web;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class JSON {
    private JSONObject json;
    private JSONArray array;
    private int indiceArray;


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

    //el parámetro jsonStr es de tipo arreglo: [{}, {}]
    public LinkedList<Map<String, String>> obtenerDatosArreglo(String jsonStr, String llaveDeArreglo){
        LinkedList<Map<String, String>> listaMapas= new LinkedList<>();

        try{
            JSONArray jsonArray= new JSONObject(jsonStr).getJSONArray(llaveDeArreglo);
            JSONObject jobj;
            for(int i=0; i<jsonArray.length(); i++){
                jobj= jsonArray.getJSONObject(i);
                listaMapas.add(obtenerDatos(jobj.toString()));
            }
        }catch(JSONException ex){
            ex.printStackTrace();
        }

        return listaMapas;
    }

    public String strJSON(){
        return json.toString();
    }

    //Agregar objetos JSON al arreglo
    public void agregarObjeto(JSON obj){
        array.put(obj.json);
    }

    public String strArregloJSON(){
        return array.toString();
    }
}
