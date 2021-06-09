package com.example.banlieueservice.interfaces;

import java.util.Map;

public interface FragmentCommunicator {
    //Implementación para pasar datos entre fragments
    void sendData(Map<String, String> data);
    void sendSingleData(Object data);
}
