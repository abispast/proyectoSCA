package com.example.sca

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.sca.BO.UsuarioBO
import com.example.sca.aplicacion.setting
import com.example.sca.configuracion.PermissionsUtils
import com.example.sca.constantes.Constantes
import com.example.sca.inicio.LauncherActivity
import com.example.sca.inicio.LoginActivity2
import com.example.sca.ui.login.LoginActivity
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.nav_header_menu.*

class Menu : AppCompatActivity()/*, NavigationView.OnNavigationItemSelectedListener*/ {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var manejador: LocationManager? = null
    private var location: Location? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback?= null
    private lateinit var usuarioBO: UsuarioBO
    private var mRequestQueue: RequestQueue? = null //Hace peticion y conexion
    private val tagWSLogin = "tagWSLogin"
    var Tiempo_enviar: Long = 30000

    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE=1
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode){
            LOCATION_PERMISSION_REQUEST_CODE->{
                if (PermissionsUtils.isPermissionGranted(permissions,grantResults, Manifest.permission.ACCESS_FINE_LOCATION))
                    else{
                    mostrarSnackBar()
                }
            }
        }
    }

    private fun mostrarSnackBar(){
        Snackbar.make(drawer_layout,"Habilitar permisos para continuar",Snackbar.LENGTH_INDEFINITE)
            .setAction("Configuraciones",({
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }))
    }

    private fun enviarUbicacion(correo:String,id:Int,lat:String,long:String){
        val url = "${setting.rutaServidor}usuarios/ubicacion/$correo/$id/$lat/$long"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,url,null, Response.Listener { response ->
            Log.e("Location","Coordenadas = $location")
        }, Response.ErrorListener { error ->
            when(error){
                is ServerError -> Toast.makeText(applicationContext,R.string.servererror,Toast.LENGTH_SHORT).show()
                is NoConnectionError -> Toast.makeText(applicationContext,R.string.noconnectionerror,Toast.LENGTH_SHORT).show()
            }
        })
        jsonObjectRequest.tag= tagWSLogin //Manejo de peticiones o cancelar peticiones. Mata el proceso
        mRequestQueue!!.add(jsonObjectRequest).retryPolicy = DefaultRetryPolicy(Constantes.TIME_REQUEST,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    private fun habilitarPermisos(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            PermissionsUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,true,"Permiso necesario para usar su ubicacion")
        }else{
            manejador = getSystemService(LOCATION_SERVICE) as LocationManager
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            locationRequest = LocationRequest.create()
            locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest!!.interval = Tiempo_enviar
            locationRequest!!.fastestInterval = 10000

            locationCallback = object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult?) {
                    if(locationResult == null)
                        return
                    location = locationResult.lastLocation
                    Log.e("usuario","=$usuarioBO")
                    Log.e("location","=$location")
                    enviarUbicacion(usuarioBO.correo,usuarioBO.id,location?.latitude.toString(),location?.longitude.toString())
                    textView_nombre!!.text = usuarioBO.nombre
                    textView_correo!!.text = usuarioBO.correo
                }
            }
            mFusedLocationClient!!.requestLocationUpdates(locationRequest,locationCallback, null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("NEW TOKEN", setting.token)
        usuarioBO = Gson().fromJson(setting.usuario,UsuarioBO::class.java)
        Log.e("usuario","u=$usuarioBO")
        setContentView(R.layout.activity_menu)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = this.findViewById(R.id.nav_view)
        //navView.setNavigationItemSelectedListener(this)
        val menu:Menu = navView.menu
        val salir = menu.findItem(R.id.nav_sesion)
        salir.setOnMenuItemClickListener {
            setting.removerSesion()
            if(mFusedLocationClient==null){
                mFusedLocationClient!!.removeLocationUpdates(LocationCallback())
            }
            //mFusedLocationClient!!.removeLocationUpdates(LocationCallback())
            startActivity(Intent(this, LoginActivity2::class.java))
            finish()
            return@setOnMenuItemClickListener true

        }
        //Insertar Imagen en barra principal
        /*Picasso.get().load("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a4/UAT2.png/800px-UAT2.png")
            .into(navView.getHeaderView(0).findViewById<ImageView>(R.id.imageView))*/

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_ajustes, R.id.nav_sesion, R.id.nav_ajustes
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        mRequestQueue = Volley.newRequestQueue(this)
    }

    /*override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.nav_sesion->{
                setting.removerSesion()
                Log.e("Clic","Ya clickqueo")
            }
        }
        return super.onOptionsItemSelected(item)
    }*/
    /*override fun onNavigationItemSelected(item:MenuItem):Boolean{
        when (item.itemId) {
            R.id.nav_sesion -> {
                setting.removerSesion()
                Log.e("Clic","Ya clickqueo")
            }
        }
        return true
    }*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        if(mFusedLocationClient==null){
            habilitarPermisos()
        }
        else{
            mFusedLocationClient!!.removeLocationUpdates(LocationCallback())
            habilitarPermisos()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mFusedLocationClient!=null){
            mFusedLocationClient!!.removeLocationUpdates(LocationCallback())
        }
    }
}
