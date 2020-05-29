package com.pernas.socialmeet.ui.register

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Build.VERSION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pernas.socialmeet.R
import com.pernas.socialmeet.ui.data.remote.RemoteRepoCalls
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import com.pernas.socialmeet.ui.quedadas.QuedadasActivity
import kotlinx.android.synthetic.main.activity_registrer.*
import java.io.ByteArrayOutputStream

class RegistrerActivity : AppCompatActivity(), RegisterView {
    private lateinit var auth: FirebaseAuth
    lateinit var presenter: RegisterPresenter
    private lateinit var filePath: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrer)

        val remoteRepository: RemoteRepository = RemoteRepoCalls()
        presenter = RegisterPresenter(this, remoteRepository)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        signUpButton.setOnClickListener {
            checkFields()
            presenter.signUpClicked(
                newpass.text.toString(),
                passwordTextRegister.text.toString(),
                oldpass.text.toString(),
                filePath
            )
        }
        profileImageView.setOnClickListener {
            if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //denied
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request runtime permission
                    requestPermissions(
                        permissions,
                        PERMISION_CODE
                    )
                } else {
                    //granted
                    pickImageFromGallery()
                }

            } else {
                //system os < marshmallow
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(
            intent,
            IMAGE_PICK_CODE
        )

    }

    companion object {
        //image picker
        private val IMAGE_PICK_CODE = 1000
        //Permision code
        private val PERMISION_CODE = 1001
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
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "Permission DENIED ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            profileImageView.setImageURI(data?.data)
            //filePath = data?.data

            val bitmap = (profileImageView.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            filePath = baos.toByteArray()
        }
    }

    private fun checkFields() {
        if (oldpass.text?.isEmpty()!!) {
            oldpass.error = "Please enter Username"
            return
        }
        if (newpass.text?.isEmpty()!!) {
            newpass.error = "Please enter Email"
            return
        }
        if (passwordTextRegister.text?.isEmpty()!!) {
            passwordTextRegister.error = "Please enter Password"
            return
        }
    }


    override fun openQuedadasActivity(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this, QuedadasActivity::class.java))
        } else {
            Toast.makeText(
                baseContext, "User not logged, please log in.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun showSignUpSuccessful() {
        Toast.makeText(
            baseContext, "User SignUp success",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showSignUpError() {
        Toast.makeText(
            baseContext, "Check User/Password/Email fields ",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onProcessStarts() {
        progressBarRegister.isVisible = true
    }

    override fun onProcessEnds() {
        progressBarRegister.isVisible = false
    }
}
