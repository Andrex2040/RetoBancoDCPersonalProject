package com.learnbible.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RatingBar
import android.widget.TextView
import com.learnbible.R
import com.learnbible.firebase.dto.VersiculosAprendidoFS

class VersiculoRecordarAdapter(c: Context?, d: List<VersiculosAprendidoFS>) : BaseAdapter() {
    internal class ViewHolder {
        var tvVersiculo: TextView? = null
        var rbStar: RatingBar? = null
    }

    private val data: List<VersiculosAprendidoFS>
    private var inflater: LayoutInflater? = null
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun getView(position: Int, cv: View?, parent: ViewGroup): View {
        var convertView = cv
        val holder: ViewHolder
        if (convertView == null) {
            convertView = inflater!!.inflate(R.layout.list_item_versiculos_aprendidos, null)
            convertViewCounter++
            holder = ViewHolder()
            holder.tvVersiculo = convertView.findViewById(R.id.tvVersiculo)
            holder.rbStar = convertView.findViewById(R.id.rbStar)
            convertView.tag = holder
        } else holder = convertView.tag as ViewHolder

        // Para porde hacer click en el checkbox
        val versiculo = getItem(position) as VersiculosAprendidoFS
        holder.tvVersiculo!!.tag = versiculo.id
        // Setting all values in listview
        holder.tvVersiculo!!.text = data[position].pasaje
        holder.rbStar!!.rating = versiculo.estrellas!!.toFloat()

        return convertView!!
    }

    companion object {
        private var convertViewCounter = 0
    }

    init {
        data = d
        inflater = LayoutInflater.from(c)
    }
}