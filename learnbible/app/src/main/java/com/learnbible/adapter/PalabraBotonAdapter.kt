package com.learnbible.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.learnbible.R
import com.learnbible.activities.core.VersiculoLearnVersiculoActivity
import com.learnbible.activities.core.VersiculoLearnPasajeVersiculoActivity
import com.learnbible.activities.core.VersiculoLearnTextVersiculoActivity
import com.learnbible.model.BotonPalabraLearn
import com.learnbible.utilities.ADAPTERS

class PalabraBotonAdapter(context: Context, data: List<BotonPalabraLearn>) : RecyclerView.Adapter<PalabraBotonAdapter.ViewHolder>() {
    private val mData: List<BotonPalabraLearn>
    private val mInflater: LayoutInflater
    private val context: Context

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.list_item_boton_palabra_versiculo, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val btn = mData[position]
        holder.btPalabra.text = btn.palabra
        holder.btPalabra.tag = btn.pos
        val params = holder.btPalabra.layoutParams
        ADAPTERS.wrapWith(btn.palabra!!, params)
        holder.btPalabra.layoutParams = params
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var btPalabra: Button
        override fun onClick(view: View) {
            val b = view as Button
            if (context is VersiculoLearnVersiculoActivity) {
                context.validarTexto(b)
            } else if (context is VersiculoLearnTextVersiculoActivity) {
                context.validarTexto(b)
            } else if (context is VersiculoLearnPasajeVersiculoActivity) {
                context.validarTexto(b)
            }
        }

        init {
            btPalabra = itemView.findViewById(R.id.btPalabra)
            btPalabra.setOnClickListener(this)
        }
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): BotonPalabraLearn {
        return mData[id]
    }

    // data is passed into the constructor
    init {
        mInflater = LayoutInflater.from(context)
        mData = data
        this.context = context
    }
}