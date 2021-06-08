package com.example.banlieueservice.herramientas;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Toast;

import com.example.banlieueservice.R;

public class Mensaje extends View{
    private Context ctx;

    public Mensaje(Context ctx){
        super(ctx);
        this.ctx=ctx;
    }

    public void mostrarDialog(String mje, String deQuien, AppCompatActivity activity){
        AlertDialog dialog;
        AlertDialog.Builder builder= new AlertDialog.Builder(activity);
        builder.setMessage(mje).setTitle(deQuien);
        /*builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });*/
        dialog= builder.create();
        //Fondo banAzulSec pero en decimal
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.rgb(17, 29, 94)));
        dialog.show();
    }

    public void mostrarToast(String mje, char opc){
        //opc=c->corto, opc=l->largo
        if(opc=='l')
            Toast.makeText(ctx, mje, Toast.LENGTH_LONG).show();
        else if (opc=='c')
            Toast.makeText(ctx, mje, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(ctx, "Opción de Toast inválida", Toast.LENGTH_LONG).show();
    }
}
