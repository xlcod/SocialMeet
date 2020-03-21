package com.pernas.socialmeet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
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
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }
}
