package com.example.sca.aplicacion

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.sca.R
import com.example.sca.configuracion.MySharedPreference

val setting:MySharedPreference by lazy {
    Aplicacion.configuraciones!!
}

class Aplicacion : Application(){
    companion object{
        var configuraciones:MySharedPreference?=null
    }

    override fun onCreate() {
        super.onCreate()
        configuraciones=MySharedPreference(applicationContext)
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "sca"
            val descriptionText = "sca"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(getString(R.string.default_notification_channel_id),name,importance).apply {
                description = descriptionText
            }
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(0,300,200,300)
            channel.enableLights(true)
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}