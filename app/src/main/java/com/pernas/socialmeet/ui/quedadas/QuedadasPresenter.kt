package com.pernas.socialmeet.ui.quedadas

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import com.pernas.socialmeet.ui.model.Quedada
import com.pernas.socialmeet.ui.model.User
import kotlinx.coroutines.*


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

                var email: User? = remoteRepository.getUserData(auth)
                remoteRepository.getQuedadas()

                withContext(mainDispatcher) {
                    view.showUserEmail(email?.email.toString())

                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Wrong", "sometging went wrong")
                }
            }
        }

    }

    fun getQuedadas() {

        CoroutineScope(ioDispatcher).launch {
            try {
                Log.d("Entra quedadas?", "SI ENTRA")
                var quedadas: HashMap<Any, Any> = remoteRepository.getQuedadas()




                Log.d("Entra quedadas?", quedadas.toString())
                withContext(mainDispatcher) {
                    if (quedadas.isEmpty()) {
                        view.showEmpty()
                        return@withContext
                    }
                    view.showQuedadas(quedadas)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Wrong", "sometging went wrong")
                }
            }
        }

    }

    fun changeStateFloating(status: Boolean) {
        if (status) {
            view.showButtons()
        } else {
            view.hideButtons()
        }
    }
}

interface QuedadasView {
    fun closeSession()
    fun onProcessStarts()
    fun onProcessEnds()
    fun showUserEmail(email: String?)
    fun showButtons()
    fun hideButtons()
    fun showQuedadas(hash: HashMap<Any, Any>)
    fun showEmpty()
}

