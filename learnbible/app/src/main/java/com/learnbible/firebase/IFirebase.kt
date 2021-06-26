package com.learnbible.firebase

import android.content.Context
import com.learnbible.firebase.dto.DispositivosFS
import com.learnbible.firebase.model.UsuarioModel

class IFirebase {

    private val usuarioModel:UsuarioModel = UsuarioModel()

    fun mergeUsuario(){
        usuarioModel.mergeUsuario()
    }

    fun mergeDeviceUsers(dispositivosFS: DispositivosFS){
        usuarioModel.mergeDeviceUsers(dispositivosFS)
    }

    fun delDeviceUsers(dispositivosFS: DispositivosFS){
        usuarioModel.delDeviceUsers(dispositivosFS)
    }

}