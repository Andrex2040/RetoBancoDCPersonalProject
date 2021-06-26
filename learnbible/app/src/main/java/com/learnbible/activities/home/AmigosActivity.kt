package com.learnbible.activities.home

import android.app.Dialog
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.learnbible.R
import com.learnbible.activities.BuscaTuAmigoActivity
import com.learnbible.adapter.AmigosAdapter
import com.learnbible.superactivities.SuperHomeActivity
import com.learnbible.utilities.CONSTANTES


class AmigosActivity : SuperHomeActivity(){

    var amigosAdapter: AmigosAdapter? = null
    var dbFS = FirebaseFirestore.getInstance()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amigos)

        bottomMenu = findViewById(R.id.bottom_navigation_view)
        super.enableMenu(R.id.item3)

        mylocation()
        misAmigos()

        findViewById<FloatingActionButton>(R.id.btBuscarAmigos)
                .setOnClickListener {
                    openActivity(this, BuscaTuAmigoActivity::class.java,false)
                }
    }

    fun misAmigos(){
        amigosAdapter = AmigosAdapter(this, CONSTANTES.listAmigos)
        val lv = findViewById<ListView>(R.id.lv_amigos)
        lv.adapter = amigosAdapter
        lv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        }
    }

    fun mylocation(){
        val locationProviders: String = Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
        if (locationProviders == null || locationProviders.equals("")) {
            mostrarDialogLocation("GPS desactivado",
                    "para una mejor experiencia recomendamos habilitar el GPS la próxima vez.")
        }else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location ->
                        setLocation(location)
                    }
                    .addOnFailureListener { e ->
                        Log.w(CONSTANTES.TAG, "Error fusedLocationClient", e)
                        if (isNormal) {
                            isNormal = false
                            mylocation()
                        } else {
                            crash.crash()
                        }
                    }
        }
    }

    fun setLocation(location: Location?){
        var geolocation = GeoPoint(CONSTANTES.default_latitud, CONSTANTES.default_longitud)
        if (location != null) {
            geolocation = GeoPoint(location.latitude, location.longitude)
        }
        isNormal = true
        CONSTANTES.USERFS!!.localizacion = geolocation
        iFirebase.mergeUsuario()
    }

    open fun mostrarDialogLocation(titulo: String, mensaje: String?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)

        dialog.findViewById<TextView>(R.id.dlgTitulo).text = titulo

        if(mensaje != null) {
            dialog.findViewById<TextView>(R.id.dlgMensaje).text = mensaje
        }else{
            dialog.findViewById<TextView>(R.id.dlgMensaje).visibility = View.GONE
        }

        val dialogButtonYes = dialog.findViewById(R.id.btn_yes) as Button
        dialogButtonYes.text = "OK"
        val dialogButtonNo = dialog.findViewById(R.id.btn_no) as Button
        dialogButtonNo.visibility = View.GONE
        dialogButtonYes.setOnClickListener {
            dialog.hide()
        }
        dialog.show()
    }




    override fun onStart() {
        super.onStart()
        actualizaResumen()
    }

    fun actualizaResumen(){
        invalidateOptionsMenu()
        if(amigosAdapter !=null) {
            amigosAdapter!!.notifyDataSetChanged()
        }
    }


}