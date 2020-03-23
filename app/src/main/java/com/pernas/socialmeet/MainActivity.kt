package com.pernas.socialmeet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_registrer.*


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val button = findViewById<Button>(R.id.registerButton)
        button.setOnClickListener {
            val intent = Intent(this, RegistrerActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            doLogin()
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
    }

    private fun doLogin() {

        if (emailField.text?.isEmpty()!!) {
            emailLogin.error = "Please enter an Email"
            return
        }
        if (passwordField.text?.isEmpty()!!) {
            passwordLogin.error = "Please enter password"
            return
        }

        auth.signInWithEmailAndPassword(emailField.text.toString(), passwordField.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)

                }
            }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {


        if (currentUser != null) {
            startActivity(Intent(this, QuedadasActivity::class.java))

        } else {
            Toast.makeText(
                baseContext, "User not logged, please log in.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}
