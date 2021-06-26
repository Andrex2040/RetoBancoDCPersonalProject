package com.learnbible.firebase.dto

import com.google.firebase.firestore.DocumentReference

data class VersiculosFS(
        val capitulo: Long? = null,
        val versiculo: Long? = null,
        var texto: String? = null,
        var titulo: String? = null,
        val libros: DocumentReference? = null
)