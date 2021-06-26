package com.learnbible.activities.core

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.learnbible.R
import com.learnbible.adapter.PalabraBotonAdapter
import com.learnbible.model.BotonPalabraLearn
import com.learnbible.superactivities.SuperVersiculoActivity
import com.learnbible.utilities.CONSTANTES
import java.util.*

class VersiculoLearnPasajeVersiculoActivity : SuperVersiculoActivity() {
    var tvPasajeLearn: TextView? = null
    private var pasajeSeleccionado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_versiculo_learn_pasaje)
        super.obtenerParametros()
        controles()
        if (progressLearn > 0) {
            controlesDinamicos()
        } else {
            progressLearn += CONSTANTES.PUNTOS_OK
        }
    }

    //habilita y crea controles visuales
    fun controles() {
        tvPasajeLearn = findViewById(R.id.tvPasajeLearn)
        val tvVersiculoLearn = findViewById<TextView>(R.id.tvVersiculoLearn)
        val pbLearn = findViewById<ProgressBar>(R.id.pbLearn)
        btComprobarLearn = findViewById(R.id.btComprobarLearn)
        pbLearn.progress = progressLearn
        pbLearn.max = progressMaxLearn
        cambiarColor(this@VersiculoLearnPasajeVersiculoActivity, pbLearn)
        tvPasajeLearn!!.setText(CONSTANTES.reemplazaGuion(versiculo!!.pasaje))
        tvVersiculoLearn.text = versiculo!!.versiculo

        CONSTANTES.disableButton(btComprobarLearn!!, resources)
        btComprobarLearn!!.setOnClickListener {
            if (dataButton != null && dataButton!!.size > 0) {
                var todoOK = true
                todoOK = if (versiculo!!.pasaje == pasajeSeleccionado) {
                    true
                } else {
                    false
                }
                progressLearn += if (todoOK) {
                    sonidoOk()
                    CONSTANTES.PUNTOS_OK
                } else {
                    sonidoNoOk()
                    CONSTANTES.PUNTOS_NOOK
                }
            }
            mostrarActivity(this@VersiculoLearnPasajeVersiculoActivity)
        }
    }

    //habilita y crea controles visuales
    fun controlesDinamicos() {
        val maxText = CONSTANTES.PALABRAS_HIDDEN_MAX
        dataButton = ArrayList()
        val listVersiculosAll = CONSTANTES.allVersiculos()

        //identifica palabras aleatorias para los botones
        for (pos in 0 until maxText) {
            val posAleatorio = CONSTANTES.aleatorioRango(0, listVersiculosAll.size - 1)
            dataButton!!.add(BotonPalabraLearn(pos, listVersiculosAll[posAleatorio].pasaje, false))
        }

        //agrega pasaje real a botones
        val posnuevo = CONSTANTES.aleatorioRango(0, dataButton!!.size - 1)
        val bpltrue = BotonPalabraLearn(posnuevo,
                versiculo!!.pasaje, true)
        dataButton!!.add(posnuevo, bpltrue)


        //crea el adapter para los botones
        val adapter = PalabraBotonAdapter(this@VersiculoLearnPasajeVersiculoActivity, dataButton!!)
        val rvBotonPalabraLearn = findViewById<RecyclerView>(R.id.rvBotonPalabraLearn)
        rvBotonPalabraLearn.adapter = adapter
        val layoutManager = FlexboxLayoutManager(this@VersiculoLearnPasajeVersiculoActivity)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        rvBotonPalabraLearn.layoutManager = layoutManager
    }

    fun validarTexto(btn: Button) {
        pasajeSeleccionado = btn.text.toString()
        tvPasajeLearn!!.text = pasajeSeleccionado
        CONSTANTES.enableButton(btComprobarLearn!!,resources)
    }
}