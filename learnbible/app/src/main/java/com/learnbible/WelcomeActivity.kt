package com.learnbible

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.learnbible.superactivities.GeneralActivity
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : GeneralActivity() {

    var index=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val defaultValue = resources.getInteger(R.integer.welcome)
        val welcomeInt = sharedPref.getInt("welcome", defaultValue)

        if(welcomeInt == 1){
            openActivityRightLeftPos(this, LoginActivity::class.java, index, true)
        }

        if(intent.getSerializableExtra("pos") != null){
            index = intent.getSerializableExtra("pos") as Int
        }

        btSaltar.visibility = View.INVISIBLE
        when(index) {
            0 -> {
                ivStep.setImageResource(R.drawable.ic_launcher)
                btStepBack.visibility = View.INVISIBLE
                tvStep.text = "Bienvenido a Versiculos aqui podrás..."
            }
            1 -> {
                ivStep.setImageResource(R.drawable.img_versiculo)
                tvStep.text = "Escuchar la palabra"
            }
            2 -> {
                ivStep.setImageResource(R.drawable.icon_plus_friends)
                tvStep.text = "Amigos cercanos (dar permitir para poder habilitar esta funcionalidad)"
            }
            3 -> {
                ivStep.setImageResource(R.drawable.img_hablar)
                tvStep.text = "Hablar (dar permitir para poder habilitar esta funcionalidad)"
            }
            4 -> {
                ivStep.setImageResource(R.drawable.img_candado_gris)
                btStepNext.text = "Finalizar"
            }
        }

        btStepNext.setOnClickListener {
            index++

            when(index) {
                0 -> {
                    openActivityRightLeftPos(this, WelcomeActivity::class.java, index, true)
                }
                1 -> {
                    openActivityRightLeftPos(this, WelcomeActivity::class.java, index, true)
                }
                2 -> {
                    openActivityRightLeftPos(this, WelcomeActivity::class.java, index, true)
                }
                3 -> {
                    permisos.enablePermisoLocation(this)
                }
                4 -> {
                    permisos.enablePermisoAudio(this)
                }
                5 -> {
                    with(sharedPref.edit()) {
                        putInt("welcome", 1)
                        commit()
                    }
                    openActivityRightLeft(this, LoginActivity::class.java, true)
                }
            }
        }
        btStepBack.setOnClickListener {
            index--
            openActivityLeftRightPos(this, WelcomeActivity::class.java, index, true)
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            permisos.RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    index--
                    openActivityRightLeftPos(this, WelcomeActivity::class.java, index, true)
                } else {
                    openActivityRightLeftPos(this, WelcomeActivity::class.java, index, true)
                }
            }
        }
    }
    
}