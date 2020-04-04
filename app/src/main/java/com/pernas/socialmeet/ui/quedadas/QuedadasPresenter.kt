package com.pernas.socialmeet.ui.quedadas

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import kotlinx.coroutines.*
import java.lang.Exception


class QuedadasPresenter(
    private val view: QuedadasView,
    private val remoteRepository: RemoteRepository,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun signOut(auth: FirebaseAuth) {
        view.onProcessStarts()

        CoroutineScope(ioDispatcher).launch {

            try {
                remoteRepository.signOut(auth)

                withContext(mainDispatcher) {
                    view.closeSession()
                    view.onProcessEnds()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.onProcessEnds()
                }
            }
        }
    }
    fun getUserData(auth: FirebaseAuth) {
        CoroutineScope(ioDispatcher).launch {

            try {
                val email = remoteRepository.getUserData(auth)
                view.showUserEmail(email)

                withContext(mainDispatcher) {
                    view.showUserEmail(email)

                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Wrong","sometging went wrong")
                }
            }
        }

    }
}

interface QuedadasView {
    fun closeSession()
    fun onProcessStarts()
    fun onProcessEnds()
    fun showUserEmail(email: String?)
}

