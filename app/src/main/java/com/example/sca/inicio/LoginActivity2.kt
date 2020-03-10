package com.example.sca.inicio

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.sca.BO.UsuarioBO
import com.example.sca.Menu
import com.example.sca.R
import com.example.sca.aplicacion.setting
import com.example.sca.constantes.Constantes
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login2.*
import kotlinx.android.synthetic.main.activity_login2.password
import kotlinx.android.synthetic.main.nav_header_menu.*
import org.json.JSONObject

class LoginActivity2 : AppCompatActivity(){
    private val mRequestTag = "mRequestTag"
    private var tagWSLogin = "webServiceLogin"
    private var mRequestQueue: RequestQueue? = null
    private var contadorCambioRutaServidor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        mRequestQueue = Volley.newRequestQueue(this)

        email_sign_in_button.setOnClickListener { attemptLogin() }

        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        password.setOnEditorActionListener(TextView.OnEditorActionListener{_,actionId,_ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                inputManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        logoSCA.setOnClickListener {
            contadorCambioRutaServidor++
            if (contadorCambioRutaServidor >= 10){
                contadorCambioRutaServidor = 0
                startActivity(Intent(this,RutaServidorActivity::class.java))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mRequestQueue != null)
            mRequestQueue!!.cancelAll(mRequestTag)
    }

    private fun attemptLogin() {
        // Reset errors.
//        inputLayoutLoginCorreo.error = null
//        inputLayoutLoginContrasenia.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(passwordStr)) {
            password.error = getString(R.string.error_field_required)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            inicioSesion(emailStr,passwordStr)
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        login_form.visibility = if (show) View.GONE else View.VISIBLE
        login_form.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 0 else 1).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_form.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

        login_progress.visibility = if (show) View.VISIBLE else View.GONE
        login_progress.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 1 else 0).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_progress.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
    }

    private fun inicioSesion(correo:String, password:String){
        val url="${setting.rutaServidor}usuarios/inicioSesion/"
        val usuarioBO = UsuarioBO(correo,password)
        val jsonObject = JSONObject(Gson().toJson(usuarioBO))
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST,url,jsonObject, Response.Listener { response ->
            setting.usuario = response.toString()
            Log.e("Usuario", setting.usuario)
            startActivity(Intent(this, Menu::class.java))
            finish()
        }, Response.ErrorListener { error ->
            Log.e("error","error al conectarse al ws $url",error)
            showProgress(false)
            //loginViewModel.loginResult
            when(error){
                is ParseError -> Toast.makeText(this,"verifique datos", Toast.LENGTH_SHORT).show()
                is ServerError -> Toast.makeText(this,"Ocurrio un problema en el servidor", Toast.LENGTH_SHORT).show()
                is NoConnectionError -> Toast.makeText(this, "Sin conexion a internet", Toast.LENGTH_SHORT).show()
                is AuthFailureError -> Toast.makeText(this,"Error en la validacion del usuario", Toast.LENGTH_SHORT).show()
                is TimeoutError -> Toast.makeText(this, "Tiempo de respuesta excedido", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(this, "Error en el usuario", Toast.LENGTH_SHORT).show()
            }
        })
        jsonObjectRequest.tag = tagWSLogin
        mRequestQueue!!.add(jsonObjectRequest).retryPolicy = DefaultRetryPolicy(Constantes.TIME_REQUEST, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }
}