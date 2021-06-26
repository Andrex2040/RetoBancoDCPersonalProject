package com.learnbible.activities.core

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.learnbible.R
import com.learnbible.superactivities.SuperVersiculoActivity
import com.learnbible.utilities.CONSTANTES
import com.learnbible.utilities.CONSTANTES.aleatorioRango
import com.learnbible.utilities.CONSTANTES.reemplazaGuion
import com.learnbible.utilities.LevenshteinDistance.computeLevenshteinDistance
import java.util.*

class VersiculoLearnVozVersiculoActivity : SuperVersiculoActivity() {

    private var tvPasajeLearnVoz: TextView? = null
    private var tvVersiculoLearnVoz: TextView? = null
    private var tvResultadosVoz: TextView? = null
    private var btComprobarLearnVoz: Button? = null
    private var faRecordVoz: FloatingActionButton? = null
    private var tvRecordVoz: TextView? = null
    private var tvRecordVozVoz: TextView? = null
    private var pbLearn: ProgressBar? = null
    private var palabrasOriginales: Array<String>? = null
    private var listPalabras: MutableList<String>? = null
    private var intentosFallidos = 0
    private var controlVoz = true
    private var mSpeechRecognizer: SpeechRecognizer? = null
    private var mSpeechRecognizerIntent: Intent? = null
    private var activity:Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        setContentView(R.layout.activity_versiculo_learn_voz)
        super.obtenerParametros()
        controles()
    }

    fun controles() {
        tvPasajeLearnVoz = findViewById(R.id.tvPasajeLearnVoz)
        tvVersiculoLearnVoz = findViewById(R.id.tvVersiculoLearnVoz)
        tvResultadosVoz = findViewById(R.id.tvResultadosVoz)
        btComprobarLearnVoz = findViewById(R.id.btComprobarLearnVoz)
        faRecordVoz = findViewById(R.id.faRecordVoz)
        tvRecordVoz = findViewById(R.id.tvRecordVoz)
        tvRecordVozVoz = findViewById(R.id.tvRecordVozVoz)
        pbLearn = findViewById(R.id.pbLearn)
        pbLearn!!.progress = progressLearn
        pbLearn!!.max = progressMaxLearn
        cambiarColor(this@VersiculoLearnVozVersiculoActivity, pbLearn!!)
        tvPasajeLearnVoz!!.text = versiculo!!.pasaje
        tvVersiculoLearnVoz!!.text = versiculo!!.versiculo

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mSpeechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
        mSpeechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mSpeechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.packageName)

        val listener = SpeechRecognitionListener()
        mSpeechRecognizer!!.setRecognitionListener(listener)
        faRecordVoz!!.setOnClickListener{
            tvResultadosVoz!!.text = ""
            tvRecordVoz!!.text = "Escuchando..."
            CONSTANTES.disableButton(btComprobarLearnVoz!!, resources)

            faRecordVoz!!.setImageDrawable(ResourcesCompat.getDrawable(resources, android.R.drawable.picture_frame, null))
            mSpeechRecognizer!!.startListening(mSpeechRecognizerIntent)
        }
        btComprobarLearnVoz!!.setOnClickListener{ mostrarActivity(this@VersiculoLearnVozVersiculoActivity) }
        if (progressLearn > 0) {
            transformarTextoVoz(tvVersiculoLearnVoz)
        }
    }

    fun transformarTextoVoz(tvVersiculoLearn: TextView?) {
        palabrasOriginales = tvVersiculoLearn!!.text.toString().split(" ").toTypedArray()
        listPalabras = ArrayList()
        for (s in palabrasOriginales!!) {
            listPalabras!!.add(s)
        }
        reemplazarTextoVoz()
    }

    fun reemplazarTextoVoz() {
        //calcula largo de ____ para la palabra
        var y = aleatorioRango(1, progressLearn * levelLearn / 2)
        if (y > palabrasOriginales!!.size) {
            y = palabrasOriginales!!.size
        }
        var x = 0
        while (x <= y) {
            val pos = super.posicionAleatoriaPalabras(palabrasOriginales!!.size)
            //reemplazo de palabra aleatoria por ----
            listPalabras!![pos] = reemplazaGuion(palabrasOriginales!![pos])
            x++
        }

        //Reconstruye versiculo
        var versiculoReemplazado = listPalabras!![0]
        for (i in 1 until palabrasOriginales!!.size) {
            versiculoReemplazado += " " + listPalabras!![i]
        }
        tvVersiculoLearnVoz!!.text = versiculoReemplazado
    }

    protected inner class SpeechRecognitionListener : RecognitionListener {
        var TAG = "FIREBASE"
        override fun onReadyForSpeech(params: Bundle) {
            Log.d("FIREBASE", "OnReadyForSpeech") //$NON-NLS-1$
            controlVoz = true
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "onBeginingOfSpeech")
        }

        override fun onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech")
        }

        override fun onResults(results: Bundle) {
            Log.d(TAG, "onResults")
            if (controlVoz) {
                tvRecordVoz!!.text = "Oprime para repetir"
                faRecordVoz!!.setImageDrawable(ResourcesCompat.getDrawable(resources,android.R.drawable.ic_btn_speak_now, null))

                //Log.d(TAG, "onResults"); //$NON-NLS-1$
                val suggestedWords = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                // matches are the return values of speech recognition engine
                // Use these values for whatever you wish to do
                val speechtotext = suggestedWords!![0]

                tvRecordVozVoz!!.text = speechtotext
                tvRecordVozVoz!!.visibility = View.VISIBLE

                val distancia = computeLevenshteinDistance(versiculo!!.versiculo, speechtotext)
                val distanciaTotal = versiculo!!.versiculo.length
                val desvDistacia = distancia.toDouble() / distanciaTotal.toDouble()
                val diffRound = Math.round(desvDistacia * 100) / 100.0
                val porcentajeIgual = ((1 - diffRound) * 100).toInt()
                if (diffRound >= CONSTANTES.PORCENTAJE_OK_LEVENSHTEIN_VERSICULO) {
                    intentosFallidos++
                    tvResultadosVoz!!.setTextColor(Color.parseColor("#FF0000"))
                    if (intentosFallidos <= 1) {
                        tvResultadosVoz!!.text = "Intenta nuevamente : $porcentajeIgual% (Intentos $intentosFallidos de 2)"
                        CONSTANTES.disableButton(btComprobarLearnVoz!!, resources)
                    } else {
                        tvResultadosVoz!!.text = "($porcentajeIgual%) Haz superado el tope de 2 intentos"
                        sonidoNoOk()
                        progressLearn += CONSTANTES.PUNTOS_NOOK
                        CONSTANTES.enableButton(btComprobarLearnVoz!!, resources)
                        faRecordVoz!!.setImageDrawable(ResourcesCompat.getDrawable(resources, android.R.drawable.ic_btn_speak_now, null))
                        tvRecordVoz!!.text = ""
                        faRecordVoz!!.isEnabled = false
                    }
                } else {
                    sonidoOk()
                    progressLearn += CONSTANTES.PUNTOS_OK
                    if (porcentajeIgual == 100) {
                        tvResultadosVoz!!.text = "Excelente!!! : $porcentajeIgual%"
                        tvResultadosVoz!!.setTextColor(ResourcesCompat.getColor(resources, android.R.color.holo_green_light, null))
                    } else if (porcentajeIgual >= 95) {
                        tvResultadosVoz!!.text = "Muy bien continua!!! : $porcentajeIgual%"
                        tvResultadosVoz!!.setTextColor(ResourcesCompat.getColor(resources, android.R.color.holo_blue_light, null))
                    } else {
                        tvResultadosVoz!!.text = "Bien intenta repetirlo!!! : $porcentajeIgual%"
                        tvResultadosVoz!!.setTextColor(ResourcesCompat.getColor(resources, android.R.color.holo_blue_light, null))
                    }
                    CONSTANTES.enableButton(btComprobarLearnVoz!!, resources)
                    faRecordVoz!!.setImageDrawable(ResourcesCompat.getDrawable(resources, android.R.drawable.ic_btn_speak_now, null))
                    tvRecordVoz!!.text = ""
                    faRecordVoz!!.isEnabled = false
                }
                controlVoz = false
            }
        }

        override fun onBufferReceived(buffer: ByteArray) {
            //Log.d(TAG, "onBufferReceived");
        }

        override fun onError(error: Int) {
            mSpeechRecognizer!!.startListening(mSpeechRecognizerIntent)
        }

        override fun onEvent(eventType: Int, params: Bundle) {
            //Log.d(TAG, "onEvent = " + params);
        }

        override fun onPartialResults(partialResults: Bundle) {
            //Log.d(TAG, "onPartialResults = " + partialResults);
        }

        override fun onRmsChanged(rmsdB: Float) {
            //Log.d(TAG, "onPartialResults = " + partialResults);
        }
    }
}