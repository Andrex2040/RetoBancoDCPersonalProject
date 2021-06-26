package com.learnbible.firebase.dto

import com.google.firebase.firestore.DocumentReference

class HistoriasFS (
    var id: String? = null,
    val titulo: String? = null,
    val versiculos: List<DocumentReference>?=null
)
