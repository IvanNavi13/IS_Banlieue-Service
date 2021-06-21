package com.example.banlieueservice.cliente;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.ArrayRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.banlieueservice.R;
import com.example.banlieueservice.actividades.RegistroRepActivity;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.herramientas.Utilidad;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.repartidor.PanelRepartidorActivity;
import com.example.banlieueservice.usuario.PanelUsuarioActivity;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class PanCliNvoNegocio extends Fragment implements OnClickListener {
    private Context ctx;
    private FragmentActivity act;
    private Button btnRegistrar, btnImagen;
    private EditText etNombre, etDireccion;
    private ImageView imgNegocio;
    private Spinner spHoraAp, spMinAp, spHoraCi, spMinCi, spGiro;
    private Mensaje mje;
    private String idCliente;
    private Bitmap bmImgNegocio;

    public PanCliNvoNegocio(String idCliente){
        this.idCliente= idCliente;
    }


    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {

        ctx= getContext();
        act= getActivity();
        mje= new Mensaje(ctx);

        return li.inflate(R.layout.fragment_panclinvonegocio, vg, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        initComponents();

        contenidoSpinner(spHoraAp, R.array.hora24, R.layout.spinner_tipous_item);
        contenidoSpinner(spHoraCi, R.array.hora24, R.layout.spinner_tipous_item);
        contenidoSpinner(spMinAp, R.array.minuto, R.layout.spinner_tipous_item);
        contenidoSpinner(spMinCi, R.array.minuto, R.layout.spinner_tipous_item);
        contenidoSpinner(spGiro, R.array.giro_negocio, R.layout.spinner_tipous_item);

    }


    @Override
    public void onClick(View view){
        Map<String, String> datos;

        int pressed= view.getId();
        switch(pressed){
            case R.id.btnRegistrar:
                String nombre= etNombre.getText().toString();
                String direc= etDireccion.getText().toString();
                String apertura= spHoraAp.getSelectedItem()+":"+spMinAp.getSelectedItem();
                String cierre= spHoraCi.getSelectedItem()+":"+spMinCi.getSelectedItem();

                if(nombre.equals("") || direc.equals("") || bmImgNegocio==null)
                    mje.mostrarToast("Ingrese todos los datos", 'c');
                else{
                    Map<String, String> mapaDatos= new HashMap<>();
                    //JSON json= new JSON();
                    mapaDatos.put("idCliente", idCliente);
                    mapaDatos.put("nombre", nombre);
                    mapaDatos.put("giro", String.valueOf(spGiro.getSelectedItemPosition())+"@"+spGiro.getSelectedItem());
                    mapaDatos.put("direccion", direc);
                    mapaDatos.put("apertura", apertura);
                    mapaDatos.put("cierre", cierre);
                    mapaDatos.put("img", toStringImage(bmImgNegocio));
                    
                    ServicioWeb.obtenerInstancia(ctx).nuevoEstablecimiento(mapaDatos, new VolleyCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            mje.mostrarDialog(result, "Banlieue Service", (AppCompatActivity)act);
                            etDireccion.setText("");
                            etNombre.setText("");
                            spGiro.setSelection(0);
                            spHoraAp.setSelection(0);
                            spMinAp.setSelection(0);
                            spHoraCi.setSelection(0);
                            spMinCi.setSelection(0);
                            bmImgNegocio=null;
                        }

                        @Override
                        public void onJsonSuccess(String jsonResult) {

                        }

                        @Override
                        public void onError(String result) {
                            mje.mostrarDialog(result, "Banlieue Service", (AppCompatActivity)act);
                        }
                    });
                }
                break;

            case R.id.btnCargarImagen:
                cargarImagen();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){ //Si se ha seleccionado una imagen
            try {
                Uri path= data.getData();
                bmImgNegocio= MediaStore.Images.Media.getBitmap(act.getContentResolver(), path);

                setImage(resizeBitmap(bmImgNegocio, 1024), imgNegocio);
            } catch (IOException e) {
                //e.printStackTrace();
                mje.mostrarDialog("Error cargando imagen\n"+e, "Banlieue Service", (AppCompatActivity)act);
            }
        }
    }

    private void cargarImagen(){
        Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent, "Seleccionar imagen"), 10);
    }

    //Este método envía una imagen en un bitmap a un ImageView
    private void setImage(Bitmap bitmap, ImageView imageView){
        ByteArrayOutputStream baos= new ByteArrayOutputStream();

        //Compresión del bitmap
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        //Decodificar
        Bitmap decodificado= BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
        imageView.setImageBitmap(decodificado);
    }

    //Este método reescala un bitmap a una dimensión maxSize específica
    private Bitmap resizeBitmap(Bitmap bitmap, int maxSize){
        int width= bitmap.getWidth();
        int height= bitmap.getHeight();

        //Si el ancho y alto del bitmap son menores al máximo tamaño, entonces no se escala
        if(width<=maxSize && height<=maxSize){
            return bitmap;
        }

        float bmRatio= (float)width / (float) height;
        if(bmRatio>1){ //Si el ancho es mayor que el alto
            width= maxSize;
            height= (int)(width/bmRatio);
        }
        else{
            width= (int)(height*bmRatio);
            height= maxSize;
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    //Este método obtiene la codificación base 64 para poder subir a la BDD
    private String toStringImage(Bitmap bitmap){
        ByteArrayOutputStream baos= new ByteArrayOutputStream();

        //Compresión del bitmap
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        //Arreglo de byte para la base 64
        byte[] imgBytes= baos.toByteArray();

        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }


    private void initComponents(){
        spHoraAp= (Spinner) getView().findViewById(R.id.spHoraAper);
        spMinAp= (Spinner) getView().findViewById(R.id.spMinAper);
        spHoraCi= (Spinner) getView().findViewById(R.id.spHoraCierre);
        spMinCi= (Spinner) getView().findViewById(R.id.spMinCierre);
        spGiro= (Spinner) getView().findViewById(R.id.spReglocalGiro);

        etNombre= (EditText) getView().findViewById(R.id.etReglocalNombre);
        etDireccion= (EditText) getView().findViewById(R.id.etReglocalDireccion);

        btnRegistrar= (Button) getView().findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(this);
        btnImagen= (Button) getView().findViewById(R.id.btnCargarImagen);
        btnImagen.setOnClickListener(this);

        imgNegocio= (ImageView) getView().findViewById(R.id.dispImgLugar);
    }

    private void contenidoSpinner(Spinner spinner, @ArrayRes int arreglo, @LayoutRes int recurso){
        ArrayAdapter<CharSequence> adapter;

        /////COLOCAR ELEMENTOS EN EL SPINNER PARA LE TIPO DE USUARIO
        // Crear ArrayAdapter desde algún recurso
        adapter = ArrayAdapter.createFromResource(ctx, arreglo, recurso);
        // Especificación de modelo a mostrar en el spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //Fondo Cyan
        spinner.setPopupBackgroundDrawable(new ColorDrawable(Color.rgb(3, 196, 161)));

    }

}
