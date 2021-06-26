package com.learnbible.activities.core

import android.os.Bundle
import android.widget.Button
import com.learnbible.R
import com.learnbible.adapter.PalabraTextoTextoColorAdapter
import com.learnbible.model.Palabra
import com.learnbible.utilities.CONSTANTES
import com.learnbible.superactivities.SuperVersiculoActivity

class VersiculoLearnTextVersiculoActivity : SuperVersiculoActivity() {
    private var palabraTextoTextoColorAdapter: PalabraTextoTextoColorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_versiculo_learn_text)

        obtenerParametros()
        controles()
        agregarVersiculoUI()
        if (progressLearn > 0) {
            super.transformarTexto()
            palabraTextoTextoColorAdapter!!.notifyDataSetChanged()
            super.controlesDinamicos(this)
        } else {
            progressLearn += CONSTANTES.PUNTOS_OK
        }
    }

    //habilita y crea controles visuales
    fun controles() {
        super.controles(this)
        palabraTextoTextoColorAdapter = PalabraTextoTextoColorAdapter(this@VersiculoLearnTextVersiculoActivity, listPalabrasTransformadas!!)
        rvVersiculoLearn!!.adapter = palabraTextoTextoColorAdapter
    }

    private fun agregarVersiculoUI() {
        arrPalabrasOriginales = versiculo!!.versiculo.split(" ").toTypedArray()
        var pos = 0
        for (spalabra in arrPalabrasOriginales!!) {
            listPalabrasTransformadas!!.add(Palabra(spalabra, true, pos))
            pos++
        }
        palabraTextoTextoColorAdapter!!.notifyDataSetChanged()
    }

    fun validarTexto(btn: Button){
        super.validarTextoSuper(btn)
        palabraTextoTextoColorAdapter!!.notifyDataSetChanged()
    }
}