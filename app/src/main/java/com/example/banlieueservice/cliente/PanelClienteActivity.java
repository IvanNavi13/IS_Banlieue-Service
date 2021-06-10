package com.example.banlieueservice.cliente;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.banlieueservice.R;
import com.example.banlieueservice.actividades.MainActivity;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.herramientas.Utilidad;
import com.example.banlieueservice.interfaces.VolleyCallBack;
/*import com.example.banlieueservice.cliente.PanCliElimCta;
import com.example.banlieueservice.cliente.PanCliHome;
import com.example.banlieueservice.cliente.PanCliModifDatosAcc;
import com.example.banlieueservice.cliente.PanCliModifDatosPer;
import com.example.banlieueservice.repartidor.PanCliModifDatosNeg;*/
import com.example.banlieueservice.repartidor.PanelRepartidorActivity;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;
import com.google.android.material.navigation.NavigationView;

import java.util.Map;

public class PanelClienteActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AppCompatActivity pancliAct;
    private Mensaje mje;
    private DrawerLayout drawerLayout;
    private Map<String, String> datosCliente;
    private NavigationView navView;
    private String correo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panelcliente);
        pancliAct= this;
        //initComponents();
        mje= new Mensaje(this);
        cargarInfoCliente();
        initComponents();
        cargarNavegador();

        PanCliHome home= new PanCliHome();
        home.sendSingleData(getIntent().getStringExtra("correo"));
        irAFragment(home, getString(R.string.usInicio));
    }

    
    //OPCIONES ÍCONO HAMBURGUESA
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int menuSelected= item.getItemId();

        switch(menuSelected){
            case R.id.itemCliHome:
                PanCliHome home= new PanCliHome();
                home.sendSingleData(getIntent().getStringExtra("correo"));
                irAFragment(home, getString(R.string.usInicio));
                break;

            case R.id.itemCliModifDatosPer:
                irAFragment(new PanCliModifDatosPer(datosCliente), getString(R.string.usModifDatosPer));
                break;

            case R.id.itemCliModifDatosAcc:
                irAFragment(new PanCliModifDatosAcc(datosCliente), getString(R.string.usModifDatosAcc));
                break;

            case R.id.itemCliNvoNegocio:
                irAFragment(new PanCliNvoNegocio(datosCliente.get("idPartic")), getString(R.string.cliNvoNegocio));
                break;

            case R.id.itemCliElimNegocio:
                irAFragment(new PanCliElimNegocio(datosCliente), getString(R.string.cliElimNegocio));
                break;

            case R.id.itemCliCerrSes:
                Intent intent= new Intent(PanelClienteActivity.this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.itemCliElimCta:
                irAFragment(new PanCliElimCta(datosCliente), getString(R.string.usElimCta));
                break;

            case R.id.itemCliAcerca:
                mje.mostrarDialog(Utilidad.strAcercaDe, "Banlieue Service", this);
                break;
        }

        return true;
    }

    //Al pulsarse el botón back si el menú está desplegado, se oculta.
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    private void cargarInfoCliente(){
        JSON json = new JSON();
        json.agregarDato("tipoPersona", "cli"); //Enviar al servidor clave de indicación de Usuario (para saber qué procedure llamar)
        json.agregarDato("correo", getIntent().getStringExtra("correo"));
        ServicioWeb.obtenerInstancia(this).infoPersona(json.strJSON(), new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onJsonSuccess(String jsonResult) {
                datosCliente= json.obtenerDatos(jsonResult);
            }

            @Override
            public void onError(String result) {
                mje.mostrarDialog(result, "Banlieue Service", pancliAct);
            }
        });
    }

    private void cargarNavegador(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Editar id del drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerPanCli);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.abrirNav, R.string.cerrarNav);
        //toggle.getDrawerArrowDrawable().setColor(Color.rgb(255, 255, 255));
        drawerLayout.addDrawerListener(toggle);
        drawerLayout.getLayoutParams();
        toggle.syncState();

        navView.getMenu().getItem(0).setChecked(true);
    }

    private void irAFragment(Fragment fragment, String titulo){
        getSupportFragmentManager()
                .beginTransaction()
                //.setCustomAnimations(R.anim.nav_enter, R.anim.nav_exit)
                //.replace(R.id.home_content, fragment)
                .replace(R.id.home_content, fragment)
                .commit();

        setTitle(titulo);

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void initComponents(){
        navView= (NavigationView) findViewById(R.id.navViewCli);
        navView.setNavigationItemSelectedListener(this);

    }
}
