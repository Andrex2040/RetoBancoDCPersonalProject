package com.learnbible.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.learnbible.LoginActivity
import com.learnbible.R
import com.learnbible.superactivities.GeneralActivity
import com.learnbible.utilities.CONSTANTES
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.dialog_login_name.view.*


class PerfilActivity : GeneralActivity(), View.OnClickListener {

    private lateinit var firebaseAuth: FirebaseAuth
    private var salirButton: Button? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    private var googleLoginButton: SignInButton? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_perfil)
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) //show back button

        firebaseAuth = FirebaseAuth.getInstance()

        salirButton = findViewById(R.id.salir)


        salirButton!!.setOnClickListener{
            firebaseAuth.signOut()
            iFirebase.delDeviceUsers(CONSTANTES.USER_DEVICE!!)
            
            openActivity(this@PerfilActivity,LoginActivity::class.java,true)
            finishAffinity()
        }


        perfilName!!.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_login_name, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                    .setView(mDialogView)
            //show dialog
            val  mAlertDialog = mBuilder.show()
            //login button click of custom layout
            mDialogView.dialogLoginBtn.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
                //get text from EditTexts of custom layout
                val name = mDialogView.dialogNameEt.text.toString()
                CONSTANTES.USERFS!!.nombre = name
                iFirebase.mergeUsuario()
                perfilName.text = CONSTANTES.USERFS!!.nombre

            }
        }


        //autenticacion google
        googleLoginButton = findViewById(R.id.sign_in_button)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleLoginButton!!.setOnClickListener{ signIn() }


        authStateListener = AuthStateListener { firebaseAuth ->
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        firebaseAuth.addAuthStateListener(authStateListener!!)
        val currentUser = firebaseAuth.currentUser
        updateUI(currentUser)
    }
    // [END on_start_check_user]


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val googleSignInAccount = completedTask.getResult(ApiException::class.java)
            firebaseGoogleAuth(googleSignInAccount)
        } catch (e: ApiException) {
            crash.crash()
            firebaseGoogleAuth(null)
            Log.e("errorAut", e.toString())
        }
    }

    private fun firebaseGoogleAuth(googleSignInAccount: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(googleSignInAccount!!.idToken, null)

        // [START link_credential]
        firebaseAuth.currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        updateUI(user)
                    } else {
                        Log.w(TAG, "linkWithCredential:failure", task.exception)
                        updateUI(null)
                    }
                }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    companion object {
        private const val TAG = "AnonymousAuth"
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            CONSTANTES.USERFS!!.nombre = user.displayName.toString()
            perfilName.text = user.displayName
            iFirebase.mergeUsuario()

            val photo = user.photoUrl.toString()

            //ver si es google o face
            val red = photo.indexOf("google")
            var photoSize = "$photo?type=large"

            if(red != -1){
                photoSize =  photo.replace("s96-c","s360-c")//"$photo?height=5000"
            }

            if(photo.length > 5){
                Glide.with(this).load(photoSize).into(profile_image)
            }
            if(user.isAnonymous){
                sign_in_button.visibility = View.VISIBLE
            }else{
                sign_in_button.visibility = View.GONE
            }
        }
    }

    //iniciar sesion con google
    private fun signIn() {
        val signInIntent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
}