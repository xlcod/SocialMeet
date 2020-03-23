package com.pernas.socialmeet

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_registrer.*
import java.util.jar.Manifest

class RegistrerActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrer)


        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        signUpButton.setOnClickListener {
            signUpUser()
        }
        profileImageView.setOnClickListener {
            if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //denied
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISION_CODE);
                } else {
                    //granted
                    pickImageFromGallery();
                }

            } else {
                //system os < marshmallow
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)

    }

    companion object {
        //image picker
        private val IMAGE_PICK_CODE = 1000;
        //Permision code
        private val PERMISION_CODE = 1001;
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(this, "Permission DENIED ", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            profileImageView.setImageURI(data?.data)
        }
    }

    fun signUpUser() {
        if (usernameTextRegister.text?.isEmpty()!!) {
            usernameTextRegister.error = "Please enter Username"
            usernameTextRegister.requestFocus()
            return
        }
        if (emailTextRegister.text?.isEmpty()!!) {
            emailTextRegister.error = "Please enter Email"
            emailTextRegister.requestFocus()
            return
        }
        if (passwordTextRegister.text?.isEmpty()!!) {
            passwordTextRegister.error = "Please enter Password"
            passwordTextRegister.requestFocus()
            return
        }


        auth.createUserWithEmailAndPassword(emailTextRegister.text.toString(), passwordTextRegister.text.toString())
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
