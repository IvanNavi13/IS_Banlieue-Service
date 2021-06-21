package com.example.banlieueservice.herramientas;

import java.util.LinkedHashMap;

public class Utilidad {
    public static String strAcercaDe=
            "Instituto Politécnico Nacional\n\n" +
            "Escuela Superior de Cómputo\n\n\n" +
            "Ingeniería de software, 3CM11\n\n" +
            "Profa: Cordero López Martha Rosa\n\n"+
            "Equipo:\n"+
            "\tBenítez Morales Manuel Emilio\n"+
            "\tCisneros Rosales Christian Ivan\n"+
            "\tEscorcia Peña José Daniel\n"+
            "\tGarcía García Cano Ivan\n\n"+
            "Junio 2021\n\n"+
            "Versión 1.1";
    private LinkedHashMap<String, String> mapa= new LinkedHashMap<>();

    public Utilidad(){
        mapa.put("01", "Enero");
        mapa.put("02", "Febrero");
        mapa.put("03", "Marzo");
        mapa.put("04", "Abril");
        mapa.put("05", "Mayo");
        mapa.put("06", "Junio");
        mapa.put("07", "Julio");
        mapa.put("08", "Agosto");
        mapa.put("09", "Septiembre");
        mapa.put("10", "Octubre");
        mapa.put("11", "Noviembre");
        mapa.put("12", "Diciembre");
    }

    /*
    Cálculo de edad.
    Recibe: Fecha de nacimiento en formato yyyy-MM-dd (preferentemente).
    Devuelve: Edad en número entero.
    */
    public int edad(String fechaNac){
        String[] fechaAct= new Fecha("yyyy-MM-dd").getSFecha().split("-");
        int retEdad=0;

        //date_default_timezone_set("America/Mexico_City");

        String[] fnP= fechaNac.split("-");//Variable para la FECHA DE NACIMIENTO POR PARTES

        int diaA= Integer.parseInt(fechaAct[2]);
        int mesA= Integer.parseInt(fechaAct[1]);
        int anioA= Integer.parseInt(fechaAct[0]);

        int diaO= Integer.parseInt(fnP[2]);
        int mesO= Integer.parseInt(fnP[1]);
        int anioO= Integer.parseInt(fnP[0]);

        if(diaA==diaO && mesA==mesO){
            retEdad= anioA-anioO;
        }

        else if((diaA<diaO || diaA>diaO) && mesA<mesO){
            retEdad=(anioA-anioO)-1;
        }

        else if(diaA<diaO && mesA==mesO){
            retEdad=(anioA-anioO)-1;
        }

        else if((diaA<diaO || diaA>diaO) && mesA>mesO){
            retEdad= anioA-anioO;
        }

        else if(diaA>diaO && mesA==mesO){
            retEdad= anioA-anioO;
        }

        return retEdad;
    }

    public String mesAnumero(String mes){
        String ret=null;
        for(String llave: mapa.keySet()) {
            if (mapa.get(llave).equalsIgnoreCase(mes)) {
                ret = llave;
                break;
            }
        }
        return ret;
    }

    public String numeroAmes(int numero){
        StringBuilder sb= new StringBuilder("");
        if(numero<=9) sb.append("0").append(numero);
        else sb.append(numero);
        return mapa.get(sb.toString());
    }
}
