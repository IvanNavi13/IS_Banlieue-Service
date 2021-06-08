package com.example.banlieueservice.web;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.banlieueservice.interfaces.VolleyCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ServicioWeb {
    private Context ctx;

    //Modificar IP del servidor que se esté usando
    private final String ip= "192.168.100.7";
    //private final String ip= "192.168.43.240";
    //private final String ip= "192.168.0.17";
    /////////////////////////////////////////////////

    private StringBuilder url;
    private static ServicioWeb servweb;
    private RequestQueue queue;

    private ServicioWeb(Context ctx){
        this.ctx= ctx;
        queue= Volley.newRequestQueue(this.ctx);
    }

    public static synchronized ServicioWeb obtenerInstancia(Context ctx){
        if(servweb==null)
            servweb= new ServicioWeb(ctx);
        return servweb;
    }

    public void nuevaPersona(String jsonStr, VolleyCallBack vcb){
        definirURL("http://"+ip+"/BanlieueService/php/Persona.php");
        altaModifElim(jsonStr, vcb, Request.Method.POST);
    }

    public void infoPersona(String jsonStr, VolleyCallBack vcb){
        definirURL("http://"+ip+"/BanlieueService/php/Persona.php?json="+jsonStr);
        consulta(vcb);
    }

    public void iniciarSesion(String jsonStr, VolleyCallBack vcb){
        definirURL("http://"+ip+"/BanlieueService/php/IniciarSesion.php?json="+jsonStr);
        consulta(/*jsonStr, */vcb);
    }

    public void modificarDatosPersonales(String jsonStr, VolleyCallBack vcb){
        definirURL("http://"+ip+"/BanlieueService/php/Persona.php");
        altaModifElim(jsonStr, vcb, Request.Method.PUT);
    }

    public void modificarDatosServicio(String jsonStr, VolleyCallBack vcb){
        modificarDatosPersonales(jsonStr, vcb);
    }

    public void eliminarPersona(String jsonStr, VolleyCallBack vcb){
        definirURL("http://"+ip+"/BanlieueService/php/Persona.php");
        altaModifElim(jsonStr, vcb, Request.Method.PATCH); //Con DELETE no funcionó así que usamos PATCH
    }


    //****************************** MÉTODOS PARA USO DE LA API REST ******************************//
    private void altaModifElim(String jsonStr, VolleyCallBack vcb, int metodo){ //Método POST
        JsonObjectRequest peticion = new JsonObjectRequest(
                metodo, url.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    vcb.onSuccess( response.getString("respuesta") );
                } catch (JSONException e) {
                    vcb.onError("Error al recibir respuesta:\n"+e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                vcb.onError("Error: "+error.toString());
            }
        }
        )
        {
            public byte[] getBody() {
                try {
                    return jsonStr == null ? null : jsonStr.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    vcb.onError("Codificación no soportada obteniendo los datos de "+jsonStr);
                    return null;
                }
            }
        };
        queue.add(peticion);
    }

    private void consulta(/*String jsonStr, */VolleyCallBack vcb){ //Método GET
        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.GET, url.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    vcb.onJsonSuccess( response.getString("respuesta") );
                } catch (JSONException e) {
                    vcb.onError("Error al recibir respuesta:\n"+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                vcb.onError("Error: "+error.toString());
            }
        }
        );
        queue.add(peticion);
    }

    private void definirURL(String url){
        this.url= new StringBuilder(url);
    }

    private String obtenerURL(){
        return url.toString();
    }


}
