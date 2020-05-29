package com.pernas.socialmeet.ui.profile


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.pernas.socialmeet.R
import com.pernas.socialmeet.ui.changePass.PasswordChangeActivity
import com.pernas.socialmeet.ui.data.remote.RemoteRepoCalls
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import com.pernas.socialmeet.ui.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_quedadas_detail.*


class ProfileActivity : AppCompatActivity(),ProfileView {

    private lateinit var auth: FirebaseAuth
    lateinit var presenter: ProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        auth = FirebaseAuth.getInstance()

        val remoteRepository: RemoteRepository = RemoteRepoCalls()
        presenter = ProfilePresenter(this, remoteRepository)


        presenter.getUserData(auth)

        changePassword.setOnClickListener {
            changePassword()
        }
    }

    fun changePassword(){
        val intent = Intent(this, PasswordChangeActivity::class.java)
        startActivity(intent)
    }

    override fun showUserData(user: User?) {
        username_field.text = user?.username.toString()
        username2_field.text = user?.username.toString()
        email_field.text = user?.email.toString()
        Picasso.get().load(user?.imageProfile.toString()).into(iconProfile)
    }

    override fun passwordConfirmation() {
        Toast.makeText(this, "password updated!", Toast.LENGTH_LONG).show()
    }
}
