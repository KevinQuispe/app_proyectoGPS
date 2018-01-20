package com.example.jesus.bdremota;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.jesus.bdremota.Fragmentos.BienvenidoFrag;
import com.example.jesus.bdremota.Fragmentos.ConsultarListaUsuarioImagenFragment;
import com.example.jesus.bdremota.Fragmentos.ConsultarUsuario2;
import com.example.jesus.bdremota.Fragmentos.ConsultarUsuarioFrag;
import com.example.jesus.bdremota.Fragmentos.DesarrolladorFrag;
import com.example.jesus.bdremota.Fragmentos.LugaresFavoritos;
import com.example.jesus.bdremota.Fragmentos.RegistrarUsuarioFrag;
import com.example.jesus.bdremota.Interfaces.iFragments;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, iFragments {
    //botton gps
    Button ubicame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ubicame = (Button) findViewById(R.id.btnubicame);
        // ubicame.setOnClickListener(new View.OnClickListener() {

        //   @Override
        // public void onClick(View view) {
        //BienvenidoFrag b=new BienvenidoFrag();
        // FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        //transaction.replace(R.id.contenedorFragment,b);
        //transaction.commit();
        //}
        //});
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment, new RegistrarUsuarioFrag()).commit();
            return true;
        }

        if (id == R.id.action_salir) {

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Inicio) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment, new BienvenidoFrag()).commit();
        } else if (id == R.id.nav_Listfavoritos) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment, new LugaresFavoritos()).commit();
        } else if (id == R.id.nav_Cons_User) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment, new ConsultarUsuarioFrag()).commit();
        } else if (id == R.id.nav_ListUserImage) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment, new ConsultarListaUsuarioImagenFragment()).commit();
        } else if (id == R.id.nav_Desarrollador) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment, new DesarrolladorFrag()).commit();
        } else if (id == R.id.nav_Cons_User2) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFragment, new ConsultarUsuario2()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}