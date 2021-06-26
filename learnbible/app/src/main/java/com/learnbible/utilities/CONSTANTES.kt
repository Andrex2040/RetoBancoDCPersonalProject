package com.learnbible.utilities

import android.app.Activity
import android.content.res.Resources
import android.location.Location
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.GeoPoint
import com.learnbible.R
import com.learnbible.firebase.dto.*
import com.learnbible.model.Historia
import com.learnbible.model.HistoriaVersiculo
import com.learnbible.model.Versiculo

object CONSTANTES {

    val TAG = "FIREBASE"

    //hora*minuto*segundos
    var INTEVALO_REMOTE_CONFIG = 1 * 1 * 60
    var PUNTOS_OK = 2
    var PUNTOS_NOOK = -1


    //porcentaje que aparece prueba de Voz
    var PORCENTAJE_VOZ = 15 //15
    //porcentaje que aparece pasaje 30
    var PORCENTAJE_PASAJE = 30
    //porcentaje que aparece versiculo sin boton 70
    var PORCENTAJE_VERSICULO_TEXTO = 60


    var PALABRAS_HIDDEN_MAX = 5

    var ISOTHERDAY: Boolean = false
    var LEARN_HISTORIA: LearnHistoriasFS? = null
    var listHistoriasApp: MutableList<Historia> = ArrayList()
    var listHistoriasVersiculoApp: MutableList<HistoriaVersiculo> = ArrayList()
    var listVersiculosAprendidosApp: MutableList<VersiculosAprendidoFS> = ArrayList()
    var USER: FirebaseUser? = null
    var USERFS: UsuarioFS? = null
    var USER_DEVICE: DispositivosFS? = null
    var listAmigos: MutableList<UsuarioFriendsFS> = ArrayList()


    var listFriendsA50: MutableList<UsuarioFriendsFS> = ArrayList()

    //remote_config
    var PORCENTAJE_OK_LEVENSHTEIN_VERSICULO = 0.0
    var PORCENTAJE_OK_LEVENSHTEIN_RECORDAR = 0.0
    var default_latitud = 0.0
    var default_longitud = 0.0

    fun cambiarImagenPerfile(photo:String, abIvProfiles: ImageView?, activity: Activity) {

        //ver si es google o face
        val red = photo.indexOf("google")
        var photoSize = "$photo?type=large"

        if(red != -1){
            photoSize =  photo.replace("s96-c","s360-c")//"$photo?height=5000"
        }

        if(photo.length > 5 && abIvProfiles != null) {
            Glide.with(activity).load(photoSize).into(abIvProfiles)
        }
    }

    fun disableButton(button: Button, resources: Resources){
        button.isEnabled = false
        button.setTextColor(ResourcesCompat.getColor(resources, R.color.colorAzulRio, null))
        button.backgroundTintList = ResourcesCompat.getColorStateList(resources, R.color.colorBlanco, null)
    }

    fun enableButton(button: Button, resources: Resources){
        button.isEnabled = true
        button.setTextColor(ResourcesCompat.getColor(resources, R.color.colorBlanco, null))
        button.backgroundTintList = ResourcesCompat.getColorStateList(resources, R.color.colorAzulRio, null)
    }

    fun allVersiculos() : List<Versiculo>{
        val listVersiculo:MutableList<Versiculo> = ArrayList()

        for(historia in listHistoriasApp){
            for(versiculo in historia.listVericulos){
                listVersiculo.add(versiculo)
            }
        }
        return listVersiculo
    }

    fun cambiaAvanceHistoriaVersiculos(versiculo:Versiculo,aumentaCoronas:Int){

        var i = 0
        for (historiaVersiculo in listHistoriasVersiculoApp) {
            if(historiaVersiculo.isVersiculo && historiaVersiculo.versiculo!!.id.equals(versiculo.id)){
                break
            }
            i++
        }

        if( i < listHistoriasVersiculoApp.size) {

            val versiculoC = listHistoriasVersiculoApp.get(i).versiculo
            versiculoC!!.nivelActual++

            //actualiza visualmente la historia
            var pos = i
            while (pos >= 0) {
                if (!listHistoriasVersiculoApp.get(pos).isVersiculo) {
                    listHistoriasVersiculoApp.get(pos).historia.avanceHistoria++
                    break
                }
                pos--
            }

            if (aumentaCoronas > 0) {
                //add to list stars
                val versiculoAprendidoFS = VersiculosAprendidoFS(versiculoC.id, versiculoC.nivelActual.toLong(),
                        versiculoC.pasaje, versiculoC.versiculo, 3)
                listVersiculosAprendidosApp.add(versiculoAprendidoFS)
            }
        }
    }



    fun aleatorioRango(desde: Int, hasta: Int): Int {
        //Variables M y N son desde 1ra palabra hasta el maximo numero de palabras
        val posDouble = Math.random() * (hasta - desde + 1) + desde
        return Math.floor(posDouble).toInt()
    }

    fun reemplazaUnderline(palabraOriginal: String): String {
        val underline = StringBuilder("")
        for (i in 0 until palabraOriginal.length) {
            underline.append("_")
        }
        return underline.toString()
    }

    fun reemplazaGuion(palabraOriginal: String): String {
        val underline = StringBuilder("")
        for (i in 0 until palabraOriginal.length) {
            underline.append("-")
        }
        return underline.toString()
    }

    fun cercaKm(geo: GeoPoint?): Float {

        var lat = 0.0
        var lon = 0.0

        if(geo != null) {
            lat = geo.latitude
            lon = geo.longitude
        }

        val loc = Location("")
        loc.latitude = lat
        loc.longitude = lon

        //other
        var lat2 = 0.0
        var lon2 = 0.0

        if(USERFS!!.localizacion != null) {
            lat2 = USERFS!!.localizacion!!.latitude
            lon2 = USERFS!!.localizacion!!.longitude
        }

        val loc2 = Location("")
        loc2.latitude = lat2
        loc2.longitude = lon2


        val returnfloat =  Math.round((loc2.distanceTo(loc)/1000)*100.0/100.0).toFloat()
        return returnfloat

    }

    fun toUserToFriend(usuarioFS: UsuarioFS, isAmigo:Boolean, iEnvie:Boolean): UsuarioFriendsFS {
        val usuarioFriendsFS = UsuarioFriendsFS()
        usuarioFriendsFS.uid = usuarioFS.uid
        usuarioFriendsFS.coin = usuarioFS.coin
        usuarioFriendsFS.lastSesion = usuarioFS.lastSesion
        usuarioFriendsFS.lastSesionDate = usuarioFS.lastSesionDate
        usuarioFriendsFS.localizacion = usuarioFS.localizacion
        usuarioFriendsFS.nombre = usuarioFS.nombre
        usuarioFriendsFS.photoUrl = usuarioFS.photoUrl
        usuarioFriendsFS.xp = usuarioFS.xp
        usuarioFriendsFS.isAmigo = isAmigo
        usuarioFriendsFS.iEnvie = iEnvie
        usuarioFriendsFS.lastVersiculo = usuarioFS.lastVersiculo

        return usuarioFriendsFS
    }
}