package com.example.banlieueservice.usuario;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.banlieueservice.R;
import com.example.banlieueservice.actividades.MainActivity;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.herramientas.Utilidad;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;
import com.google.android.material.navigation.NavigationView;

import java.util.Map;

public class PanelUsuarioActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AppCompatActivity panusAct;
    private Mensaje mje;
    private DrawerLayout drawerLayout;
    private Map<String, String> datosUsusario;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panelusuario);
        panusAct= this;
        //initComponents();
        mje= new Mensaje(this);
        cargarInfoUsuario();
        initComponents();
        cargarNavegador();

        //Por defecto ir a la actividad del mapa
        irAFragment(new PanUsHome(), getString(R.string.usInicio));
    }

    //OPCIONES ÍCONO HAMBURGUESA
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int menuSelected= item.getItemId();

        switch(menuSelected){
            case R.id.itemUsHome:
                irAFragment(new PanUsHome(), getString(R.string.usInicio));
                break;

            case R.id.itemUsModifDatosPer:
                irAFragment(new PanUsModifDatosPer(datosUsusario), getString(R.string.usModifDatosPer));
                break;

            case R.id.itemUsModifDatosAcc:
                irAFragment(new PanUsModifDatosAcc(datosUsusario), getString(R.string.usModifDatosAcc));
                break;

            case R.id.itemUsCerrSes:
                Intent intent= new Intent(PanelUsuarioActivity.this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.itemUsElimCta:
                irAFragment(new PanUsElimCta(datosUsusario), getString(R.string.usElimCta));
                break;

            case R.id.itemUsAcerca:
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

    private void cargarInfoUsuario(){
        JSON json = new JSON();
        json.agregarDato("tipoPersona", "usr"); //Enviar al servidor clave de indicación de Usuario (para saber qué procedure llamar)
        json.agregarDato("correo", getIntent().getStringExtra("correo"));
        ServicioWeb.obtenerInstancia(this).infoPersona(json.strJSON(), new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onJsonSuccess(String jsonResult) {
                datosUsusario= json.obtenerDatos(jsonResult);
            }

            @Override
            public void onError(String result) {
                mje.mostrarDialog(result, "Banlieue Service", panusAct);
            }
        });
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


    private void cargarNavegador(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Editar id del drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerPanUs);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.abrirNav, R.string.cerrarNav);
        toggle.getDrawerArrowDrawable().setColor(Color.BLACK);
        drawerLayout.addDrawerListener(toggle);
        drawerLayout.getLayoutParams();
        toggle.syncState();

        navView.getMenu().getItem(0).setChecked(true);
    }

    private void initComponents(){
        navView= (NavigationView) findViewById(R.id.navViewUs);
        navView.setNavigationItemSelectedListener(this);

    }
}
