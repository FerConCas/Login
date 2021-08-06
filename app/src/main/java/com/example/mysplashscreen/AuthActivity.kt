package com.example.mysplashscreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_auth.*


class AuthActivity : AppCompatActivity() {
    private val googlesign = 100

    private val callbackmanager = CallbackManager.Factory.create()


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)
        Thread.sleep(2000)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle= Bundle()
        bundle.putString("message", "Integracion Completa")
        analytics.logEvent("initScreen", bundle)

        setup()
        session()

    }

  override fun onStart() {
        super.onStart()
        authLayout.visibility= View.VISIBLE
    }

   private fun  session(){
val prefs=getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email =  prefs.getString("email",null)
        val provider= prefs.getString("provider",null)

        if(email != null && provider != null){
            authLayout.visibility= View.INVISIBLE
            showHome(email, ProviderType.valueOf(provider))

        }

    }

private fun setup(){

    title = "Autenticacion"

    signupButton.setOnClickListener{

        if(emailEditText.text.isNotEmpty() && passEditText.text.isNotEmpty()){

            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(emailEditText.text.toString(),passEditText.text.toString())
                .addOnCompleteListener{
                    if(it.isSuccessful){

                        showHome(it.result?.user?.email?:"", ProviderType.BASIC)
                    }else{ showAlert()
                    }
                }
           }
     }

    loginButton.setOnClickListener{

        if(emailEditText.text.isNotEmpty() && passEditText.text.isNotEmpty()){

            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(emailEditText.text.toString(),passEditText.text.toString())
                .addOnCompleteListener{
                    if(it.isSuccessful){

                        showHome(it.result?.user?.email?:"", ProviderType.BASIC)
                    }else{ showAlert()
                    }
                }
        }
    }

    logingoogle.setOnClickListener{
val googleconf= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestIdToken(getString(R.string.default_web_client_id))
    .requestEmail()
    .build()
        
        val googleClient = GoogleSignIn.getClient(this, googleconf)
        googleClient.signOut()
        startActivityForResult(googleClient.signInIntent,googlesign)

    }

    loginface.setOnClickListener{
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))


        LoginManager.getInstance().registerCallback(callbackmanager,
            object : FacebookCallback<LoginResult> {

            override fun onSuccess(result: LoginResult?) {
                result?.let {
                    val token= it.accessToken
                    val credential = FacebookAuthProvider
                        .getCredential(token.token)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {

                        if (it.isSuccessful) {

                            showHome(it.result?.user?.email ?: "", ProviderType.FACCEBOOK)
                        } else {
                            showAlert()
                        }
                    }

                }
            }
                override fun onCancel() {

                }

            override fun onError(error: FacebookException?) {
                showAlert()

            }
        })
        }


    }


    private fun showAlert(){

        val builder=AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error")
        builder.setPositiveButton("Aceptar", null)
        val dialog:AlertDialog=builder.create()
        dialog.show()

    }
    
    private fun showHome(email:String, provider: ProviderType){
        
val homeIntent:Intent= Intent(this, ActivityHome::class.java).apply {

    putExtra("email", email)
    putExtra("provider", provider.name)

}
        startActivity(homeIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

       callbackmanager.onActivityResult(requestCode, resultCode, data)


        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==googlesign){
            val task= GoogleSignIn.getSignedInAccountFromIntent(data)

           try {


            val account= task.getResult((ApiException::class.java))
            if(account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {

                    if (it.isSuccessful) {

                        showHome(account.email?: "", ProviderType.GOOGLE)
                    } else {
                        showAlert()
                    }
                }}
            }catch(e:ApiException){

           showAlert()
            }

        }
    }

}