package com.pernas.socialmeet.ui.login

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import kotlinx.coroutines.*
import java.lang.Exception

class LoginPresenter(
    private val view: LoginView,
    private val remoteRepository: RemoteRepository,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

) {

    fun loginClicked(email: String, password: String) {
        view.onProcessStarts()

        CoroutineScope(ioDispatcher).launch {
            try {
                val user = remoteRepository.doLogin(email, password)

                withContext(mainDispatcher) {

                    view.openQuedadasActivity(user)
                    view.onProcessEnds()
                    view.showLoginSuccessful()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.onProcessEnds()
                    view.showLoginError()
                }
            }
        }
    }

    fun saveFirestore(email: String,username: String,uid: String?,imageUrl : String) {
        CoroutineScope(ioDispatcher).launch {
            try {
                 remoteRepository.saveFirestore(email, username,uid,imageUrl)


            } catch (e: Exception) {
                withContext(Dispatchers.Main) {

                }
            }
        }
    }

      fun checkUserGoogle(): String? {
        CoroutineScope(ioDispatcher).launch {
            try {
                var googleUser = remoteRepository.checkifUserExist()
                Log.e("TESTUID DESDE PRESEnter", googleUser.toString())
                withContext(mainDispatcher) {
                   view.checkGoogleUser(googleUser)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {

                }
            }
        }
        return null
    }
}

interface LoginView {
    fun showLoginSuccessful()
    fun showLoginError()
    fun openQuedadasActivity(user: FirebaseUser?)
    fun onProcessStarts()
    fun onProcessEnds()
    fun checkGoogleUser(gUser: String?): Boolean

}