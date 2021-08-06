package com.example.mysplashscreen

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mysplashscreen.ProviderType.FACCEBOOK
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import java.security.Provider


enum class ProviderType {
    BASIC,
   GOOGLE,
    FACCEBOOK
}
class ActivityHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val bundle:Bundle?=intent.extras
        val email:String?= bundle?.getString("email")

        val provider:String?= bundle?.getString("provider")
        setup(email?: "", provider?: "")

        //GUARDADO DE DATOS
       val prefs= getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

    }

    private fun setup(email:String, provider:String){


        title = "inicio"

        emailTextView.text=email
        providerTextView.text=provider

        logoutbutton.setOnClickListener{

            val prefs= getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
           prefs.clear()
           prefs.apply()

if (provider == ProviderType.FACCEBOOK.name){
LoginManager.getInstance().logOut()

}
            
            FirebaseAuth.getInstance().signOut()
            onBackPressed()  //Volver a pantalla anterior
        }
    }
}