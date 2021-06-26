package com.learnbible.superactivities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.learnbible.Permisos
import com.learnbible.R
import com.learnbible.firebase.IFirebase


open class GeneralActivity : AppCompatActivity() {

    val permisos = Permisos()
    val iFirebase = IFirebase()

    var isNormal = true
    val crash = Crashlytics.getInstance()

    open fun mostrarDialog(titulo: String, mensaje: String?) {
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
        val dialogButtonNo = dialog.findViewById(R.id.btn_no) as Button
        dialogButtonYes.setOnClickListener { finish() }
        dialogButtonNo.setOnClickListener { dialog.cancel() }
        dialog.show()
    }



    fun openActivity(cActual: Context, clase: Class<*>?, isFinish: Boolean) {
        val i = Intent(cActual, clase)
        cActual.startActivity(i)
        if (isFinish) (cActual as Activity).finish()
    }

    fun openActivityRightLeft(cActual: Context, clase: Class<*>?, isFinish: Boolean) {
        val i = Intent(cActual, clase)
        cActual.startActivity(i)
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        if (isFinish) (cActual as Activity).finish()
    }

    fun openActivityMenu(cActual: Context, clase: Class<*>?, isFinish: Boolean, selectedItemId: Int, itemId: Int) {
        val i = Intent(cActual, clase)
        cActual.startActivity(i)
        if(selectedItemId == R.id.item1)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        else if(selectedItemId == R.id.item3)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        else if(selectedItemId == R.id.item2 && itemId == R.id.item3)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        else
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)

        if (isFinish) (cActual as Activity).finish()
    }

    fun openActivityEnviaPos(cActual: Context, clase: Class<*>?, pos: Int, isFinish: Boolean) {
        val i = Intent(cActual, clase)
        i.putExtra("pos", pos)
        cActual.startActivity(i)
        if (isFinish) (cActual as Activity).finish()
    }


    fun openActivityRightLeftPos(cActual: Context, clase: Class<*>?, pos: Int, isFinish: Boolean) {
        val i = Intent(cActual, clase)
        i.putExtra("pos", pos)
        cActual.startActivity(i)
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        if (isFinish) (cActual as Activity).finish()
    }
    fun openActivityLeftRightPos(cActual: Context, clase: Class<*>?, pos: Int, isFinish: Boolean) {
        val i = Intent(cActual, clase)
        i.putExtra("pos", pos)
        cActual.startActivity(i)
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        if (isFinish) (cActual as Activity).finish()
    }
}