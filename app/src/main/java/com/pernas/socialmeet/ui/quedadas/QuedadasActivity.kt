package com.pernas.socialmeet.ui.quedadas

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import com.pernas.socialmeet.ui.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_quedadas.*
import kotlinx.android.synthetic.main.activity_quedadas.progressBar

class QuedadasActivity : AppCompatActivity(),QuedadasView {

    private lateinit var auth: FirebaseAuth
    lateinit var presenter: QuedadasPresenter
    private val id_quedadas = 1
    private val id_maps = 2
    private  var isOpen: Boolean = true
    private lateinit var mFabOpenAnim :Animation
    private lateinit var mFabCloseAnim :Animation


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

        mFabOpenAnim = AnimationUtils.loadAnimation(this,R.anim.fab_open)
        mFabCloseAnim = AnimationUtils.loadAnimation(this,R.anim.fab_close)

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
        floatingButtonMain.setOnClickListener {


            if (isOpen) {
                presenter.changeStateFloating(isOpen)
                isOpen = false
            }else{
                presenter.changeStateFloating(isOpen)
                isOpen = true
            }
        }

        floatingButtonprofile.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java),ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
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

    override fun showButtons() {

        floatingButtonprofile.startAnimation(mFabOpenAnim)
        floatingButtonQuedadas.startAnimation(mFabOpenAnim)

        floatingButtonQuedadas.isVisible = true
        floatingButtonprofile.isVisible = true

    }

    override fun hideButtons() {


        floatingButtonprofile.startAnimation(mFabCloseAnim)
        floatingButtonQuedadas.startAnimation(mFabCloseAnim)

        floatingButtonQuedadas.isVisible = false
        floatingButtonprofile.isVisible = false
    }
}


