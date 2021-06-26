package com.learnbible

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permisos {

    val RECORD_REQUEST_CODE = 101




    fun enablePermisoLocation(activity: Activity) {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage("Permiso de la localización es necesario para que la aplicación busque amigos.")
                    .setTitle("Permisos Necesarios")
            builder.setPositiveButton("OK"
            ) { dialog, id ->
                setupPermisoLocation(activity)
            }

            val dialog = builder.create()
            dialog.show()
        }else{
            setupPermisoLocation(activity)
        }
    }

    fun enablePermisoAudio(activity: Activity) {
        val permission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage("Permiso del microfono es necesario para que la aplicación grabe el audio.")
                            .setTitle("Permisos Necesarios")
                            builder.setPositiveButton("OK"
                            ) { dialog, id ->
                                setupPermisoAudio(activity)
                    }

            val dialog = builder.create()
            dialog.show()
        }else{
            setupPermisoAudio(activity)
        }
    }


    private fun setupPermisoAudio(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO),RECORD_REQUEST_CODE)
    }

    private fun setupPermisoLocation(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION),RECORD_REQUEST_CODE)
    }


    fun enablePermisoRead(c: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val requiredPermission = android.Manifest.permission.READ_EXTERNAL_STORAGE

            // If the user previously denied this permission then show a message explaining why
            // this permission is needed
            if ((c as Activity).checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_DENIED) {
                (c as Activity).requestPermissions(arrayOf(requiredPermission), 101)
            }
        }
    }
}