package com.learnbible.activities.core

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.learnbible.R
import com.learnbible.firebase.dto.VersiculosAprendidoFS
import com.learnbible.superactivities.SuperRecordarActivity
import com.learnbible.utilities.CONSTANTES
import com.learnbible.utilities.CONSTANTES.reemplazaGuion
import com.learnbible.utilities.LevenshteinDistance.computeLevenshteinDistance
import java.util.*
import kotlin.collections.ArrayList

class RecordarLearnVozVersiculoActivity : SuperRecordarActivity() {
    private var tvPasajeLearnVoz: TextView? = null
    private var tvVersiculoLearnVoz: TextView? = null
    private var tvResultadosVoz: TextView? = null
    private var btComprobarLearnVoz: Button? = null
    private var ivRecordVoz: ImageView? = null
    private var tvRecordVoz: TextView? = null
    private var tvRecordVozVoz: TextView? = null
    private var rbStar: RatingBar? = null
    private var palabrasOriginales: Array<String>? = null
    private var listPalabras: MutableList<String>? = null
    private var intentosFallidos = 0
    private var controlVoz = true
    private var mSpeechRecognizer: SpeechRecognizer? = null
    private var mSpeechRecognizerIntent: Intent? = null

    val documentUserId = "/usuarios/"+CONSTANTES.USER?.uid
    val TAG = "RECORDAR"
    var aumentaCoronas: Int = 0
    var aumentaXP: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recordar_learn_voz)
        super.obtenerParametros()
        controles()
    }

    fun controles() {
        tvPasajeLearnVoz = findViewById(R.id.tvPasajeLearnVoz)
        tvVersiculoLearnVoz = findViewById(R.id.tvVersiculoLearnVoz)
        tvResultadosVoz = findViewById(R.id.tvResultadosVoz)
        btComprobarLearnVoz = findViewById(R.id.btComprobarLearnVoz)
        ivRecordVoz = findViewById(R.id.ivRecordVoz)
        tvRecordVoz = findViewById(R.id.tvRecordVoz)
        tvRecordVozVoz = findViewById(R.id.tvRecordVozVoz)
        rbStar = findViewById(R.id.rbStar)
        rbStar!!.rating = versiculoAprendidoFs!!.estrellas!!.toFloat()

        tvPasajeLearnVoz!!.text = versiculoAprendidoFs!!.pasaje
        tvVersiculoLearnVoz!!.text = versiculoAprendidoFs!!.versiculo

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mSpeechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
        mSpeechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mSpeechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.packageName)

        val listener = SpeechRecognitionListener()
        mSpeechRecognizer!!.setRecognitionListener(listener)

        CONSTANTES.disableButton(btComprobarLearnVoz!!,resources)
        ivRecordVoz!!.setOnClickListener{
            tvResultadosVoz!!.text = ""
            tvRecordVoz!!.text = "Escuchando..."
            CONSTANTES.disableButton(btComprobarLearnVoz!!,resources)
            ivRecordVoz!!.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.icon_record, null))
            mSpeechRecognizer!!.startListening(mSpeechRecognizerIntent)
        }
        btComprobarLearnVoz!!.setOnClickListener{

            val versiculo = CONSTANTES.listVersiculosAprendidosApp.get(posicion)
            if(isCorrect) {
                versiculo.estrellas = versiculo.estrellas!!+1

                val star = versiculo.estrellas!!.toInt()
                var porcentaje = 0.0
                if(star == 1){
                    porcentaje = 0.01
                }else if(star == 2){
                    porcentaje = 0.05
                }else if(star == 3){
                    porcentaje = 0.1
                }else if(star == 4){
                    porcentaje = 0.25
                }else if(star == 5){
                    porcentaje = 0.5
                }

                aumentaXP = (versiculo.versiculo!!.split(" ").size * porcentaje).toInt()
                aumentaCoronas = 1
                updateVerseUser(versiculo)
                saveVerseXPUser(versiculo)
                saveXpUser(versiculo)
                finish()
            }else{

                //minimo sale del recordar con una estrella positiva
                if(versiculo.estrellas!! > 0) {
                    versiculo.estrellas = versiculo.estrellas!! - 1
                }

                updateVerseUser(versiculo)
                openActivityEnviaPos(this, RecordarLearnVozVersiculoActivity::class.java, posicion, true)
            }
        }
        transformarTextoVoz(tvVersiculoLearnVoz)
    }


    fun updateVerseUser(versiculo:VersiculosAprendidoFS){

        val document = documentUserId+"/"+versiculo.id

        dbFS.document(document)
                .update("estrellas", versiculo.estrellas)
                .addOnSuccessListener { Log.d(TAG, "updateVerseUser successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing updateVerseUser", e) }

    }

    fun saveVerseXPUser(versiculo:VersiculosAprendidoFS){
        val versiculoMap = hashMapOf(
                "id" to versiculo.id,
                "pasaje" to versiculo.pasaje,
                "versiculo" to versiculo.versiculo,
                "nivel" to versiculo.nivel,
                "xp" to aumentaXP
        )

        val document = documentUserId+"/versiculosxp/"+Calendar.getInstance().timeInMillis

        dbFS.document(document)
                .set(versiculoMap)
                .addOnSuccessListener { Log.d(TAG, "versiculoMap successfully written!") }
                .addOnFailureListener { e -> crash.crash()
                    Log.w(TAG, "Error writing versiculoMap", e) }

    }

    fun saveXpUser(versiculo:VersiculosAprendidoFS){

        if(aumentaCoronas>0){
            saveVerseCoronasUser(versiculo)
        }

        val userRef = dbFS.document(documentUserId)

        CONSTANTES.USERFS!!.lastSesionDate = Calendar.getInstance().time
        CONSTANTES.USERFS!!.xp = CONSTANTES.USERFS!!.xp + aumentaXP.toLong()
        CONSTANTES.USERFS!!.coin = CONSTANTES.USERFS!!.coin + aumentaCoronas.toLong()

        CONSTANTES.ISOTHERDAY = false
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

    fun saveVerseCoronasUser(versiculo:VersiculosAprendidoFS){
        val versiculoMap = hashMapOf(
                "id" to versiculo.id,
                "pasaje" to versiculo.pasaje,
                "versiculo" to versiculo.versiculo,
                "nivel" to versiculo.nivel,
                "coin" to aumentaCoronas
        )

        val document = documentUserId+"/versiculoscoin/"+Calendar.getInstance().timeInMillis

        dbFS.document(document)
                .set(versiculoMap)
                .addOnSuccessListener { Log.d(TAG, "versiculoMap successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing versiculoMap", e) }

    }

    fun transformarTextoVoz(tvVersiculoLearn: TextView?) {
        palabrasOriginales = tvVersiculoLearn!!.text.toString().split(" ").toTypedArray()
        listPalabras = ArrayList()
        for (s in palabrasOriginales!!) {
            listPalabras!!.add(s)
        }
        reemplazarTextoVoz()
    }

    //recursividad para que no se repitan las palabras
    fun buscaPalabraAquitar(listPos:MutableCollection<Int>){
        val pos = super.posicionAleatoriaPalabras(palabrasOriginales!!.size)
        var exist = false
        for(posi in listPos){
            if(posi == pos){
                exist = true
            }
        }
        if(exist){
            buscaPalabraAquitar(listPos)
        }else{
            listPos.add(pos)
            //reemplazo de palabra aleatoria por ----
            listPalabras!![pos] = reemplazaGuion(palabrasOriginales!![pos])
        }
    }

    fun reemplazarTextoVoz() {
        //calcula largo de ____ para la palabra
        val dividendo: Double = versiculoAprendidoFs!!.estrellas!!.toDouble().div(4f)
        val y = (palabrasOriginales!!.size * dividendo).toInt()-1

        var x = 0
        val listPos:MutableCollection<Int> = ArrayList()
        while (x <= y) {
            buscaPalabraAquitar(listPos)
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
                ivRecordVoz!!.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.icon_microphone, null))

                //Log.d(TAG, "onResults"); //$NON-NLS-1$
                val suggestedWords = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                // matches are the return values of speech recognition engine
                // Use these values for whatever you wish to do
                val speechtotext = suggestedWords!![0]

                tvRecordVozVoz!!.text = speechtotext
                tvRecordVozVoz!!.visibility = View.VISIBLE

                val distancia = computeLevenshteinDistance(versiculoAprendidoFs!!.versiculo!!, speechtotext)
                val distanciaTotal = versiculoAprendidoFs!!.versiculo!!.length
                val desvDistacia = distancia.toDouble() / distanciaTotal.toDouble()
                val diffRound = Math.round(desvDistacia * 100) / 100.0
                val porcentajeIgual = ((1 - diffRound) * 100).toInt()
                if (diffRound >= CONSTANTES.PORCENTAJE_OK_LEVENSHTEIN_RECORDAR) {
                    intentosFallidos++
                    tvResultadosVoz!!.setTextColor(Color.parseColor("#FF0000"))
                    if (intentosFallidos <= 1) {
                        tvResultadosVoz!!.text = "Intenta nuevamente : $porcentajeIgual% (Intentos $intentosFallidos de 2)"
                        CONSTANTES.disableButton(btComprobarLearnVoz!!,resources)
                    } else {
                        tvResultadosVoz!!.text = "($porcentajeIgual%) Haz superado el tope de 2 intentos"
                        sonidoNoOk()
                        CONSTANTES.enableButton(btComprobarLearnVoz!!,resources)
                        ivRecordVoz!!.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.icon_microphone_disabled, null))
                        tvRecordVoz!!.text = ""
                        ivRecordVoz!!.isEnabled = false
                    }
                } else {
                    sonidoOk()
                    isCorrect = true
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
                    CONSTANTES.enableButton(btComprobarLearnVoz!!,resources)
                    ivRecordVoz!!.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.icon_microphone_disabled, null))
                    tvRecordVoz!!.text = ""
                    ivRecordVoz!!.isEnabled = false
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