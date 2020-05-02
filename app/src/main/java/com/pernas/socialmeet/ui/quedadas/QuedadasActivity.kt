package com.pernas.socialmeet.ui.quedadas

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.firebase.auth.FirebaseAuth
import com.pernas.socialmeet.R
import com.pernas.socialmeet.ui.MapActivity.MapsActivity
import com.pernas.socialmeet.ui.data.remote.RemoteRepoCalls
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import com.pernas.socialmeet.ui.login.LoginActivity
import com.pernas.socialmeet.ui.profile.ProfileActivity
import com.pernas.socialmeet.ui.quedadasDetail.QuedadasDetail
import kotlinx.android.synthetic.main.activity_quedadas.*
import java.io.Serializable


class QuedadasActivity : AppCompatActivity(),QuedadasView {

    private lateinit var auth: FirebaseAuth
    lateinit var presenter: QuedadasPresenter
    private  var isOpen: Boolean = true
    private lateinit var mFabOpenAnim :Animation
    private lateinit var mFabCloseAnim :Animation
    private lateinit var quedadasAdapter: QuedadasAdapter



    override fun onPause() {
        super.onPause()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quedadas)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        auth = FirebaseAuth.getInstance()
        val remoteRepository: RemoteRepository = RemoteRepoCalls()
        presenter = QuedadasPresenter(this, remoteRepository)
        presenter.getUserData(auth)

        quedadasRecyclerView.layoutManager = LinearLayoutManager(this)
        quedadasRecyclerView.setHasFixedSize(true)
        quedadasAdapter = QuedadasAdapter{
            presenter.quedadaClicked(listOf(it))
        }
        quedadasRecyclerView.adapter = quedadasAdapter
        quedadasRecyclerView.layoutManager?.isMeasurementCacheEnabled = false;

        quedadasAdapter.notifyDataSetChanged()
        presenter.getQuedadas()


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

        itemsswipetorefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorPrimary))
        itemsswipetorefresh.setColorSchemeColors(Color.WHITE)

        itemsswipetorefresh.setOnRefreshListener {

            quedadasRecyclerView.adapter = quedadasAdapter
            quedadasAdapter.notifyDataSetChanged()
            presenter.getQuedadas()
            itemsswipetorefresh.isRefreshing = false
        }



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

    override fun showQuedadas(quedadaLista: HashMap<Any, Any>) {
        quedadasAdapter.addQuedadas(quedadaLista)
        quedadasRecyclerView.visibility= View.VISIBLE
        //emptyView.visibility = View.GONE

    }

    override fun showEmpty() {
        //emptyView.visibility = View.VISIBLE
        quedadasRecyclerView.visibility = View.GONE
    }

    override fun openQuedadaDetail(data: List<Any>) {
        val intent = Intent(this, QuedadasDetail::class.java)
        intent.putExtra("data", data as Serializable)
        startActivity(intent)
    }
}


