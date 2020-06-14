package com.pernas.socialmeet.ui.quedadasAdd

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.pernas.socialmeet.R
import com.pernas.socialmeet.ui.data.remote.RemoteRepoCalls
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import com.pernas.socialmeet.ui.quedadas.QuedadasActivity
import com.pernas.socialmeet.ui.quedadas.QuedadasAdapter
import kotlinx.android.synthetic.main.activity_quedadas_add.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class QuedadasAddActivity : AppCompatActivity(), QuedadasAddView {

    var button_date: Button? = null
    var textview_date: TextView? = null
    private lateinit var quedadasAddAdapter: QuedadasAddAdapter


    @RequiresApi(Build.VERSION_CODES.N)
    var cal = Calendar.getInstance()
    lateinit var presenter: QuedadasAddPresenter
    private lateinit var filePath: ByteArray


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quedadas_add)

        val remoteRepository: RemoteRepository = RemoteRepoCalls()
        presenter = QuedadasAddPresenter(this, remoteRepository)

        textview_date = text_view_date_1
        button_date = button_date_1
        val mPickTimeBtn = findViewById<Button>(R.id.pickTimeBtn)
        val textView = findViewById<TextView>(R.id.text_view_time_1)


        textview_date!!.text = "--/--/----"
        text_view_time_1!!.text = "HH:MM"

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }


        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        button_date!!.setOnClickListener {
            DatePickerDialog(
                this@QuedadasAddActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        mPickTimeBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                textView.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }
        usernamesAddRecyclerView.layoutManager = LinearLayoutManager(this)
        usernamesAddRecyclerView.setHasFixedSize(true)

        quedadasAddAdapter = QuedadasAddAdapter{

        }
        usernamesAddRecyclerView.adapter = quedadasAddAdapter
        usernamesAddRecyclerView.layoutManager?.isMeasurementCacheEnabled = false

        presenter.getUsernames()

        addQuedadasImageView.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        addQuedadaButton.setOnClickListener {
            val condition = checkFields()
            val users=  quedadasAddAdapter.addSelectedUsers()
            val usersid = quedadasAddAdapter.selectedUsersId
            if (condition == false) {
                return@setOnClickListener
            } else {
                presenter.addQuedada(
                    nameTextAdd.text.toString(),
                    lugarTextAdd.text.toString(),
                    calleTextAdd.text.toString(),
                    filePath,
                    textview_date!!.text.toString(),
                    text_view_time_1.text.toString(),
                    users,
                    usersid
                )
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
            addQuedadasImageView.setImageURI(data?.data)


            val bitmap = (addQuedadasImageView.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            filePath = baos.toByteArray()
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.time)
        val test = textview_date!!.text.toString()
        Log.d("Test date ", test)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun addSuccesful() {
        startActivity(Intent(this, QuedadasActivity::class.java))
    }

    override fun onProcessStarts() {
        progressBarQuedadas.isVisible = true
    }

    override fun onProcessEnds() {
        progressBarQuedadas.isVisible = false
    }

    override fun showSignUpSuccessful() {
        Toast.makeText(
            baseContext, "Quedada Added successfully",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showSignUpError() {
        Toast.makeText(
            baseContext, "Check all fields ",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun addNames(list: HashMap<Any,Any>) {
        quedadasAddAdapter.showUsers(list)
        usernamesAddRecyclerView.visibility= View.VISIBLE
    }

    private fun checkFields(): Boolean {
        var condition = false

        if (nameTextAdd.text?.isEmpty()!!) {
            nameInputLayout.error = "Please enter a name"
            condition = false
        } else if (lugarTextAdd.text?.isEmpty()!!) {
            lugarInputLayout.error = "Please enter a place"
            condition = false
        } else if (calleTextAdd.text?.isEmpty()!!) {
            calleInputLayout.error = "Please enter street"
            condition = false
        } else if (textview_date?.text?.isEmpty()!!) {
            textview_date!!.error = "Please enter a date"
            condition = false
        } else {
            condition = true
        }
        return condition
    }
}

