package com.learnbible.utilities

import android.view.View
import android.view.ViewGroup

object ADAPTERS {
    
    fun setComun(v: View, palabra: String, position: Int, isButton: Boolean) {
        v.tag = position
        val params = v.layoutParams
        if (isButton) {
            wrapWith(palabra, params)
        }
        v.layoutParams = params
        v.visibility = View.VISIBLE
    }

    fun wrapWith(palabra: String, params: ViewGroup.LayoutParams){
        if (palabra.length == 3) params.width = 180
        else if (palabra.length <= 2) params.width = 140
        else params.width = ViewGroup.LayoutParams.WRAP_CONTENT
    }
}