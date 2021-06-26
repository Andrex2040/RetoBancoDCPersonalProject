package com.learnbible

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.learnbible.firebase.dto.DispositivosFS
import com.learnbible.firebase.dto.UsuarioFS
import com.learnbible.superactivities.GeneralActivity
import com.learnbible.utilities.CONSTANTES
import kotlinx.android.synthetic.main.dialog_login_name.view.*
import java.util.*

class LoginActivity : GeneralActivity() {

    var dbFS = FirebaseFirestore.getInstance()
    private var authStateListener: AuthStateListener? = null
    private var textProfileName: TextView? = null

    //variables autenticacion con google
    private val signInButton: SignInButton? = null
    private var googleLoginButton: SignInButton? = null
    private var googleSignInClient: GoogleSignInClient? = null

    //private Button  googleLoginButton;
    private val TAG = "LoginActivity"
    private val RC_SIGN_IN = 1

    //variables anonimo
    private var anonimoButton: Button? = null

    private var isNew: Boolean = false

    //variables de firebase
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseAuth = FirebaseAuth.getInstance()

        textProfileName = findViewById(R.id.profile_name)
        authStateListener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                updateUI(user)
            }
        }


        //autenticacion google
        googleLoginButton = findViewById(R.id.sign_in_button)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleLoginButton!!.setOnClickListener{signIn()}

        //autenticacion anonima
        anonimoButton = findViewById(R.id.anonimo_button)
        anonimoButton!!.setOnClickListener{signInAnonymously()}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val googleSignInAccount = completedTask.getResult(ApiException::class.java)
            firebaseGoogleAuth(googleSignInAccount)
        } catch (e: ApiException) {
            Toast.makeText(this, "Ocurrio un error$e", Toast.LENGTH_SHORT).show()
            Log.e("errorAut", e.toString())
            //crash.crash()
        }catch (e: Exception) {
            Toast.makeText(this, "Ocurrio un error$e", Toast.LENGTH_SHORT).show()
            Log.e("errorAut", e.toString())
            crash.crash()
        }
    }

    private fun firebaseGoogleAuth(googleSignInAccount: GoogleSignInAccount?) {

        val credential = GoogleAuthProvider.getCredential(googleSignInAccount!!.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            CONSTANTES.USER = task.result?.user
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "firebaseGoogleAuth:success")
            }else{
                crash.crash()
                Log.d(TAG, "firebaseGoogleAuth:failed")
            }
        }

    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            textProfileName!!.text = user.displayName

            CONSTANTES.USER = user

            if(isNew) {
                consultaUsuario()
            }else{
                openActivity(this@LoginActivity, MainVersiculoActivity::class.java, true)
            }
        }
    }

    /*
    * Consulta usuario
    * Consulta Token
    * hace merge
    * abre activities
    * */
    fun consultaUsuario(){

        val documentUserId = "/usuarios/"+CONSTANTES.USER?.uid
        val userRef = dbFS.document(documentUserId)
        //consulta el usuario
        userRef.get()
                .addOnSuccessListener { document ->
                    CONSTANTES.USERFS = document.toObject(UsuarioFS::class.java)
                    //valida usuario y si no existe lo crea
                    if (CONSTANTES.USERFS == null) {
                        CONSTANTES.USERFS = UsuarioFS(0,
                                Calendar.getInstance().timeInMillis,
                                Calendar.getInstance().time,
                                0,
                                null,
                                "",
                                CONSTANTES.USER!!.photoUrl.toString())

                        CONSTANTES.USERFS!!.uid = document.id

                        //Obtener token para las notificaciones
                        obtenerTokenNotificaciones()

                    }else{
                        //Obtener token para las notificaciones
                        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.w(TAG, "getInstanceId failed", task.exception)
                                return@OnCompleteListener
                            }

                            // Get new Instance ID token
                            val token = task.result?.token
                            CONSTANTES.USER_DEVICE = DispositivosFS(token)
                            //agrega device
                            iFirebase.mergeDeviceUsers(CONSTANTES.USER_DEVICE!!)
                        })

                        CONSTANTES.USERFS!!.uid = document.id
                        openActivity(this@LoginActivity, MainVersiculoActivity::class.java, true)
                    }
                }
                .addOnFailureListener { e -> Log.w(CONSTANTES.TAG, "Error writing document", e) }
    }

    //Obtener token para las notificaciones
    fun obtenerTokenNotificaciones(){
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "getInstanceId failed", task.exception)
                return@OnCompleteListener
            }

            // Get new Instance ID token
            val token = task.result?.token
            CONSTANTES.USER_DEVICE = DispositivosFS(token)
            //agrega device
            iFirebase.mergeDeviceUsers(CONSTANTES.USER_DEVICE!!)

            if(CONSTANTES.USER!!.displayName != null){
                CONSTANTES.USERFS!!.nombre = CONSTANTES.USER!!.displayName.toString()
                iFirebase.mergeUsuario()
                openActivity(this@LoginActivity, MainVersiculoActivity::class.java, true)
            }else{
                //Inflate the dialog with custom view
                val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_login_name, null)
                //AlertDialogBuilder
                val mBuilder = AlertDialog.Builder(this)
                        .setView(mDialogView)
                        .setTitle("Solo nos falta tu nombre!!!")
                //show dialog
                val  mAlertDialog = mBuilder.show()
                mAlertDialog.setCancelable(false)
                //login button click of custom layout
                mDialogView.dialogLoginBtn.setOnClickListener {
                    //dismiss dialog
                    mAlertDialog.dismiss()
                    //get text from EditTexts of custom layout
                    val name = mDialogView.dialogNameEt.text.toString()
                    CONSTANTES.USERFS!!.nombre = name
                    iFirebase.mergeUsuario()
                    openActivity(this@LoginActivity, MainVersiculoActivity::class.java, true)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(authStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener!!)
        }
    }

    //iniciar sesion con google
    private fun signIn() {
        isNew = true
        val signInIntent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //iniciar sesion anonimo
    private fun signInAnonymously() {
        isNew = true
        firebaseAuth.signInAnonymously().addOnCompleteListener(this) { task ->

            CONSTANTES.USER = task.result?.user
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInAnonymously:success")
            } else {
                crash.crash()
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInAnonymously:failure", task.exception)
                Toast.makeText(this@LoginActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }


}
