package com.learnbible.superactivities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.learnbible.R
import com.learnbible.activities.PerfilActivity
import com.learnbible.activities.home.AmigosActivity
import com.learnbible.activities.home.HomeVersiculoActivity
import com.learnbible.activities.home.RecordarVersiculoActivity
import com.learnbible.utilities.CONSTANTES
import de.hdodenhof.circleimageview.CircleImageView


open class SuperHomeActivity : GeneralActivity() {

    var bottomMenu : BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Enable Permisos de Localizacion
        permisos.enablePermisoLocation(this)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu items for use in the action bar
        menuInflater.inflate(R.menu.menu_actionbar, menu)

        val item = menu!!.findItem(R.id.item_actionbar)

        item.setActionView(R.layout.actionlayout)
        val notifCount = item.actionView as RelativeLayout

        val abTvXP = notifCount.findViewById<View>(R.id.ab_tvXP) as TextView
        abTvXP.text = CONSTANTES.USERFS!!.xp.toString()

        val abTvCorona = notifCount.findViewById<View>(R.id.ab_tvCorona) as TextView
        abTvCorona.text = CONSTANTES.USERFS!!.coin.toString()

        val abIvProfile = notifCount.findViewById<View>(R.id.ab_ivProfiles) as CircleImageView
        CONSTANTES.cambiarImagenPerfile(CONSTANTES.USER!!.photoUrl.toString(), abIvProfile, this)
        abIvProfile.setOnClickListener({
            openActivityRightLeft(this, PerfilActivity::class.java, false)
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        return when (item.itemId) {
            R.id.item_actionbar -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    override fun onBackPressed() {
        super.mostrarDialog("¿Deseas salir de la aplicación?", null)
    }

    fun enableMenu(item:Int){

        bottomMenu!!.selectedItemId = item
        bottomMenu!!.setOnNavigationItemSelectedListener {item ->
            when(item.itemId) {
                R.id.item1 -> {
                    openActivityMenu(this, RecordarVersiculoActivity::class.java, true, bottomMenu!!.selectedItemId, item.itemId)
                    true
                }
                R.id.item2 -> {
                    openActivityMenu(this, HomeVersiculoActivity::class.java, true, bottomMenu!!.selectedItemId, item.itemId)
                    true
                }
                R.id.item3 -> {
                    openActivityMenu(this, AmigosActivity::class.java, true, bottomMenu!!.selectedItemId, item.itemId)
                    true
                }
                else -> false
            }
        }
    }
}