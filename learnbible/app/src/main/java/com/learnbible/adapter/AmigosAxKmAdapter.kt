package com.learnbible.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.learnbible.R
import com.learnbible.firebase.dto.UsuarioFriendsFS
import com.learnbible.utilities.CONSTANTES

class AmigosAxKmAdapter(activity: Activity, d: List<UsuarioFriendsFS>) : BaseAdapter() {
    internal class ViewHolder {
        var ciProfile: ImageView? = null
        var tvAmigos: TextView? = null
        var tvXp: TextView? = null
        var tvKm: TextView? = null
        var btAgregarAmigo: Button? = null
    }

    private val activity: Activity
    private val data: List<UsuarioFriendsFS>
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
            convertView = inflater!!.inflate(R.layout.list_item_amigos, null)
            convertViewCounter++
            holder = ViewHolder()
            holder.ciProfile = convertView.findViewById(R.id.ciProfile)
            holder.tvAmigos = convertView.findViewById(R.id.tvAmigos)
            holder.tvXp = convertView.findViewById(R.id.tvXp)
            holder.tvKm = convertView.findViewById(R.id.tvKm)
            holder.btAgregarAmigo = convertView.findViewById(R.id.btAgregarAmigo)
            convertView.tag = holder
        } else holder = convertView.tag as ViewHolder

        // Para porde hacer click en el checkbox
        val usuarioFriendsFS = getItem(position) as UsuarioFriendsFS
        holder.tvAmigos!!.tag = usuarioFriendsFS.uid
        // Setting all values in listview

        var nombre = "Anónimo"
        if(!(usuarioFriendsFS.nombre.equals("") || usuarioFriendsFS.nombre.equals(" "))){
            nombre = usuarioFriendsFS.nombre
        }

        val urlImg = usuarioFriendsFS.photoUrl
        CONSTANTES.cambiarImagenPerfile(urlImg, holder.ciProfile, activity)

        holder.tvAmigos!!.text = nombre
        holder.tvXp!!.text = usuarioFriendsFS.xp.toString()
        holder.tvKm!!.text = "a " + CONSTANTES.cercaKm(usuarioFriendsFS.localizacion).toString()+" km"

        if(usuarioFriendsFS.iEnvie!!) {
            holder.btAgregarAmigo!!.text = "Enviado"
            holder.btAgregarAmigo!!.isEnabled = false
        }else{
            holder.btAgregarAmigo!!.isEnabled = true
        }

        holder.btAgregarAmigo!!.setOnClickListener{

            val dbFS = FirebaseFirestore.getInstance()

            usuarioFriendsFS.iEnvie = true
            val otherUserFriendsFS = CONSTANTES.toUserToFriend(CONSTANTES.USERFS!!, usuarioFriendsFS.isAmigo!!, false)

            dbFS.document("/usuarios/"+CONSTANTES.USER!!.uid+"/amigos/"+usuarioFriendsFS.uid)
                    .set(usuarioFriendsFS)
                    .addOnSuccessListener {
                        Log.d(CONSTANTES.TAG, "Crea Usuario successfully written!")
                        dbFS.document("/usuarios/"+usuarioFriendsFS.uid+"/amigos/"+otherUserFriendsFS.uid)
                                .set(otherUserFriendsFS)
                                .addOnSuccessListener {
                                    Log.d(CONSTANTES.TAG, "Crea Amigo successfully written!")
                                    holder.btAgregarAmigo!!.text = "Enviado"
                                    holder.btAgregarAmigo!!.isEnabled = false
                                    CONSTANTES.listAmigos.add(usuarioFriendsFS)
                                }
                                .addOnFailureListener { e ->
                                    Log.w(CONSTANTES.TAG, "Error writing document", e)
                                }
                    }
                    .addOnFailureListener { e ->
                        Log.w(CONSTANTES.TAG, "Error writing document", e)
                    }

        }

        return convertView!!
    }

    companion object {
        private var convertViewCounter = 0
    }

    init {
        data = d
        this.activity = activity
        inflater = LayoutInflater.from(activity)
    }
}