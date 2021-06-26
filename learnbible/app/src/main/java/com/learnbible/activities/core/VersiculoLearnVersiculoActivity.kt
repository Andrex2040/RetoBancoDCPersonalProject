package com.learnbible.activities.core

import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.learnbible.R
import com.learnbible.adapter.PalabraTextoBotonAdapter
import com.learnbible.model.Palabra
import com.learnbible.superactivities.SuperVersiculoActivity
import com.learnbible.utilities.CONSTANTES
import com.learnbible.utilities.CONSTANTES.reemplazaUnderline
import java.util.*


class VersiculoLearnVersiculoActivity : SuperVersiculoActivity(), TextToSpeech.OnInitListener {
    private var palabraTextoBotonAdapter: PalabraTextoBotonAdapter? = null


    private var tts: TextToSpeech? = null
    var isPlay = false
    var btRead:FloatingActionButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_versiculo_learn)

        obtenerParametros()
        controles()
        agregarVersiculoUI()

        tts = TextToSpeech(this, this)
        btRead = findViewById(R.id.btRead)

        btRead!!.setOnClickListener {
            if(tts!!.isSpeaking) {
                btRead!!.setImageDrawable(ResourcesCompat.getDrawable(resources, android.R.drawable.ic_media_play, null))
                tts!!.stop()
            }else{
                speakOut()
                btRead!!.setImageDrawable(ResourcesCompat.getDrawable(resources, android.R.drawable.picture_frame, null))
            }
        }

        if (progressLearn > 0) {
            super.transformarTexto()
            palabraTextoBotonAdapter!!.notifyDataSetChanged()
            super.controlesDinamicos(this)
        } else {

            CONSTANTES.enableButton(btComprobarLearn!!,resources)
            btComprobarLearn!!.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlanco, null))
            btComprobarLearn!!.backgroundTintList = ResourcesCompat.getColorStateList(resources, R.color.colorAzulRio, null)
            progressLearn += CONSTANTES.PUNTOS_OK
        }
    }

    override fun onInit(status: Int) {
        if(progressLearnOld == 0){
            enableInit(status)
        }else if(versiculo!!.nivelActual >=1) {
            btRead!!.visibility = View.GONE
        }else{
            enableInit(status)
        }
    }

    fun enableInit(status: Int){
        if (status == TextToSpeech.SUCCESS) {
            // set UK English as language for tts
            val result = tts!!.setLanguage(Locale.getDefault())

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                crash.crash()
                Log.e("TTS", "The Language specified is not supported!")
            }else{
                speakOut()
            }
        } else {
            crash.crash()
            Log.e("TTS", "Initialization Failed!")
        }
    }

    private fun speakOut() {
        isPlay = true
        var message = versiculo!!.versiculo
        if (message.isNullOrBlank()) message = "Please enter a message"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts!!.speak(message, TextToSpeech.QUEUE_FLUSH, null, "")
        } else {
            @Suppress("DEPRECATION")
            tts!!.speak(message, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    override fun onDestroy() {
        // Shut down TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }

        super.onDestroy()
    }

    override fun onBackPressed() {
        if(progressLearnOld ==  0)
            finish()
        else
            super.onBackPressed()
    }


    //habilita y crea controles visuales
    fun controles() {
        super.controles(this)
        palabraTextoBotonAdapter = PalabraTextoBotonAdapter(this@VersiculoLearnVersiculoActivity, listPalabrasTransformadas!!)
        rvVersiculoLearn!!.adapter = palabraTextoBotonAdapter
    }

    private fun agregarVersiculoUI() {
        arrPalabrasOriginales = versiculo!!.versiculo.split(" ").toTypedArray()
        var pos = 0
        for (spalabra in arrPalabrasOriginales!!) {
            listPalabrasTransformadas!!.add(Palabra(spalabra, true, pos))
            pos++
        }
        palabraTextoBotonAdapter!!.notifyDataSetChanged()
    }

    fun removeButon(btn: Button) {
        val pos = btn.tag as Int

        //eliminar de la pantalla
        //identifica la posicion de la palabra seleccionada
        for (palabraTransformada in listPalabrasTransformadas!!) {
            if (palabraTransformada.position == pos) {
                palabraTransformada.palabra = reemplazaUnderline(palabraTransformada.palabra)
                palabraTransformada.isText = true
                break
            }
        }
        palabraTextoBotonAdapter!!.notifyDataSetChanged()
    }

    fun validarTexto(btn: Button){
        super.validarTextoSuper(btn)
        palabraTextoBotonAdapter!!.notifyDataSetChanged()
    }
}