package com.example.sca.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.sca.BO.UsuarioBO
import com.example.sca.Menu

import com.example.sca.R
import com.example.sca.aplicacion.setting
import com.example.sca.constantes.Constantes
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import com.google.gson.Gson


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var context:Context

    private var tagWSLogin = "webServiceLogin"
    private var mRequestQueue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        context=this
        mRequestQueue = Volley.newRequestQueue(context)

        /*val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)*/

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                        username.text.toString(),
                        password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                                username.text.toString(),
                                password.text.toString(),
                                context
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                inicioSesion(username.text.toString(),password.text.toString())
                loginViewModel.login(username.text.toString(), password.text.toString(),context)
            }
        }
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
            //showProgress(false)
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

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
                applicationContext,
                "$welcome $displayName",
                Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
