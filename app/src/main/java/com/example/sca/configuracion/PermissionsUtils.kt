package com.example.sca.configuracion

import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.GeolocationPermissions
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

object PermissionsUtils {
    fun requestPermission(activity: AppCompatActivity,request:Int,permission:String,finishActivity:Boolean,messagePermission:String){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
            RationaleDialog.newInstance(request,finishActivity,messagePermission,permission).show(activity.supportFragmentManager,"dialog")
        }else{
            ActivityCompat.requestPermissions(activity, arrayOf(permission),request)
        }
    }

    fun isPermissionGranted(grantPermissions: Array<String>,grantResults:IntArray,permission: String):Boolean{
        for(i in grantPermissions.indices){
            if(permission == grantPermissions[i]){
                return grantResults[i] == PackageManager.PERMISSION_GRANTED
            }
        }
        return false
    }

    class RationaleDialog: androidx.fragment.app.DialogFragment() {
        private var mFinishActivity = false

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            mFinishActivity = arguments!!.getBoolean(ARGUMENT_FINISH_ACTIVITY)
            val requestCode = arguments!!.getInt(ARGUMENT_PERMISSION_REQUEST_CODE)
            val dialogCreado = AlertDialog
                .Builder(activity)
                .setMessage(arguments!!.getString(ARGUMENT_MESSAGE_PERMISSION))
                .setPositiveButton(android.R.string.ok,({_, _ ->
                    ActivityCompat.requestPermissions(activity!!, arrayOf(permisoRequerido), requestCode)
                    mFinishActivity = false
                }))
                .setNegativeButton(android.R.string.cancel,({_, _ ->
                    ActivityCompat.requestPermissions(activity!!, arrayOf(permisoRequerido), requestCode)
                    mFinishActivity = false
                }))
                .create()
            dialogCreado.show()
            return dialogCreado
        }

        companion object {
            private const val ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode"
            private const val ARGUMENT_FINISH_ACTIVITY = "finish"
            private const val ARGUMENT_MESSAGE_PERMISSION = "messagePermission"
            private lateinit var permisoRequerido: String
            fun newInstance(requestCode: Int, finishActivity: Boolean, messagePermission: String, permiso: String): RationaleDialog {
                val arguments = Bundle()
                arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE,requestCode)
                arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY,finishActivity)
                arguments.putString(ARGUMENT_MESSAGE_PERMISSION, messagePermission)
                permisoRequerido = permiso
                val dialog = RationaleDialog()
                dialog.arguments = arguments
                return dialog
            }
        }
    }
}