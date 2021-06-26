package com.learnbible.superactivities

import android.media.MediaPlayer
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.learnbible.R
import com.learnbible.firebase.dto.VersiculosAprendidoFS
import com.learnbible.utilities.CONSTANTES
import com.learnbible.utilities.CONSTANTES.aleatorioRango

open class SuperRecordarActivity : GeneralActivity() {

    // Access a Cloud Firestore instance from your Activity
    var dbFS = FirebaseFirestore.getInstance()
    var versiculoAprendidoFs: VersiculosAprendidoFS? = null
    var posicion: Int = 0
    var isCorrect: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun posicionAleatoriaPalabras(size:Int): Int {
        val desde = 0
        val hasta = size - 1
        return aleatorioRango(desde, hasta)
    }

    fun obtenerParametros() {

        posicion = intent.getSerializableExtra("pos") as Int
        versiculoAprendidoFs = CONSTANTES.listVersiculosAprendidosApp.get(posicion)
    }

    fun sonidoNoOk() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.nook)
        mediaPlayer.start()
    }

    fun sonidoOk() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.siok)
        mediaPlayer.start()
    }


}