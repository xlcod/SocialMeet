package com.pernas.socialmeet.ui.quedadasDetail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.EditText
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


class QuedadasDetail : AppCompatActivity(), QuedadasDetailView {

    lateinit var presenter: QuedadasDetailPresenter


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quedadas_detail)

        val remoteRepository: RemoteRepository = RemoteRepoCalls()
        presenter = QuedadasDetailPresenter(this, remoteRepository)

        val quedadaData = intent.extras?.get("data") as List<Any>
        val selectedData = quedadaData[0] as List<Any>
        Log.e("Data", quedadaData.toString())

        presenter.manageData(selectedData)

        deleteQuedadaButton.setOnClickListener {
            basicAlert(selectedData)
        }


        fechaTextView.setOnClickListener {
            alertFecha(selectedData[3].toString())
        }
        nameTextView.setOnClickListener {
            alertName(selectedData[3].toString())
        }
        lugarTextView.setOnClickListener {
            alertLugar(selectedData[3].toString())
        }
        calleTextView.setOnClickListener {
            alertCalle(selectedData[3].toString())
        }


    }

    private fun alertCalle(quedadaId: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val editText = EditText(this)


        alertDialogBuilder.setTitle("Editar Calle")
        alertDialogBuilder.setView(editText)

        alertDialogBuilder.setPositiveButton(
            "guardar",
            { dialog, which ->
                calleTextView.setText(editText.text)
                presenter.updateFields(
                    nameTextView.text.toString(),
                    fechaTextView.text.toString(),
                    lugarTextView.text.toString(),
                    editText.text.toString(),
                    quedadaId
                )
            })

        if (calleTextView != null) {
            val parentViewGroup = editText.parent as ViewGroup?
            parentViewGroup?.removeAllViews();
        }
        editText.setText(calleTextView.text)
        alertDialogBuilder.show()
    }

    private fun alertLugar(quedadaId: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val editText = EditText(this)


        alertDialogBuilder.setTitle("Editar Lugar")
        alertDialogBuilder.setView(editText)

        alertDialogBuilder.setPositiveButton(
            "guardar",
            { dialog, which ->
                lugarTextView.setText(editText.text)
                presenter.updateFields(
                    nameTextView.text.toString(),
                    fechaTextView.text.toString(),
                    editText.text.toString(),
                    calleTextView.text.toString(),
                    quedadaId
                )
            })

        if (lugarTextView != null) {
            val parentViewGroup = editText.parent as ViewGroup?
            parentViewGroup?.removeAllViews();
        }
        editText.setText(lugarTextView.text)
        alertDialogBuilder.show()
    }

    private fun alertName(quedadaId: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val editText = EditText(this)


        alertDialogBuilder.setTitle("Editar Nombre")
        alertDialogBuilder.setView(editText)

        alertDialogBuilder.setPositiveButton(
            "guardar",
            { dialog, which ->
                nameTextView.setText(editText.text)
                presenter.updateFields(
                    editText.text.toString(),
                    fechaTextView.text.toString(),
                    lugarTextView.text.toString(),
                    calleTextView.text.toString(),
                    quedadaId
                )
            })

        if (fechaTextView != null) {
            val parentViewGroup = editText.parent as ViewGroup?
            parentViewGroup?.removeAllViews();
        }
        editText.setText(nameTextView.text)
        alertDialogBuilder.show()

    }

    private fun alertFecha(quedadaId: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val editText = EditText(this)


        alertDialogBuilder.setTitle("Editar Fecha")
        alertDialogBuilder.setView(editText)

        alertDialogBuilder.setPositiveButton(
            "guardar",
            { dialog, which ->
                fechaTextView.setText(editText.text)
                presenter.updateFields(
                    nameTextView.text.toString(),
                    editText.text.toString(),
                    lugarTextView.text.toString(),
                    calleTextView.text.toString(),
                    quedadaId
                )
            })

        if (fechaTextView != null) {
            val parentViewGroup = editText.parent as ViewGroup?
            parentViewGroup?.removeAllViews();
        }
        editText.setText(fechaTextView.text)
        alertDialogBuilder.show()


    }

    override fun showData(
        nombre: String,
        lugar: String,
        fecha: String,
        calle: String,
        usuarios: String,
        quedadaPhoto: String
    ) {
        nameTextView.text = nombre
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

    override fun updatedSucess() {
        val intent = Intent(this, QuedadasActivity::class.java)
        startActivity(intent)
        Toast.makeText(
            baseContext, "Quedada Updated successfully",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun updateFailed() {
        Toast.makeText(
            baseContext, "Quedada Update Failed, Please Check Fields or try again",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun basicAlert(data: List<Any>) {

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
