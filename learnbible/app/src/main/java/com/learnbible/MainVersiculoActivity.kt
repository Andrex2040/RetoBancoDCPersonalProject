package com.learnbible

import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.learnbible.activities.home.HomeVersiculoActivity
import com.learnbible.firebase.dto.*
import com.learnbible.model.Historia
import com.learnbible.model.Versiculo
import com.learnbible.superactivities.GeneralActivity
import com.learnbible.utilities.CONSTANTES
import com.learnbible.utilities.MENSAJES
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MainVersiculoActivity : GeneralActivity() {

    var dbFS = FirebaseFirestore.getInstance()
    /**FIREBASE - REMOTE CONFIG */
    var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    var configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(CONSTANTES.INTEVALO_REMOTE_CONFIG.toLong()).build()
    var mapHistoriasFs: MutableMap<String, HistoriasFS>? = null
    var mapLibrosFs: MutableMap<String, LibrosFS>? = null
    var mapVersiculoFs: MutableMap<String, VersiculosFS>? = null
    var mapVersiculoAprendidoFs: MutableMap<String, VersiculosAprendidoFS>? = null
    var readLearn = false
    var readTextoVersiculo = false
    var readAmigos = false
    val pathUsuarios = "/usuarios/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        mFirebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig!!.setDefaultsAsync(R.xml.remote_config_defaults)
        mFirebaseRemoteConfig!!.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                CONSTANTES.PORCENTAJE_OK_LEVENSHTEIN_VERSICULO = mFirebaseRemoteConfig!!.getDouble("porcentaje_ok_levenshtein_versiculo")
                CONSTANTES.PORCENTAJE_OK_LEVENSHTEIN_RECORDAR = mFirebaseRemoteConfig!!.getDouble("porcentaje_ok_levenshtein_recordar")
                CONSTANTES.default_latitud = mFirebaseRemoteConfig!!.getDouble("default_latitud")
                CONSTANTES.default_longitud = mFirebaseRemoteConfig!!.getDouble("default_longitud")
            }
        }
        CONSTANTES.PORCENTAJE_OK_LEVENSHTEIN_VERSICULO = mFirebaseRemoteConfig!!.getDouble("porcentaje_ok_levenshtein_versiculo")
        CONSTANTES.PORCENTAJE_OK_LEVENSHTEIN_RECORDAR = mFirebaseRemoteConfig!!.getDouble("porcentaje_ok_levenshtein_recordar")
        CONSTANTES.default_latitud = mFirebaseRemoteConfig!!.getDouble("default_latitud")
        CONSTANTES.default_longitud = mFirebaseRemoteConfig!!.getDouble("default_longitud")

        mapHistoriasFs = HashMap()
        mapVersiculoFs = HashMap()
        mapLibrosFs = HashMap()
        mapVersiculoAprendidoFs = HashMap()

        consultarFechaSesion()

    }

    fun consultarFechaSesion() {
        val documentUserId = pathUsuarios+CONSTANTES.USER?.uid
        val userRef = dbFS.document(documentUserId)

        // Atomically incrememnt the population of the city by 50.
        userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        CONSTANTES.USERFS = document.toObject(UsuarioFS::class.java)
                        CONSTANTES.USERFS!!.uid = document.id

                        FirebaseInstanceId.getInstance().instanceId
                                .addOnCompleteListener(OnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        return@OnCompleteListener
                                    }
                                    // Get new Instance ID token
                                    val token = task.result?.token
                                    CONSTANTES.USER_DEVICE = DispositivosFS(token)
                                })


                        val sdf = SimpleDateFormat("yyyyMMdd")
                        val dateString = sdf.format(CONSTANTES.USERFS!!.lastSesionDate)
                        val dateNow = sdf.format(Calendar.getInstance().time)

                        CONSTANTES.ISOTHERDAY = !dateString.equals(dateNow)

                        actualizaFechaSesion()
                        cargarHistoriasFS()
                        cargarVersiculosFS()
                        cargarLibrosFS()
                        cargarVersiculosAprendidosFS()
                        cargarLearnFS()
                        misAmigos()
                    } else {
                        crash.crash()
                        Log.d("FIREBASE", "No such document")
                    }
                }
    }


    fun actualizaFechaSesion() {
        val documentUserId = pathUsuarios+CONSTANTES.USER?.uid
        val userRef = dbFS.document(documentUserId)

        CONSTANTES.USERFS!!.lastSesion = Calendar.getInstance().timeInMillis

        userRef.set(CONSTANTES.USERFS!!)
                .addOnSuccessListener {
                    Log.d(CONSTANTES.TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener {
                    e ->
                    crash.crash()
                    Log.w(CONSTANTES.TAG, "Error writing document", e)
                }
    }

    fun cargarLearnFS() {
        dbFS.collection("learn")
                .document("historias")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        CONSTANTES.LEARN_HISTORIA = document.toObject(LearnHistoriasFS::class.java)
                        relacionarLibrosFS()
                    } else {
                        crash.crash()
                        Log.d("FIREBASE", "No such document")
                    }
                }

    }


    fun misAmigos(){
        dbFS.collection("/usuarios/" + CONSTANTES.USERFS!!.uid + "/amigos")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        isNormal=true

                        CONSTANTES.listAmigos = ArrayList()
                        for (document in task.result!!) {
                            val amigosFS = document.toObject(UsuarioFriendsFS::class.java)
                            amigosFS.uid = document.id
                            CONSTANTES.listAmigos.add(amigosFS)
                        }
                        readAmigos = true
                        relacionarLibrosFS()
                    } else {
                        Log.w("FIREBASE", MENSAJES.ER_GET_DOC_FIREBASE, task.exception)
                        if(isNormal) {
                            isNormal=false
                            misAmigos()
                        }else{
                            crash.crash()
                        }
                    }
                }
    }

    fun cargarHistoriasFS() {
        dbFS.collection("historias")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val historiasFS = document.toObject(HistoriasFS::class.java)
                            historiasFS.id = document.id
                            mapHistoriasFs!![document.reference.path] = historiasFS
                        }
                        relacionarLibrosFS()
                    } else {
                        crash.crash()
                        Log.w("FIREBASE", MENSAJES.ER_GET_DOC_FIREBASE, task.exception)
                    }
                }
    }

    fun cargarVersiculosFS() {
        dbFS.collection("versiculos")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document2 in task.result!!) {
                            mapVersiculoFs!![document2.reference.path] = document2.toObject(VersiculosFS::class.java)
                        }
                        cargarTextoVersiculosFS()
                        relacionarLibrosFS()
                    } else {
                        crash.crash()
                        Log.w("FIREBASE", MENSAJES.ER_GET_DOC_FIREBASE, task.exception)
                    }
                }
    }

    fun cargarTextoVersiculosFS() {
        if(mapVersiculoFs!!.size > 0 ) {

            var control = 0
            for((path, versiculoFS)  in mapVersiculoFs!!) {
                dbFS.collection(path + "/versiones/").document("RVR1960").get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                mapVersiculoFs!!.get(path)!!.texto = document.getString("texto")
                                control++
                                //revisa que sea el ultimo read de textos del versiculo para poder relacionar los textos con la data
                                if(mapVersiculoFs!!.size == control) {
                                    readTextoVersiculo = true
                                    relacionarLibrosFS()
                                }
                            }
                        }
            }
        }
    }

    fun cargarVersiculosAprendidosFS() {
        dbFS.collection(pathUsuarios+CONSTANTES.USER?.uid+"/versiculos")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        CONSTANTES.listVersiculosAprendidosApp = ArrayList()
                        for (document in task.result!!) {
                            val versiculosAprendidoFS = document.toObject(VersiculosAprendidoFS::class.java)
                            mapVersiculoAprendidoFs!![document.reference.path] = versiculosAprendidoFS

                            if(versiculosAprendidoFS.estrellas!!.toInt() > 0){
                                CONSTANTES.listVersiculosAprendidosApp.add(versiculosAprendidoFS)
                            }
                        }
                        readLearn = true
                        relacionarLibrosFS()
                    } else {
                        crash.crash()
                        Log.w("FIREBASE", MENSAJES.ER_GET_DOC_FIREBASE, task.exception)
                    }
                }
    }

    fun cargarLibrosFS() {
        dbFS.collection("libros")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            mapLibrosFs!![document.reference.path] = document.toObject(LibrosFS::class.java)
                        }
                        relacionarLibrosFS()
                    } else {
                        crash.crash()
                        Log.w("FIREBASE", MENSAJES.ER_GET_DOC_FIREBASE, task.exception)
                    }
                }
    }

    //con toda la data de Libros+Versiculos se relacionan los datos
    fun relacionarLibrosFS() {
        if (mapVersiculoFs!!.size > 0 && mapLibrosFs!!.size > 0 && mapHistoriasFs!!.size > 0
                && readLearn && CONSTANTES.LEARN_HISTORIA != null && readTextoVersiculo && readAmigos) {

            CONSTANTES.listHistoriasApp = ArrayList()
            for (arr in CONSTANTES.LEARN_HISTORIA!!.arrayhistorias!!) {
                val historiasFS = mapHistoriasFs!!.get(arr.path)
                val listaVersiculos: MutableList<Versiculo> = ArrayList()

                var sumNivelActual = 0
                var sumRondas = 0
                for (versiculosRef in historiasFS?.versiculos!!) {

                    try {
                        val versiculosFS = mapVersiculoFs!![versiculosRef.path]
                        val librosFS = mapLibrosFs!![versiculosFS!!.libros!!.path]
                        val libro = librosFS!!.nombre
                        val pasajeSimple = versiculosFS.capitulo.toString() + ":" + versiculosFS.versiculo
                        val pasaje = libro + " " + pasajeSimple

                        val nivelActual = nivelActual(versiculosRef.path)

                        ///versiculos/genesis_1_27/versiones/RVR1960

                        val rondas = calcularRondas(versiculosFS.texto!!)

                        sumNivelActual += nivelActual
                        sumRondas += rondas

                        if (versiculosFS.titulo == null) {
                            versiculosFS.titulo = pasaje
                        }

                        val versiculo = Versiculo(versiculosRef.path, libro!!, pasajeSimple, pasaje, versiculosFS.texto!!, versiculosFS.titulo!!, nivelActual, rondas)
                        listaVersiculos.add(versiculo)
                    }catch (e: Exception) {
                        crash.crash()
                        Log.e(CONSTANTES.TAG, "error:"+versiculosRef.id+" - "+e.toString())
                    }
                }
                val historia = Historia(historiasFS.id!!, historiasFS.titulo!!, sumNivelActual, sumRondas, listaVersiculos)
                CONSTANTES.listHistoriasApp.add(historia)
            }


            openActivity(this@MainVersiculoActivity, HomeVersiculoActivity::class.java, true)
        }
    }

    fun nivelActual(idPath: String): Int{
        var nivelActual = 0
        val versiculosAprendidoFS = mapVersiculoAprendidoFs!!.get("usuarios/"+CONSTANTES.USER?.uid+"/"+idPath)
        if(versiculosAprendidoFS != null){
            nivelActual =  versiculosAprendidoFS.nivel!!.toInt()
        }
        return nivelActual
    }

    fun calcularRondas(texto: String): Int{

        val size = texto.split(" ").size
        val calculo = Math.pow(size.toDouble(), 0.5)
        var rondas = Math.round(calculo).toInt()
        if(rondas>5){
            rondas = 5
        }
        if(rondas<=0){
            rondas = 1
        }
        return rondas
    }
}