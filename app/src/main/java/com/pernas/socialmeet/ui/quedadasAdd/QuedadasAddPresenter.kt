package com.pernas.socialmeet.ui.quedadasAdd

import android.util.Log
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import kotlinx.coroutines.*
import java.lang.Exception

class QuedadasAddPresenter(
    private val view: QuedadasAddView,
    private val remoteRepository: RemoteRepository,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getUsernames() {
        CoroutineScope(ioDispatcher).launch {

            try {
                val names = remoteRepository.getUsers()

                withContext(mainDispatcher) {
                    view.addNames(names)

                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {

                }
            }
        }

    }

    fun addQuedada(
        name: String,
        place: String,
        street: String,
        filePath: ByteArray?,
        date: String,
        time: String,
        selectedUsers : ArrayList<String>,
        usersId  : ArrayList<String>
    ) {
        view.onProcessStarts()
        CoroutineScope(ioDispatcher).launch {

            try {
                remoteRepository.addQuedada(name, place, street, filePath, date, time,selectedUsers,usersId)

                withContext(mainDispatcher) {

                    view.addSuccesful()
                    view.onProcessEnds()
                    view.showSignUpSuccessful()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("ERROe manu",e.toString())
                    view.onProcessEnds()
                    view.showSignUpError()
                }
            }
        }
    }
}

interface QuedadasAddView {
    fun addSuccesful()
    fun onProcessStarts()
    fun onProcessEnds()
    fun showSignUpSuccessful()
    fun showSignUpError()
    fun addNames(list: HashMap<Any,Any>)
}