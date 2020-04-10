package com.pernas.socialmeet.ui.profile

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import com.pernas.socialmeet.ui.model.User
import kotlinx.coroutines.*
import java.lang.Exception

class ProfilePresenter (
    private val view: ProfileView,
    private val remoteRepository: RemoteRepository,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO)
    {

        fun getUserData(auth: FirebaseAuth) {
            CoroutineScope(ioDispatcher).launch {

                try {
                    var userData: User? = remoteRepository.getUserData(auth)

                    withContext(mainDispatcher) {
                        view.showUserData(userData)

                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("Wrong","sometging went wrong")
                    }
                }
            }

        }




}
interface  ProfileView {
    fun showUserData(user: User?)

}