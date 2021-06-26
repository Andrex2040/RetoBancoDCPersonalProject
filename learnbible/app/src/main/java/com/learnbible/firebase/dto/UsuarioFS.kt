package com.learnbible.firebase.dto

import com.google.firebase.firestore.GeoPoint
import java.util.*

class UsuarioFS (
    var coin: Long = 0,
    var lastSesion: Long = 0,
    var lastSesionDate: Date?=null,
    var xp: Long = 0,
    var localizacion: GeoPoint?=null,
    var nombre: String = "",
    var photoUrl: String = ""
){
    var uid:String? = null
    var lastVersiculo:String = ""
}
