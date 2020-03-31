package com.pernas.socialmeet.ui.login

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
}

interface LoginView {
    fun showLoginSuccessful()
    fun showLoginError()
    fun openQuedadasActivity(user: FirebaseUser?)
    fun onProcessStarts()
    fun onProcessEnds()
}