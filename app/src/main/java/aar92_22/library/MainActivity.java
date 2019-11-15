package aar92_22.library;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.TransactionTooLargeException;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import aar92_22.library.BaseDeDatos.Libro;
import aar92_22.library.BaseDeDatos.Master;
import aar92_22.library.BaseDeDatos.Session;
import aar92_22.library.Fragments.Agregar;
import aar92_22.library.Fragments.Editar;
import aar92_22.library.Fragments.Ver;
import aar92_22.library.Fragments.VerCompleto;
import aar92_22.library.Utilities.Holder;
import aar92_22.library.Utilities.Navigator;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Navigator, Agregar.BookConsumer, Ver.ChoosenBook  {


    //Variables base de datos
    private SQLiteDatabase db;
    private Master master;
    public Session session;


    public MenuItem botonEliminar, botonEditar;


    //Variable anuncios
    private AdView mAdView;

    private Libro libro;

    FloatingActionButton fab;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        //Cargar anuncios
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Conexión base de datos
        try {
            Master.DevOpenHelper openHelper = new Master.DevOpenHelper(this, "mimDb14", null);
            db = openHelper.getWritableDatabase();
            master = new Master(db);
            session = master.newSession();
        } catch (Exception e) {
            Log.d("Errores: ", e.getMessage());
        }



        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();

                manager.beginTransaction().replace(R.id.main,
                        Agregar.newInstance(null, null), "asd").commit();
                fab.setVisibility(View.INVISIBLE);
            }
        });



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        launchMenuFragment();
    }

    private void launchMenuFragment() {
        FragmentManager manager = getSupportFragmentManager();
        Holder holder = new Holder();
        holder.setBookList(session.getLibroDao().loadAll());
        manager.beginTransaction().replace(R.id.main, Ver.newInstance(holder, null)).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           // super.onBackPressed();
            navigate("sdfsd");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        botonEliminar = menu.findItem(R.id.accionEliminar);
        botonEditar = menu.findItem(R.id.accionEditar);
        botonEditar.setVisible(false);
        botonEliminar.setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.accionEliminar) {

            final CharSequence[] options = {"Delete", "Cancel"};


            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Delete")) {
                        try {
                            libro.delete();
                        }catch (Exception e){
                            builder.setMessage("Select a book");
                            builder.show();
                        }
                        navigate("vsdfsder");
                    }
                    else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();

        }

        if(id == R.id.accionEditar){
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.main,
                    Editar.newInstance(libro, null)).commit();
            fab.setVisibility(View.INVISIBLE);
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.agregar) {
            navigate("agregar");
        } else if (id == R.id.libreria) {
            navigate("dfsd");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void consumeBook(Libro libro) {
        session.getLibroDao().insert(libro);
    }

    public void sendBook(Libro libro) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.main,
                VerCompleto.newInstance(libro, null)).commit();
        VerCompleto.menuVisible(botonEliminar, botonEditar);
        this.libro = libro;
    }

    public void navigate(String name) {
        FragmentManager manager = getSupportFragmentManager();
        switch (name) {
            case "agregar":
                manager.beginTransaction().replace(R.id.main,
                        Agregar.newInstance(null, null), "asd").commit();
                fab.setVisibility(View.INVISIBLE);
                botonEditar.setVisible(false);
                botonEliminar.setVisible(false);
                break;

            default:
                Holder h = new Holder();
                h.setBookList(session.getLibroDao().loadAll());
                manager.beginTransaction().replace(R.id.main,
                        Ver.newInstance(h, null)).commit();
                fab.setVisibility(View.VISIBLE);
                botonEditar.setVisible(false);
                botonEliminar.setVisible(false);
                break;
        }


    }



}
