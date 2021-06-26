package com.learnbible.activities

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.learnbible.R
import com.learnbible.adapter.AmigosAxKmAdapter
import com.learnbible.firebase.dto.UsuarioFS
import com.learnbible.firebase.dto.UsuarioFriendsFS
import com.learnbible.superactivities.GeneralActivity
import com.learnbible.utilities.CONSTANTES


class BuscaTuAmigoActivity : GeneralActivity(){


    var dbFS = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_item_buscaramigo)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) //show back button

        dbFS.collection("usuarios")
                .orderBy("xp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        CONSTANTES.listFriendsA50 = ArrayList()

                        for (document in task.result!!) {
                            val usuarioFS = document.toObject(UsuarioFS::class.java)
                            usuarioFS.uid = document.id

                            searchFriends(usuarioFS)
                        }

                        val axKmAdapter = AmigosAxKmAdapter(this, CONSTANTES.listFriendsA50)
                        val lv = findViewById<ListView>(R.id.lv_amigosAxkm)
                        lv.adapter = axKmAdapter

                    } else {
                        crash.crash()
                        Log.w(CONSTANTES.TAG, "Error read document ", task.exception)
                    }
                }
    }

    //bisqueda de amigos
    fun searchFriends(usuarioFS:UsuarioFS){
        var usuarioFriendsFS = CONSTANTES.toUserToFriend(usuarioFS, false, false)
        //valida que el mismo usuario no sea yo
        if(!usuarioFriendsFS.uid.equals(CONSTANTES.USER!!.uid)) {


            for (amigo in CONSTANTES.listAmigos) {
                if (usuarioFriendsFS.uid.equals(amigo.uid)) {
                    usuarioFriendsFS = amigo
                    break
                }
            }

            //lo descarta si ya la solicitud se envio
            addFriends(usuarioFS, usuarioFriendsFS)
        }
    }

    fun addFriends(usuarioFS:UsuarioFS, usuarioFriendsFS: UsuarioFriendsFS){
        //lo descarta si ya la solicitud se envio
        if(!usuarioFriendsFS.isAmigo!!) {
            val geo = usuarioFS.localizacion
            val distance = CONSTANTES.cercaKm(geo)
            if (distance.toInt() <= 50) {
                CONSTANTES.listFriendsA50.add(usuarioFriendsFS)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}