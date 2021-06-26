package com.learnbible.activities.core

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.learnbible.R
import com.learnbible.R.drawable
import com.learnbible.model.Versiculo
import com.learnbible.superactivities.SuperVersiculoActivity
import com.learnbible.utilities.CONSTANTES
import java.util.*

class VersiculoLearnFinalVersiculoActivity : SuperVersiculoActivity() {
    val TAG :String = "VersiculoLearnFinal"
    var aumentaXP: Int = 0
    var aumentaCoronas: Int = 0
    var estrellas: Int = 0
    val documentUserId = "/usuarios/"+CONSTANTES.USER?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_versiculo_learn_final)
        super.obtenerParametros()
        sonidoLevelOk()

        calcularXP()
        if(aumentaXP == 0){
            aumentaXP = 1
        }
        
        //agrega coronas y estrellas
        if(versiculo!!.rondasMax == levelLearn){
            estrellas = 3
            aumentaCoronas = 3
        }

        controles(versiculo!!)


    }

    fun calcularXP(){
        val rondasMax = versiculo!!.rondasMax
        val xpTotal = versiculo!!.versiculo.split(" ").size.div(rondasMax)

        if(rondasMax == 5){
            calculaLevel1(xpTotal,4)
            calculaLevel2(xpTotal,2)
            calculaLevel3(xpTotal,1)
            if(levelLearn == 4){
                aumentaXP = (xpTotal*1.5).toInt()
            }else if(levelLearn == 5){
                aumentaXP = (xpTotal*1.75).toInt()
            }
        }else if(rondasMax == 4){
            calculaLevel1(xpTotal,4)
            calculaLevel2(xpTotal,2)
            if(levelLearn == 3){
                aumentaXP = (xpTotal*1.5).toInt()
            }else if(levelLearn == 4){
                aumentaXP = (xpTotal*1.75).toInt()
            }
        }else if(rondasMax == 3){
            calculaLevel1(xpTotal,2)
            calculaLevel2(xpTotal,1)
            if(levelLearn == 3){
                aumentaXP = (xpTotal*1.5).toInt()
            }
        }else if(rondasMax == 2){
            calculaLevel1(xpTotal,2)
            if(levelLearn == 2){
                aumentaXP = (xpTotal*1.5).toInt()
            }
        }else if(rondasMax == 1){
            calculaLevel1(xpTotal,1)
        }
    }

    fun calculaLevel1(xpTotal: Int, divi: Int){
        if(levelLearn == 1){
            aumentaXP = xpTotal/divi
        }
    }

    fun calculaLevel2(xpTotal: Int, divi: Int){
        if(levelLearn == 2){
            aumentaXP = xpTotal/divi
        }
    }

    fun calculaLevel3(xpTotal: Int, divi: Int){
        if(levelLearn == 3){
            aumentaXP = xpTotal/divi
        }
    }

    fun controles(versiculo: Versiculo) {
        val tvPasajeLearnFinish = findViewById<TextView>(R.id.tvPasajeLearnFinish)
        val ivLearnOk = findViewById<ImageView>(R.id.ivLearnOk)
        val tvXPLearnFinish = findViewById<TextView>(R.id.tvXPLearnFinish)
        val tvCoronasLearnFinish = findViewById<TextView>(R.id.tvCoronasLearnFinish)
        val btLearnFin = findViewById<Button>(R.id.btLearnFin)
        val animSequential = AnimationUtils.loadAnimation(applicationContext,
                R.anim.sequential)
        ivLearnOk.startAnimation(animSequential)
        tvPasajeLearnFinish.text = versiculo.pasaje
        ivLearnOk.setImageDrawable(resources.getDrawable(drawable.icon_ok))

        tvXPLearnFinish.text = "+"+aumentaXP+" experiencia"
        if(aumentaCoronas>0) {
            tvCoronasLearnFinish.visibility = View.VISIBLE
            tvCoronasLearnFinish.text = "+3 coronas"
        }


        CONSTANTES.enableButton(btLearnFin, resources)
        btLearnFin.setOnClickListener {

            //cambia Valores de Avance Historia y Versiculos
            CONSTANTES.cambiaAvanceHistoriaVersiculos(versiculo, aumentaCoronas)
            //guarda el versiculo en el usuario
            saveVerseUser()
            //guarda xp al usuario
            saveVerseXPUser()
            saveXpUser()

            finish()
        }
    }

    fun saveVerseUser(){

        val versiculoMap = hashMapOf(
                "id" to versiculo!!.id,
                "pasaje" to versiculo!!.pasaje,
                "versiculo" to versiculo!!.versiculo,
                "nivel" to levelLearn,
                "estrellas" to estrellas
        )

        val document = documentUserId+"/"+versiculo!!.id

        dbFS.document(document)
                .set(versiculoMap)
                .addOnSuccessListener { Log.d(TAG, "saveVerseUser successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing saveVerseUser", e) }

    }

    fun saveVerseXPUser(){
        val versiculoMap = hashMapOf(
                "id" to versiculo!!.id,
                "pasaje" to versiculo!!.pasaje,
                "versiculo" to versiculo!!.versiculo,
                "nivel" to levelLearn,
                "xp" to aumentaXP
        )

        val document = documentUserId+"/versiculosxp/"+ Calendar.getInstance().timeInMillis

        dbFS.document(document)
                .set(versiculoMap)
                .addOnSuccessListener { Log.d(TAG, "versiculoMap successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing versiculoMap", e) }

    }


    fun saveVerseCoronasUser(){
        val versiculoMap = hashMapOf(
                "id" to versiculo!!.id,
                "pasaje" to versiculo!!.pasaje,
                "versiculo" to versiculo!!.versiculo,
                "nivel" to levelLearn,
                "coin" to aumentaCoronas
        )

        val document = documentUserId+"/versiculoscoin/"+Calendar.getInstance().timeInMillis

        dbFS.document(document)
                .set(versiculoMap)
                .addOnSuccessListener { Log.d(TAG, "versiculoMap successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing versiculoMap", e) }

    }


    fun saveXpUser(){

        if(aumentaCoronas>0){
            saveVerseCoronasUser()
        }

        val userRef = dbFS.document(documentUserId)

        CONSTANTES.USERFS!!.xp = CONSTANTES.USERFS!!.xp + aumentaXP.toLong()
        CONSTANTES.USERFS!!.coin = CONSTANTES.USERFS!!.coin + aumentaCoronas.toLong()
        CONSTANTES.USERFS!!.lastVersiculo = versiculo!!.id

        // Atomically incrememnt the population of the city by 50.
        userRef.set(CONSTANTES.USERFS!!)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener {
                    e ->
                    crash.crash()
                    Log.w(TAG, "Error writing document", e)
                }

    }
}