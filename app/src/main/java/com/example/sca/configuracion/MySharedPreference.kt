package com.example.sca.configuracion

import android.content.Context
import android.content.SharedPreferences

class MySharedPreference(context:Context) {
    val settings:SharedPreferences=context.getSharedPreferences("MySharedPreference",Context.MODE_PRIVATE)
    //private val rutaServidorDefault = "http://192.168.1.74:8080/uat/webresources/"
    private val usuarioSesion = "usuarioSesion"
    private val rutaDelServidor = "rutaDelServidor"
    private val direccion = "http://192.168.1.74:8080/uat/webresources/"
    private val tokens = "token"

    var token:String
        get() = settings.getString(tokens,"")!!
        set(value) = settings.edit().putString(tokens,value).apply()

    var usuario:String
        get() = settings.getString(usuarioSesion,"")!!
        set(value)= settings.edit().putString(usuarioSesion,value).apply()

    var rutaServidor:String
        get() = settings.getString(rutaDelServidor,direccion)!!
        set(value)=settings.edit().putString(rutaDelServidor,value).apply()

    fun removerSesion(){
        settings.edit().remove("usuarioSesion").apply()
    }
    /*private val nameConfiguracion = "configuracionUsuario"
    private val preferencias:SharedPreferences = context.getSharedPreferences(nameConfiguracion,Context.MODE_PRIVATE)
    private val rutaDelServidor = "rutaDelServidor"
    private val direccion = "http://192.168.43.248:8080/uat/webresources/"
    private val cerrado = ""

    var sesionUsuarios:String
        get()=preferencias.getString("usuarioEnSesion","")!!
        set(value)=preferencias.edit().putString("usuarioEnSesion",value).apply()

    var rutaServidor:String
        get()=preferencias.getString(rutaDelServidor,direccion)!!
        set(value)=preferencias.edit().putString(rutaDelServidor,value).apply()

    fun cerrarSesion(){
        preferencias.edit().remove("usuarioEnSesion").apply()
    }*/
}