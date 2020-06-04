package com.pernas.socialmeet.ui.changePass

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.pernas.socialmeet.R
import com.pernas.socialmeet.ui.data.remote.RemoteRepoCalls
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import com.pernas.socialmeet.ui.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_password_change.*

class PasswordChangeActivity : AppCompatActivity(), ChangePassView {

    lateinit var presenter: PasswordChangePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_change)

        val remoteRepository: RemoteRepository = RemoteRepoCalls()
        presenter = PasswordChangePresenter(this, remoteRepository)
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val userEmail = user?.email

        if (userEmail != null) {
            presenter.getEmail(userEmail)
        }
        email_view_pass.setOnClickListener {
            changeEmail()
        }


        updatePassButton.setOnClickListener {
            checkFields()
            presenter.changePassword(oldpass.text.toString(), newpass.text.toString())
        }

        callPassToast()
    }

    private fun callPassToast() {
        Toast.makeText(this, R.string.alert_ChangeEmail, Toast.LENGTH_LONG).show()
    }

    private fun changeEmail() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val editText = EditText(this)


        alertDialogBuilder.setTitle(R.string.change_email)
        alertDialogBuilder.setView(editText)

        alertDialogBuilder.setPositiveButton(
            "guardar",
            { dialog, which ->
                email_view_pass.setText(editText.text)
                presenter.changeEmail(editText.text.toString(),oldpass.text.toString())

            })

        if (email_view_pass != null) {
            val parentViewGroup = editText.parent as ViewGroup?
            parentViewGroup?.removeAllViews();
        }
        editText.setText(email_view_pass.text)
        alertDialogBuilder.show()

    }

    override fun passwordConfirmation() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        Toast.makeText(this, "password updated!", Toast.LENGTH_LONG).show()
    }

    override fun showEmail(email: String) {
        email_view_pass.text = email
    }

    override fun emailUpdated() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        Toast.makeText(this, "Email updated!", Toast.LENGTH_LONG).show()
    }

    fun checkFields() {
        if (oldpass.text?.isEmpty()!!) {
            oldPassword.error = "Please enter the old password"
            return
        }
        if (newpass.text?.isEmpty()!!) {
            newPassword.error = "Please enter new  password"
            return
        }
    }
}
