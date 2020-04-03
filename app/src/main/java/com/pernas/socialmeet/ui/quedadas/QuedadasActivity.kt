package com.pernas.socialmeet.ui.quedadas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.pernas.socialmeet.ui.login.LoginActivity
import com.pernas.socialmeet.R
import kotlinx.android.synthetic.main.activity_quedadas.*

class QuedadasActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quedadas)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        logOutIcon.setOnClickListener {
            signOut()
        }
        getUserData()

    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun getUserData() {
        /*val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            for (profile in it.providerData) {
                // Id of the provider (ex: google.com)
                val providerId = profile.providerId

                // UID specific to the provider
                val uid = profile.uid

                // Name, email address, and profile photo Url
                val name = profile.displayName
                val email = profile.email
                val photoUrl = profile.photoUrl*/


       /* val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid

            Log.e("uid", "${uid}")
            Log.e("name", "${name}")
            Log.e("email", "${email}")
            Log.e("photoUrl", "${photoUrl}")
        }*/


    }
}


