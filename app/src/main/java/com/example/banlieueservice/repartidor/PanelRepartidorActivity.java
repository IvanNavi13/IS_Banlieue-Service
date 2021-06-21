package com.example.banlieueservice.repartidor;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.banlieueservice.R;
import com.example.banlieueservice.actividades.HomeFragment;
import com.example.banlieueservice.actividades.MainActivity;
import com.example.banlieueservice.cliente.PanelClienteActivity;
import com.example.banlieueservice.herramientas.Mensaje;
import com.example.banlieueservice.herramientas.Utilidad;
import com.example.banlieueservice.interfaces.VolleyCallBack;
import com.example.banlieueservice.web.JSON;
import com.example.banlieueservice.web.ServicioWeb;
import com.google.android.material.navigation.NavigationView;

import java.util.Map;

public class PanelRepartidorActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AppCompatActivity panrepAct;
    private Mensaje mje;
    private DrawerLayout drawerLayout;
    private Map<String, String> datosRepartidor;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panelrepartidor);
        panrepAct= this;
        //initComponents();
        mje= new Mensaje(this);
        cargarInfoRepartidor();
        initComponents();
        cargarNavegador();


        //Por defecto ir a la actividad del mapa
        irAFragment(new HomeFragment(), getString(R.string.home));
    }

    //OPCIONES ÍCONO HAMBURGUESA
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int menuSelected= item.getItemId();

        switch(menuSelected){
            case R.id.itemRepHome:
                irAFragment(new PanRepInicio(datosRepartidor), getString(R.string.usInicio));
                break;

            case R.id.itemRepModifDatosPer:
                irAFragment(new PanRepModifDatosPer(datosRepartidor), getString(R.string.usModifDatosPer));
                break;

            case R.id.itemRepModifDatosAcc:
                irAFragment(new PanRepModifDatosAcc(datosRepartidor), getString(R.string.usModifDatosAcc));
                break;

            case R.id.itemRepModifDatosServicio:
                irAFragment(new PanRepModifDatosServ(datosRepartidor), getString(R.string.repModifDatosServ));
                break;

            case R.id.itemRepCerrSes:
                cerrarSesionRepartidor();
                break;

            case R.id.itemRepElimCta:
                irAFragment(new PanRepElimCta(datosRepartidor), getString(R.string.usElimCta));
                break;

            case R.id.itemRepAcerca:
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

    private void cargarInfoRepartidor() {
        JSON json = new JSON();
        json.agregarDato("tipoPersona", "rep"); //Enviar al servidor clave de indicación de Usuario (para saber qué procedure llamar)
        json.agregarDato("correo", getIntent().getStringExtra("correo"));
        ServicioWeb.obtenerInstancia(this).infoPersona(json.strJSON(), new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onJsonSuccess(String jsonResult) {
                datosRepartidor = json.obtenerDatos(jsonResult);
            }

            @Override
            public void onError(String result) {
                mje.mostrarDialog(result, "Banlieue Service", panrepAct);
            }
        });
    }

    private void cerrarSesionRepartidor(){
        JSON json = new JSON();
        json.agregarDato("tipoPersona", "rep"); //Enviar al servidor clave de indicación de Usuario (para saber qué procedure llamar)
        json.agregarDato("correo", getIntent().getStringExtra("correo"));
        ServicioWeb.obtenerInstancia(this).cerrarSesion(json.strJSON(), new VolleyCallBack() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onJsonSuccess(String jsonResult) {
                mje.mostrarToast(json.obtenerDatos(jsonResult).get("mjeCierre"), 'l');
                Intent intent= new Intent(PanelRepartidorActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(String result) {
                mje.mostrarDialog(result, "Banlieue Service", panrepAct);
            }
        });
    }


    private void cargarNavegador(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Editar id del drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerPanRep);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.abrirNav, R.string.cerrarNav);
        drawerLayout.addDrawerListener(toggle);
        drawerLayout.getLayoutParams();
        toggle.syncState();

        //navView.getMenu().getItem(0).setChecked(true);
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
        navView= (NavigationView) findViewById(R.id.navViewRep);
        navView.setNavigationItemSelectedListener(this);

    }
}
