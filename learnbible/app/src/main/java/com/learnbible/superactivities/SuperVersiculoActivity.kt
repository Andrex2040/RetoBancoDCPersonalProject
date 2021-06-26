package com.learnbible.superactivities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.firestore.FirebaseFirestore
import com.learnbible.R
import com.learnbible.activities.core.*
import com.learnbible.adapter.PalabraBotonAdapter
import com.learnbible.model.BotonPalabraLearn
import com.learnbible.model.Palabra
import com.learnbible.model.Versiculo
import com.learnbible.utilities.CONSTANTES
import com.learnbible.utilities.CONSTANTES.aleatorioRango
import java.util.*

open class SuperVersiculoActivity : GeneralActivity() {
    // Access a Cloud Firestore instance from your Activity
    var dbFS = FirebaseFirestore.getInstance()

    var listPalabrasTransformadas: MutableList<Palabra>? = null
    var listPosPalVersVectAleatoria: MutableList<Int>? = null
    var arrPalabrasOriginales: Array<String>? = null
    var dataButton: MutableList<BotonPalabraLearn>? = null

    var rvVersiculoLearn:RecyclerView? = null;
    var btComprobarLearn:Button? = null

    var progressLearnOld = 0
    var progressLearn = 0
    var progressMaxLearn = 10
    var versiculo: Versiculo? = null
    var levelLearn = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) //show back button

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.mostrarDialog("¿Estás seguro de que quieres salir?","Perderás el progreso de la prueba.")
    }


    //habilita y crea controles visuales
    fun controles(c: Context) {
        val tvPasajeLearn = findViewById<TextView>(R.id.tvPasajeLearn)
        rvVersiculoLearn = findViewById(R.id.rvVersiculoLearn)
        btComprobarLearn = findViewById(R.id.btComprobarLearn)

        val pbLearn = findViewById<ProgressBar>(R.id.pbLearn)

        circleNivel()

        pbLearn.progress = progressLearn
        pbLearn.max = progressMaxLearn
        cambiarColor(c, pbLearn)
        tvPasajeLearn.text = versiculo!!.pasaje
        listPalabrasTransformadas = ArrayList()
        val layoutManager = FlexboxLayoutManager(c)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.FLEX_START
        rvVersiculoLearn!!.layoutManager = layoutManager

        CONSTANTES.disableButton(btComprobarLearn!!, resources)

        btComprobarLearn!!.setOnClickListener {
            if (dataButton != null && dataButton!!.size > 0) {
                var todoOK = true
                for (posPalVersVectAleatoria in listPosPalVersVectAleatoria!!) {
                    if (arrPalabrasOriginales!![posPalVersVectAleatoria] == listPalabrasTransformadas!!.get(posPalVersVectAleatoria).palabra) {
                        todoOK = true
                    } else {
                        todoOK = false
                        break
                    }
                }
                progressLearn += if (todoOK) {
                    sonidoOk()
                    CONSTANTES.PUNTOS_OK
                } else {
                    sonidoNoOk()
                    CONSTANTES.PUNTOS_NOOK
                }
            }
            mostrarActivity(c)
        }

    }

    fun circleNivel(){
        if(progressLearn==0) {
            val tvLevel = findViewById<TextView>(R.id.tvLevel)
            tvLevel.text = "Nivel\n"+levelLearn
            tvLevel.visibility = View.VISIBLE
        }
    }

    //habilita y crea controles visuales
    fun controlesDinamicos(c: Context) {
        var maxText = CONSTANTES.PALABRAS_HIDDEN_MAX
        if (arrPalabrasOriginales!!.size <= 2) {
            maxText = 3
        }
        dataButton = ArrayList()
        //identifica palabras aleatorias para los botones
        for (pos in 0 until maxText) {
            val posnuevo = posicionAleatoriaPalabras(arrPalabrasOriginales!!.size)
            dataButton!!.add(BotonPalabraLearn(pos, arrPalabrasOriginales!![posnuevo], false))
        }

        //agrega palabras a botones
        for (posPalVersVectAleatoria in listPosPalVersVectAleatoria!!) {
            val posnuevo = aleatorioRango(0, dataButton!!.size - 1)
            val bpltrue = BotonPalabraLearn(posnuevo,
                    arrPalabrasOriginales!![posPalVersVectAleatoria], true)
            dataButton!!.add(posnuevo, bpltrue)
        }


        //crea el adapter para los botones
        val adapter = PalabraBotonAdapter(c, dataButton!!)
        val rvBotonPalabraLearn = findViewById<RecyclerView>(R.id.rvBotonPalabraLearn)
        rvBotonPalabraLearn.adapter = adapter
        val layoutManager = FlexboxLayoutManager(c)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.CENTER
        rvBotonPalabraLearn.layoutManager = layoutManager
    }

    fun posicionAleatoriaPalabras(size:Int): Int {
        val desde = 0
        val hasta = size - 1
        return aleatorioRango(desde, hasta)
    }

    fun obtenerParametros() {
        versiculo = intent.getSerializableExtra("versiculo") as Versiculo
        progressLearn = intent.getSerializableExtra("progressLearn") as Int
        progressLearnOld = intent.getSerializableExtra("progressLearnOld") as Int
        levelLearn = intent.getSerializableExtra("levelLearn") as Int
    }

    fun sonidoNoOk() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.nook)
        mediaPlayer.start()
    }

    fun sonidoOk() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.siok)
        mediaPlayer.start()
    }

    fun sonidoLevelOk() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.levelok)
        mediaPlayer.start()
    }

    @SuppressLint("ResourceAsColor")
    fun cambiarColor(c: Context?, pbLearn: ProgressBar) {
        var color = ContextCompat.getColor(c!!, R.color.colorVerde)
        if (progressLearnOld > progressLearn) {
            color = ContextCompat.getColor(c, R.color.colorRojo)
        }
        // fixes pre-Lollipop progressBar indeterminateDrawable tinting
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val wrapDrawable = DrawableCompat.wrap(pbLearn.indeterminateDrawable)
            DrawableCompat.setTint(wrapDrawable, color)
            pbLearn.indeterminateDrawable = DrawableCompat.unwrap(wrapDrawable)
        } else {
            pbLearn.progressTintList = ColorStateList.valueOf(color)
        }
        progressLearnOld = progressLearn
    }

    fun mostrarActivity(c: Context) {
        if (progressLearn >= progressMaxLearn) {
            openActivityEnviaVersiculoProgress(c, VersiculoLearnFinalVersiculoActivity::class.java, versiculo, progressLearn, progressLearnOld, levelLearn, true)
        } else if (progressLearn == 0) {
            openActivityEnviaVersiculoProgress(c, VersiculoLearnVersiculoActivity::class.java, versiculo, progressLearn, progressLearnOld, levelLearn, true)
        } else {
            if (progressLearn == CONSTANTES.PUNTOS_OK) {
                sonidoOk()
            }
            val aleatorio = aleatorioRango(1, 100)
            if (aleatorio <= CONSTANTES.PORCENTAJE_VOZ) {
                openActivityEnviaVersiculoProgress(c, VersiculoLearnVozVersiculoActivity::class.java, versiculo, progressLearn, progressLearnOld, levelLearn, true)
            } else if (aleatorio <= CONSTANTES.PORCENTAJE_PASAJE) {
                openActivityEnviaVersiculoProgress(c, VersiculoLearnPasajeVersiculoActivity::class.java, versiculo, progressLearn, progressLearnOld, levelLearn, true)
            } else if (aleatorio <= CONSTANTES.PORCENTAJE_VERSICULO_TEXTO) {
                openActivityEnviaVersiculoProgress(c, VersiculoLearnTextVersiculoActivity::class.java, versiculo, progressLearn, progressLearnOld, levelLearn, true)
            } else {
                openActivityEnviaVersiculoProgress(c, VersiculoLearnVersiculoActivity::class.java, versiculo, progressLearn, progressLearnOld, levelLearn, true)
            }
        }
    }


    fun openActivityEnviaVersiculoProgress(cActual: Context, clase: Class<*>?, versiculo: Versiculo?, progressLearn: Int, progressLearnOld: Int, levelLearn: Int, isFinish: Boolean) {
        val i = Intent(cActual, clase)
        i.putExtra("versiculo", versiculo)
        i.putExtra("progressLearn", progressLearn)
        i.putExtra("progressLearnOld", progressLearnOld)
        i.putExtra("levelLearn", levelLearn)
        cActual.startActivity(i)
        if (isFinish) (cActual as Activity).finish()
    }

    fun transformarTexto() {

        //inicia transformacion de texto
        listPosPalVersVectAleatoria = ArrayList()
        //revision posiciones aleatorias y diferentes dentro del versiculo
        var x = 1
        while (x <= levelLearn) {
            val posAleatoria = posicionAleatoriaPalabras(arrPalabrasOriginales!!.size)
            var aprobado = true
            for (pos in listPosPalVersVectAleatoria!!) {
                if (posAleatoria == pos) {
                    aprobado = false
                    break
                }
            }
            if (aprobado) {
                listPosPalVersVectAleatoria!!.add(posAleatoria)
            } else {
                if (levelLearn <= arrPalabrasOriginales!!.size) {
                    //Con esta accion repite el proceso para extraer las palabras aleatorias
                    x--
                }
            }
            x++
        }

        //ordena los valores aleatorios
        Collections.sort(listPosPalVersVectAleatoria)
        reemplazarTexto()
    }

    fun reemplazarTexto() {
        //calcula largo de ____ para la palabra
        for (posPalVersVecAleatoria in listPosPalVersVectAleatoria!!) {
            val underline = CONSTANTES.reemplazaUnderline(arrPalabrasOriginales!![posPalVersVecAleatoria])
            //reemplazo de palabra aleatoria por ___
            listPalabrasTransformadas!![posPalVersVecAleatoria] = Palabra(underline, true, posPalVersVecAleatoria)
        }
    }

    fun validarTextoSuper(btn: Button) {
        val palabraSeleccionada = btn.text.toString()
        var palabrasSel = 0
        for (palabraTransformada in listPalabrasTransformadas!!) {
            if (palabraTransformada.isText) {
                if (palabraTransformada.palabra != arrPalabrasOriginales!![palabraTransformada.position]) {
                    break
                }
            } else {
                palabrasSel++
            }
        }


        //algoritmo para revertir a underline despues de llegar al tope de 5
        if (palabrasSel == levelLearn) {
            for (palabraTransformada in listPalabrasTransformadas!!) {
                //develve los cambios a Palabras con underline
                if (!palabraTransformada.isText) {
                    palabraTransformada.palabra = CONSTANTES.reemplazaUnderline(palabraTransformada.palabra)
                    palabraTransformada.isText = true
                }
            }
            palabrasSel = 0
        }

        //algoritmo convertir palabra en botones solo identificacion
        convertirPalabrasBotones(palabrasSel, palabraSeleccionada)
    }

    fun convertirPalabrasBotones(palabrasSel: Int, palabraSeleccionada: String){
        //algoritmo convertir palabra en botones
        var i = 0
        for (palabraTransformada in listPalabrasTransformadas!!) {
            //transforma a boton la palabra seleccionada
            if (listPosPalVersVectAleatoria!![palabrasSel] == i) {
                palabraTransformada.palabra = palabraSeleccionada
                palabraTransformada.isText = false
                break
            }
            i++
        }

        //validar enable boton
        i=0
        for (palabraTransformada in listPalabrasTransformadas!!) {
            if (!palabraTransformada.isText) {
                 i++
            }
        }

        CONSTANTES.disableButton(btComprobarLearn!!, resources)
        if(i == levelLearn){
            CONSTANTES.enableButton(btComprobarLearn!!, resources)
        }
    }
}