package com.pernas.socialmeet.ui.register

import com.google.firebase.auth.FirebaseUser
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import com.pernas.socialmeet.ui.login.LoginView
import kotlinx.coroutines.*
import java.lang.Exception

class RegisterPresenter(
    private val view: RegisterView,
    private val remoteRepository: RemoteRepository,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun signUpClicked(email: String, password: String) {
        view.onProcessStarts()

        CoroutineScope(ioDispatcher).launch {
            try {
                val user = remoteRepository.doRegister(email, password)

                withContext(mainDispatcher) {

                    view.openQuedadasActivity(user)
                    view.onProcessEnds()
                    view.showSignUpSuccessful()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.onProcessEnds()
                    view.showSignUpError()
                }
            }
        }
    }
}

interface RegisterView {
    fun openQuedadasActivity(user: FirebaseUser?)
    fun showSignUpSuccessful()
    fun showSignUpError()
    fun onProcessStarts()
    fun onProcessEnds()
}