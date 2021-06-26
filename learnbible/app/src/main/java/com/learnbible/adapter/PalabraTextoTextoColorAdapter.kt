package com.learnbible.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.learnbible.R
import com.learnbible.model.Palabra
import com.learnbible.utilities.ADAPTERS

class PalabraTextoTextoColorAdapter(context: Context, data: List<Palabra>) : RecyclerView.Adapter<PalabraTextoTextoColorAdapter.ViewHolder>() {
    private val mData: List<Palabra>
    private val mInflater: LayoutInflater
    private val context: Context

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.list_item_text_palabra_versiculo, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val palabra = mData[position]
        holder.tvPalabraLearn.visibility = View.GONE
        if (palabra.isText) {
            holder.tvPalabraLearn.text = palabra.palabra + " "
            holder.tvPalabraLearn.setTextColor(context.resources.getColor(android.R.color.darker_gray))
            ADAPTERS.setComun(holder.tvPalabraLearn, palabra.palabra, position, false)
        } else {
            holder.tvPalabraLearn.text = palabra.palabra + " "
            holder.tvPalabraLearn.setTextColor(context.resources.getColor(android.R.color.holo_green_light))
            ADAPTERS.setComun(holder.tvPalabraLearn, palabra.palabra, position, false)
        }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvPalabraLearn: TextView

        init {
            tvPalabraLearn = itemView.findViewById(R.id.tvPalabraLearn)
        }
    }

    // data is passed into the constructor
    init {
        mInflater = LayoutInflater.from(context)
        mData = data
        this.context = context
    }
}