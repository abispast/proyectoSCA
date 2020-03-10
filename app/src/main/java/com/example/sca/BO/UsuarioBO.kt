package com.example.sca.BO

import java.io.Serializable

class UsuarioBO(
    val correo: String,
    val contraseña: String = "",
    var id: Int = 0,
    var nombre: String = "",
    var rol: Int = 0) : Serializable