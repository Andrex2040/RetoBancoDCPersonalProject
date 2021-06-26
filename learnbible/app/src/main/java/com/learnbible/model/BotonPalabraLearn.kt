package com.learnbible.model

import java.io.Serializable

class BotonPalabraLearn(pos: Int, palabra: String?, isTrue: Boolean) : Serializable {
    var pos = 0
    var palabra: String? = null
    var isTrue = false

    init {
        this.pos = pos
        this.palabra = palabra
        this.isTrue = isTrue
    }
}