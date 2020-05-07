package com.pernas.socialmeet.ui.quedadasDetail

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.pernas.socialmeet.R
import com.pernas.socialmeet.ui.data.remote.RemoteRepoCalls
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_quedadas_detail.*


class QuedadasDetail : AppCompatActivity(),QuedadasDetailView {

    lateinit var presenter: QuedadasDetailPresenter

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_quedadas_detail)

        val remoteRepository: RemoteRepository = RemoteRepoCalls()
        presenter = QuedadasDetailPresenter(this, remoteRepository)

        val quedadaData = intent.extras?.get("data") as List<Any>
        val selectedData = quedadaData[0] as List<Any>

        presenter.manageData(selectedData)
    }

    override fun showData(nombre: String,lugar: String,fecha: String,calle: String,usuarios: String,quedadaPhoto: String) {
        nameTextView.text= nombre
        lugarTextView.text = lugar
        fechaTextView.text = fecha
        calleTextView.text = calle
        usuariosTextView.text = usuarios
        Picasso.get().load(quedadaPhoto).into(imageViewPager)
    }
}
