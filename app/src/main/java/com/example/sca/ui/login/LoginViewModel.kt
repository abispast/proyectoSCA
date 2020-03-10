package com.example.sca.ui.login

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.example.sca.BO.UsuarioBO
import com.example.sca.Menu
import com.example.sca.data.LoginRepository
import com.example.sca.data.Result

import com.example.sca.R
import com.example.sca.aplicacion.setting
import com.example.sca.constantes.Constantes
import com.google.gson.Gson
import org.json.JSONObject

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String,context:Context) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)

        if (result is Result.Success) {
            //setting.usuario=username// + "#@" + password
            _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
            var intent=Intent(context, Menu::class.java)
            intent.putExtra("correo",username)
            Log.e("Usuario", setting.usuario)
            //context.startActivity(intent)
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }
}
