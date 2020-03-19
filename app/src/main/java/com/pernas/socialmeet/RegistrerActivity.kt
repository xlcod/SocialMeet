package com.pernas.socialmeet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_registrer.*

class RegistrerActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
// ...


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrer)


        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        signUpButton.setOnClickListener {
            signUpUser()

        }
    }

    fun signUpUser() {
        if (usernameText.text.isEmpty()) {
            usernameText.error = "Please enter Username"
            usernameText.requestFocus()
            return
        }
        if (emailText.text.isEmpty()) {
            emailText.error = "Please enter Email"
            emailText.requestFocus()
            return
        }
        if (passwordText.text.isEmpty()) {
            passwordText.error = "Please enter Password"
            passwordText.requestFocus()
            return
        }


        auth.createUserWithEmailAndPassword(emailText.text.toString(), passwordText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {

                    Toast.makeText(
                        baseContext, "Sign up failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
