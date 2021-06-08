package com.example.banlieueservice.interfaces;

public interface VolleyCallBack {
    void onSuccess(String result); //Cuando hay respuesta del servidor (POST)
    void onJsonSuccess(String jsonResult); //Cuando hay respuesta del servidor en formato JSON (GET)
    void onError(String result); //Cuando hay mensaje de error
}

