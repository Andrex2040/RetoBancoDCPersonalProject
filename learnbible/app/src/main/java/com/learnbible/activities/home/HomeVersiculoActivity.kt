package com.learnbible.activities.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import com.learnbible.R
import com.learnbible.activities.core.VersiculoLearnVersiculoActivity
import com.learnbible.adapter.HistoriaVersiculoAdapter
import com.learnbible.model.HistoriaVersiculo
import com.learnbible.model.Versiculo
import com.learnbible.superactivities.SuperHomeActivity
import com.learnbible.utilities.CONSTANTES
import kotlin.collections.ArrayList


class HomeVersiculoActivity : SuperHomeActivity() {
    var adapter: HistoriaVersiculoAdapter? = null
    var lv: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if(CONSTANTES.ISOTHERDAY){
            openActivity(this, RecordarVersiculoActivity::class.java, true)
        }

        cargarVersiculosUI()
    }

    fun cargarVersiculosUI() {
        bottomMenu = findViewById(R.id.bottom_navigation_view)
        super.enableMenu(R.id.item2)

        actualizaResumen()

        refreshLista()
        adapter = HistoriaVersiculoAdapter(this@HomeVersiculoActivity)

        lv = findViewById<ListView>(R.id.lv_home_historias)
        lv!!.adapter = adapter
        lv!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            
            var historiaVersiculo = CONSTANTES.listHistoriasVersiculoApp.get(position)
            if (historiaVersiculo.isVersiculo) {
                if(historiaVersiculo.versiculo!!.rondasMax == historiaVersiculo.versiculo!!.nivelActual){
                    Toast.makeText(this, "Versiculo aprendido!!!", Toast.LENGTH_SHORT).show()
                }else if(historiaVersiculo.versiculo!!.nivelActual == 0 && !historiaVersiculo.versiculo!!.isActivo){
                    Toast.makeText(this, "Versiculo bloqueado!!!", Toast.LENGTH_SHORT).show()
                }else {
                    //aumenta el nivel en uno para que se haga el nivel completo
                    val levelLearn = historiaVersiculo.versiculo!!.nivelActual + 1
                    openActivityEnviaVersiculoProgress(this, VersiculoLearnVersiculoActivity::class.java, historiaVersiculo.versiculo, 0, 0, levelLearn, false)
                }
            } else {
                Toast.makeText(this,"Acción no valida, Selecciona un versiculo", Toast.LENGTH_SHORT).show()
            }
        }


    }

    fun scrolldown(){
        val c: View = lv!!.getChildAt(0)
        val scrolly: Int = -c.getTop() + lv!!.getFirstVisiblePosition() * c.getHeight()
        lv!!.scrollY = scrolly
    }


    fun refreshLista() {
        CONSTANTES.listHistoriasVersiculoApp = ArrayList()

        var isNew = true
        for (historia in CONSTANTES.listHistoriasApp) {

            var historiaVersiculo = HistoriaVersiculo(null, historia)
            historiaVersiculo.isVersiculo = false

            CONSTANTES.listHistoriasVersiculoApp.add(historiaVersiculo)

            for (vers in historia.listVericulos) {
                var historiaVersiculo = HistoriaVersiculo(vers, historia)
                historiaVersiculo.isVersiculo = true
                CONSTANTES.listHistoriasVersiculoApp.add(historiaVersiculo)

                vers.isActivo = false
                //identificar si hay uno iniciado para habilitarlo
                if (vers.nivelActual > 0 && vers.nivelActual < vers.rondasMax) {
                    vers.isActivo = true
                    isNew = false
                }
            }
        }

        //enable versiculo

        if (isNew) {
            for (historiaVersiculo in CONSTANTES.listHistoriasVersiculoApp) {
                if(historiaVersiculo.isVersiculo && historiaVersiculo.versiculo!!.rondasMax > historiaVersiculo.versiculo!!.nivelActual) {
                    historiaVersiculo.versiculo!!.isActivo = true
                    break
                }
            }
        }
    }

    fun openActivityEnviaVersiculoProgress(cActual: Context, clase: Class<*>?, versiculo: Versiculo?, progressLearn: Int, progressLearnOld: Int, levelLearn: Int, isFinish: Boolean) {
        val i = Intent(cActual, clase)
        i.putExtra("versiculo", versiculo)
        i.putExtra("progressLearn", progressLearn)
        i.putExtra("progressLearnOld", progressLearnOld)
        i.putExtra("levelLearn", levelLearn)
        cActual.startActivity(i)
        if (isFinish) (cActual as Activity).finish()
    }




    override fun onStart() {
        super.onStart()
        actualizaResumen()
    }

    fun actualizaResumen(){
        invalidateOptionsMenu()
        refreshLista()
        if(adapter !=null) {
            adapter!!.notifyDataSetChanged()
        }
    }

}

