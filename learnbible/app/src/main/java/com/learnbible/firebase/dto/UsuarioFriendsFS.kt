package com.learnbible.firebase.dto

import com.google.firebase.firestore.GeoPoint
import java.util.*

class UsuarioFriendsFS (
    var coin: Long? = null,
    var lastSesion: Long? = null,
    var lastSesionDate: Date?=null,
    var xp: Long?=null,
    var localizacion: GeoPoint?=null,
    var nombre: String = "",
    var uid:String? = null,
    var isAmigo:Boolean? = false,
    var iEnvie:Boolean? = false,
    var photoUrl: String = "",
    var lastVersiculo: String = ""
)
