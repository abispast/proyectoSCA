package com.example.sca.inicio

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sca.Menu
import com.example.sca.R
import com.example.sca.aplicacion.setting
import com.example.sca.ui.login.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import android.content.ContentValues.TAG
import com.google.firebase.messaging.FirebaseMessaging

class LauncherActivity :AppCompatActivity(){
    
    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(setting.usuario.isEmpty()){
            startActivity(Intent(this,LoginActivity2::class.java))
        }else{
            startActivity(Intent(this,Menu::class.java))
        }
        finish()
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    var w = Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                //val msg = getString(R.string.msg_token_fmt, token)
                //Log.d(TAG, msg)
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
    }
    
}