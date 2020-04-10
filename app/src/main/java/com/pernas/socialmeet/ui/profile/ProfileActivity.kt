package com.pernas.socialmeet.ui.profile


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.pernas.socialmeet.R
import com.pernas.socialmeet.ui.data.remote.RemoteRepoCalls
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import com.pernas.socialmeet.ui.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity(),ProfileView {

    private lateinit var auth: FirebaseAuth
    lateinit var presenter: ProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        auth = FirebaseAuth.getInstance()

        val remoteRepository: RemoteRepository = RemoteRepoCalls()
        presenter = ProfilePresenter(this, remoteRepository)


        presenter.getUserData(auth)





    }

    override fun showUserData(user: User?) {
        username_field.text = user?.username.toString()
        username2_field.text = user?.username.toString()
        email_field.text = user?.email.toString()
        Picasso.get().load(user?.imageProfile.toString()).into(iconProfile);
    }
}
