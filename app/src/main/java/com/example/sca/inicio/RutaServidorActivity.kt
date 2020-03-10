package com.example.sca.inicio

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.sca.R
import com.example.sca.aplicacion.setting
import com.example.sca.constantes.Constantes
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_ruta_servidor.*
import kotlinx.android.synthetic.main.app_bar_menu.toolbar

class RutaServidorActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruta_servidor)

        val actionBar = supportActionBar
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeButtonEnabled(true)
        }

        editTextRutaServidor.setText(setting.rutaServidor)
        buttonGuardarRuta.setOnClickListener {
            setting.rutaServidor = editTextRutaServidor.text.toString()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            Constantes.BACK_ARROW -> onBackPressed()
        }
        return true
    }
}