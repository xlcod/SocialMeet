package com.pernas.socialmeet.ui.changePass

import android.util.Log
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import com.pernas.socialmeet.ui.profile.ProfileView
import kotlinx.coroutines.*

class PasswordChangePresenter(
    private val view: ChangePassView,
    private val remoteRepository: RemoteRepository,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun changePassword(oldPass: String, newPass: String) {
        CoroutineScope(ioDispatcher).launch {

            remoteRepository.changePassword(oldPass, newPass)

            withContext(mainDispatcher) {
                view.passwordConfirmation()

            }
        }
    }

    fun changeEmail(newEmail: String, oldPass: String) {
        CoroutineScope(ioDispatcher).launch {
            remoteRepository.updateEmail(newEmail, oldPass)

            withContext(mainDispatcher) {
                view.emailUpdated()

            }
        }
    }

    fun getEmail(email: String) {
        view.showEmail(email)
    }
}


interface ChangePassView {
    fun passwordConfirmation()
    fun showEmail(email: String)
    fun emailUpdated()

}