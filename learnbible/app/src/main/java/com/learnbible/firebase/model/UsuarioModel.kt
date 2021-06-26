package com.learnbible.firebase.model

import android.content.Context
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.learnbible.MainVersiculoActivity
import com.learnbible.firebase.dto.DispositivosFS
import com.learnbible.utilities.CONSTANTES

class UsuarioModel {

    var dbFS = FirebaseFirestore.getInstance()
    val crash = Crashlytics.getInstance()
    val pathUsuarios = "/usuarios/"

    fun mergeUsuario(){
        val documentUserId = pathUsuarios+CONSTANTES.USER?.uid
        dbFS.document(documentUserId)
                .set(CONSTANTES.USERFS!!)
                .addOnSuccessListener {
                    Log.d(CONSTANTES.TAG, "Crea Usuario successfully written!")
                }
                .addOnFailureListener { e ->
                    crash.crash()
                    Log.w(CONSTANTES.TAG, "Error writing document", e)
                }
    }

    fun mergeDeviceUsers(dispositivosFS: DispositivosFS){
        dbFS.document(pathUsuarios + CONSTANTES.USER?.uid+"/dispositivos/"+dispositivosFS.token)
                .set(dispositivosFS)
                .addOnSuccessListener {
                    Log.d(CONSTANTES.TAG, "Crea Device successfully written!")
                }.addOnFailureListener { e ->
                    crash.crash()
                    Log.w(CONSTANTES.TAG, "Error writing document", e)
                }
    }


    fun delDeviceUsers(dispositivosFS: DispositivosFS){
        dbFS.document(pathUsuarios + CONSTANTES.USER?.uid+"/dispositivos/"+dispositivosFS.token)
                .delete()
                .addOnSuccessListener {
                    Log.d(CONSTANTES.TAG, "delete Device successfully written!")
                }.addOnFailureListener { e ->
                    crash.crash()
                    Log.w(CONSTANTES.TAG, "Error delete document", e)
                }
    }
}
