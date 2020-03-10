package com.example.sca.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.sca.Menu
import com.example.sca.MyFirebaseMessagingService
import com.example.sca.R
import com.example.sca.aplicacion.setting
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseListener: FirebaseMessagingService() {
    override fun onNewToken(ntoken: String) {
        super.onNewToken(ntoken)
        setting.token = ntoken
        Log.e("NEW TOKEN", setting.token)
    }

    override fun onMessageReceived(mensaje: RemoteMessage) {
        super.onMessageReceived(mensaje)
        recibirNotificacion(
            mensaje.notification!!.title.toString(),
            mensaje.notification!!.body.toString()
        )
    }
    fun recibirNotificacion(titulo:String,mensaje:String){
        var intent: Intent? = null
        var idNotification = 0

        intent = Intent(this,Menu::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT)
        val sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this,getString(R.string.default_notification_channel_id))
            .setChannelId(getString(R.string.default_notification_channel_id))
            .setContentIntent(pendingIntent)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setSound(sonido)
            .setColor(ContextCompat.getColor(this,R.color.colorPrimary))
            .setVibrate(longArrayOf(0,300,200,300))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setSmallIcon(R.drawable.ic_menu_share,5)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(idNotification,notificationBuilder.build())
    }
}