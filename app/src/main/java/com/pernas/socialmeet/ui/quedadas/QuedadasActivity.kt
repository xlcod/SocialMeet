package com.pernas.socialmeet.ui.quedadas

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.firebase.auth.FirebaseAuth
import com.pernas.socialmeet.ui.login.LoginActivity
import com.pernas.socialmeet.R
import com.pernas.socialmeet.ui.MapActivity.MapsActivity
import com.pernas.socialmeet.ui.data.remote.RemoteRepoCalls
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import kotlinx.android.synthetic.main.activity_quedadas.*
import kotlinx.android.synthetic.main.activity_quedadas.progressBar

class QuedadasActivity : AppCompatActivity(),QuedadasView {

    private lateinit var auth: FirebaseAuth
    lateinit var presenter: QuedadasPresenter
    private val id_quedadas = 1
    private val id_maps = 2

    override fun onPause() {
        super.onPause()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quedadas)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        auth = FirebaseAuth.getInstance()

        val remoteRepository: RemoteRepository = RemoteRepoCalls()
        presenter = QuedadasPresenter(this, remoteRepository)
        presenter.getUserData(auth)

        logOutIcon.setOnClickListener {
            presenter.signOut(auth)
            finish()
        }
        val bottomNavigation = findViewById<MeowBottomNavigation>(R.id.bottomNavigation)
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.quedadas_icon))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.profile_icon))

        bottomNavigation.show(1)



        bottomNavigation.setOnShowListener {
            Toast.makeText(
                baseContext, "selected" + it.id,
                Toast.LENGTH_SHORT
            ).show()
        }

        bottomNavigation.setOnClickMenuListener {
            when(it.id) {
                1 -> {
                    startActivity(Intent(this, QuedadasActivity::class.java),ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                }

                2 -> {
                    startActivity(Intent(this, MapsActivity::class.java),ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

                    }
            }
        }



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

    override fun closeSession() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onProcessStarts() {
        progressBar.isVisible = true
    }

    override fun onProcessEnds() {
        progressBar.isVisible = false
    }

    override fun showUserEmail(email: String?) {
       emailTextView.text = email.toString()
    }
}


