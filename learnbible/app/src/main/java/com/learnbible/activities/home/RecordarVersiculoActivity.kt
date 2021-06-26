package com.learnbible.activities.home

import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.learnbible.R
import com.learnbible.activities.core.RecordarLearnVozVersiculoActivity
import com.learnbible.adapter.VersiculoRecordarAdapter
import com.learnbible.superactivities.SuperHomeActivity
import com.learnbible.utilities.CONSTANTES


class RecordarVersiculoActivity : SuperHomeActivity() {
    var adapter: VersiculoRecordarAdapter? = null
    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recordar)
        //ads()
        cargarVersiculosUI()
    }

    fun ads(){
        //publicidad
        MobileAds.initialize(this) {}
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.admob_intersticial)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
    }

    fun adsListerter(position:Int){
        mInterstitialAd.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
                // Code to be executed when the interstitial ad is closed.
                openActivityEnviaPos(this@RecordarVersiculoActivity, RecordarLearnVozVersiculoActivity::class.java, position, false)
            }
        }
    }

    fun cargarVersiculosUI() {

        bottomMenu = findViewById(R.id.bottom_navigation_view)
        super.enableMenu(R.id.item1)

        actualizaResumen()
        adapter = VersiculoRecordarAdapter(this@RecordarVersiculoActivity, CONSTANTES.listVersiculosAprendidosApp)
        val lv = findViewById<ListView>(R.id.lv_home_historias)
        lv.adapter = adapter
        lv.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            if(CONSTANTES.listVersiculosAprendidosApp.get(position).estrellas!!.toInt() == 5){
                Toast.makeText(this,"Versiculo Aprendido!!",Toast.LENGTH_SHORT).show()
            }else {
                //carga Ads
//                if (mInterstitialAd.isLoaded) {
//                    mInterstitialAd.show()
//                    adsListerter(position)
//                } else {
//                    openActivityEnviaPos(this@RecordarVersiculoActivity, RecordarLearnVozVersiculoActivity::class.java, position, false)
//                }
                openActivityEnviaPos(this@RecordarVersiculoActivity, RecordarLearnVozVersiculoActivity::class.java, position, false)

            }
        }

        if(CONSTANTES.ISOTHERDAY && !CONSTANTES.listVersiculosAprendidosApp.isEmpty()){
            var isOk = true
            var pos = 0
            for(versiculoAprendido in CONSTANTES.listVersiculosAprendidosApp){
                if(versiculoAprendido.estrellas!!.toInt() < 5){
                    openActivityEnviaPos(this@RecordarVersiculoActivity, RecordarLearnVozVersiculoActivity::class.java, pos, false)
                    isOk = false
                    break
                }
                pos++
            }
            if(isOk){
                CONSTANTES.ISOTHERDAY = false
                Toast.makeText(this,"Todos los versiculo estan Aprendidos!!",Toast.LENGTH_SHORT).show()
                openActivity(this, HomeVersiculoActivity::class.java, true)
            }
        }else{
            CONSTANTES.ISOTHERDAY = false
        }
    }



    override fun onStart() {
        super.onStart()
        actualizaResumen()
    }

    fun actualizaResumen(){
        invalidateOptionsMenu()
        if(adapter !=null) {
            adapter!!.notifyDataSetChanged()
        }
    }
}

