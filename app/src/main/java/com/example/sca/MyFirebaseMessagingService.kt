package com.example.sca

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

open class MyFirebaseMessagingService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        Log.d(TAG,"Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    fun sendRegistrationToServer(token: String){
        Log.d(TAG,"sendRegistrationToServer($token)")
    }
}