package com.learnbible.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.learnbible.R
import com.learnbible.firebase.dto.UsuarioFriendsFS
import com.learnbible.model.HistoriaVersiculo
import com.learnbible.model.Versiculo
import com.learnbible.utilities.CONSTANTES
import de.hdodenhof.circleimageview.CircleImageView


class HistoriaVersiculoAdapter(activity: Activity) : BaseAdapter() {
    class ViewHolder {

        var vdividerUp: View? = null
        var vdividerUp3: View? = null
        //nuevo
        var rlItem: RelativeLayout? = null
        var rlItemInterno: RelativeLayout? = null

        var ivCircleHistoria: ImageView? = null
        var tvLibro: TextView? = null
        var tvPasajeSimple: TextView? = null

        var ivCaminoHistoria: ImageView? = null
        var ivVertical: ImageView? = null
        var pbLearnVersiculo: ProgressBar? = null
        var tvNivel: TextView? = null
        var tvLevel: TextView? = null

        var tvHistoria: TextView? = null
        var vdivider: View? = null
        var vdivider2: View? = null

        var civFriend3: CircleImageView? = null
        var civFriend2: CircleImageView? = null
        var civFriend1: CircleImageView? = null

    }

    private val activity: Activity
    private var posHistoria: Int = 1
    private var inflater: LayoutInflater? = null
    override fun getCount(): Int {
        return CONSTANTES.listHistoriasVersiculoApp.size
    }

    override fun getItem(position: Int): Any {
        return CONSTANTES.listHistoriasVersiculoApp[position]
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

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, cv: View?, parent: ViewGroup): View {
        var convertView = cv
        val holder: ViewHolder
        if (convertView == null) {
            convertView = inflater!!.inflate(R.layout.list_item_historias_versiculos, null)
            convertViewCounter++
            holder = ViewHolder()

            holder.vdividerUp = convertView.findViewById(R.id.vdividerUp)
            holder.vdividerUp3 = convertView.findViewById(R.id.vdividerUp3)

            holder.rlItem = convertView.findViewById(R.id.rlItem)
            holder.rlItemInterno = convertView.findViewById(R.id.rlItemInterno)
            holder.ivCircleHistoria = convertView.findViewById(R.id.ivCircleHistoria)

            holder.tvLibro = convertView.findViewById(R.id.tvLibro)
            holder.tvPasajeSimple = convertView.findViewById(R.id.tvPasajeSimple)

            holder.ivCaminoHistoria = convertView.findViewById(R.id.ivCaminoHistoria)
            holder.ivVertical = convertView.findViewById(R.id.ivVertical)

            holder.tvHistoria = convertView.findViewById(R.id.tvHistoria)
            holder.pbLearnVersiculo = convertView.findViewById(R.id.pbLearnVersiculo)
            holder.tvNivel = convertView.findViewById(R.id.tvNivel)
            holder.tvLevel = convertView.findViewById(R.id.tvLevel)

            holder.vdivider = convertView.findViewById(R.id.vdivider)
            holder.vdivider2 = convertView.findViewById(R.id.vdivider2)

            holder.civFriend1 = convertView.findViewById(R.id.civFriend1)
            holder.civFriend2 = convertView.findViewById(R.id.civFriend2)
            holder.civFriend3 = convertView.findViewById(R.id.civFriend3)

            convertView.tag = holder
        } else holder = convertView.tag as ViewHolder


        // Para porde hacer click en el checkbox
        val historiaVersiculo = getItem(position) as HistoriaVersiculo

        holder.vdividerUp!!.visibility = View.GONE
        holder.vdividerUp3!!.visibility = View.GONE

        holder.ivCircleHistoria!!.visibility = View.GONE

        holder.tvLibro!!.visibility = View.GONE
        holder.tvPasajeSimple!!.visibility = View.GONE

        holder.ivVertical!!.visibility = View.GONE
        holder.ivCaminoHistoria!!.visibility = View.GONE
        holder.tvHistoria!!.visibility = View.GONE

        holder.pbLearnVersiculo!!.visibility = View.GONE
        holder.tvNivel!!.visibility = View.GONE
        holder.tvLevel!!.visibility = View.GONE

        holder.vdivider!!.visibility = View.GONE
        holder.vdivider2!!.visibility = View.GONE

        holder.civFriend1!!.visibility = View.INVISIBLE
        holder.civFriend2!!.visibility = View.INVISIBLE
        holder.civFriend3!!.visibility = View.INVISIBLE


        if(historiaVersiculo.isVersiculo){

            holder.tvLibro!!.visibility = View.VISIBLE
            holder.tvPasajeSimple!!.visibility = View.VISIBLE
            holder.ivVertical!!.visibility = View.VISIBLE
            holder.ivCaminoHistoria!!.visibility = View.VISIBLE
            holder.tvHistoria!!.visibility = View.VISIBLE

            holder.tvHistoria!!.tag = historiaVersiculo.historia.id + historiaVersiculo.versiculo!!.id

            holder.tvLibro!!.text = historiaVersiculo.versiculo!!.libro
            holder.tvPasajeSimple!!.text = historiaVersiculo.versiculo!!.pasajeSimple

            holder.tvHistoria!!.text = historiaVersiculo.versiculo!!.titulo

            val layoutParams = holder.rlItem!!.layoutParams
            layoutParams.width = convertView!!.resources.getDimension(R.dimen.dimen_120).toInt()
            holder.rlItem!!.layoutParams = layoutParams
            val layoutParamsInterno = holder.rlItemInterno!!.layoutParams
            layoutParamsInterno.width = convertView.resources.getDimension(R.dimen.dimen_140).toInt()
            holder.rlItemInterno!!.layoutParams = layoutParamsInterno
            if(historiaVersiculo.versiculo!!.rondasMax == historiaVersiculo.versiculo!!.nivelActual) {
                holder.ivVertical!!.setImageResource(R.drawable.img_vertical)
                holder.ivCaminoHistoria!!.setImageResource(R.drawable.img_ok)
            }else if((historiaVersiculo.versiculo!!.rondasMax > historiaVersiculo.versiculo!!.nivelActual
                            && historiaVersiculo.versiculo!!.nivelActual > 0) || historiaVersiculo.versiculo!!.isActivo){
                holder.ivVertical!!.visibility = View.GONE
                holder.ivCaminoHistoria!!.setImageResource(R.drawable.img_process)

                if(historiaVersiculo.versiculo!!.nivelActual > 0) {
                    holder.ivCaminoHistoria!!.visibility = View.INVISIBLE
                    holder.pbLearnVersiculo!!.visibility = View.VISIBLE
                    holder.pbLearnVersiculo!!.max = historiaVersiculo.versiculo!!.rondasMax
                    holder.pbLearnVersiculo!!.progress = historiaVersiculo.versiculo!!.nivelActual
                    holder.pbLearnVersiculo!!.secondaryProgress = historiaVersiculo.versiculo!!.rondasMax

                    holder.tvNivel!!.visibility = View.VISIBLE
                    holder.tvLevel!!.visibility = View.VISIBLE
                    holder.tvLevel!!.text = historiaVersiculo.versiculo!!.nivelActual.toString()
                }


            }else{
                holder.ivVertical!!.setImageResource(R.drawable.img_vertical_disabled)
                holder.ivCaminoHistoria!!.setImageResource(R.drawable.img_candado_gris)
            }

            mostrarAmigos(holder, historiaVersiculo.versiculo!!)

        }else{

            holder.tvHistoria!!.tag = historiaVersiculo.historia.id
            // Setting all values in listview
            posHistoria++

            holder.vdividerUp!!.visibility = View.VISIBLE
            holder.vdividerUp3!!.visibility = View.VISIBLE
            holder.ivCircleHistoria!!.visibility = View.VISIBLE
            holder.tvHistoria!!.visibility = View.VISIBLE
            holder.vdivider!!.visibility = View.VISIBLE
            holder.vdivider2!!.visibility = View.VISIBLE

            holder.tvHistoria!!.text = historiaVersiculo.historia.titulo
            val layoutParams = holder.rlItem!!.layoutParams
            layoutParams.width = convertView!!.resources.getDimension(R.dimen.dimen_60).toInt()
            holder.rlItem!!.layoutParams = layoutParams
            val layoutParamsInterno = holder.rlItemInterno!!.layoutParams
            layoutParamsInterno.width = convertView.resources.getDimension(R.dimen.dimen_60).toInt()
            holder.rlItemInterno!!.layoutParams = layoutParamsInterno
        }

        return convertView
    }

    fun mostrarAmigos(holder: ViewHolder, versiculo: Versiculo){

        var listAmigosVersiculo: MutableList<UsuarioFriendsFS> = ArrayList()
        for(friend in CONSTANTES.listAmigos){
            if(friend.lastVersiculo.equals(versiculo.id)){
                listAmigosVersiculo.add(friend)
            }
        }

        when(listAmigosVersiculo.size) {
            0 -> {true
            }
            1 -> {
                holder.civFriend1!!.visibility = View.VISIBLE
                CONSTANTES.cambiarImagenPerfile(listAmigosVersiculo.get(0).photoUrl, holder.civFriend1!!, activity)
                true
            }
            2 -> {
                holder.civFriend1!!.visibility = View.VISIBLE
                holder.civFriend2!!.visibility = View.VISIBLE
                CONSTANTES.cambiarImagenPerfile(listAmigosVersiculo.get(0).photoUrl, holder.civFriend1!!, activity)
                CONSTANTES.cambiarImagenPerfile(listAmigosVersiculo.get(1).photoUrl, holder.civFriend2!!, activity)
                true
            }
            3 -> {
                holder.civFriend1!!.visibility = View.VISIBLE
                holder.civFriend2!!.visibility = View.VISIBLE
                holder.civFriend3!!.visibility = View.VISIBLE
                CONSTANTES.cambiarImagenPerfile(listAmigosVersiculo.get(0).photoUrl, holder.civFriend1!!, activity)
                CONSTANTES.cambiarImagenPerfile(listAmigosVersiculo.get(1).photoUrl, holder.civFriend2!!, activity)
                CONSTANTES.cambiarImagenPerfile(listAmigosVersiculo.get(2).photoUrl, holder.civFriend3!!, activity)
                true
            }
            else -> {
                holder.civFriend1!!.visibility = View.VISIBLE
                holder.civFriend2!!.visibility = View.VISIBLE
                holder.civFriend3!!.visibility = View.VISIBLE
                CONSTANTES.cambiarImagenPerfile(listAmigosVersiculo.get(0).photoUrl, holder.civFriend1!!, activity)
                CONSTANTES.cambiarImagenPerfile(listAmigosVersiculo.get(1).photoUrl, holder.civFriend2!!, activity)
                CONSTANTES.cambiarImagenPerfile(listAmigosVersiculo.get(2).photoUrl, holder.civFriend3!!, activity)
                true
            }
        }
    }

    companion object {
        private var convertViewCounter = 0
    }

    init {
        this.activity = activity
        inflater = LayoutInflater.from(activity)
    }
}