package com.learnbible.model

import java.io.Serializable

class Versiculo(var id: String,
                var libro: String,
                var pasajeSimple: String,
                var pasaje: String,
                var versiculo: String,
                var titulo: String,
                var nivelActual: Int,
                var rondasMax: Int
) : Serializable {
    var isActivo: Boolean = false
}