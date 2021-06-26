package com.learnbible.model

import android.graphics.drawable.Drawable
import java.io.Serializable

class HistoriaVersiculo(var versiculo: Versiculo?,
                        //Historia
                        var historia: Historia
) : Serializable {
    var isVersiculo: Boolean = true
    var color: Drawable? = null
    var colorClaro: Drawable? = null
}