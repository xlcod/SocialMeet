package com.pernas.socialmeet.ui.quedadasDetail

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pernas.socialmeet.R
import com.pernas.socialmeet.ui.data.remote.RemoteRepoCalls
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import com.pernas.socialmeet.ui.quedadas.QuedadasActivity
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
        Log.e("Data",quedadaData.toString())

        presenter.manageData(selectedData)

        deleteQuedadaButton.setOnClickListener {
            basicAlert(selectedData)
        }
    }

    override fun showData(nombre: String,lugar: String,fecha: String,calle: String,usuarios: String,quedadaPhoto: String) {
        nameTextView.text= nombre
        lugarTextView.text = lugar
        fechaTextView.text = fecha
        calleTextView.text = calle
        usuariosTextView.text = usuarios
        Picasso.get().load(quedadaPhoto).into(imageViewPager)
    }

    override fun deletedSuccess() {
        val intent = Intent(this, QuedadasActivity::class.java)
        startActivity(intent)
    }

    fun basicAlert(data: List<Any>){

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.delete_quedada_confirmation))
            .setMessage(resources.getString(R.string.delete_quedada_question))
            .setNeutralButton(resources.getString(R.string.delete_quedada_neutral)) { dialog, which ->
                // Respond to neutral button press
            }
            .setNegativeButton(resources.getString(R.string.delete_quedada_no)) { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton(resources.getString(R.string.delete_quedada_yes)) { dialog, which ->
                presenter.deleteQuedada(data)
            }
            .show()
    }
}
