package com.pernas.socialmeet.ui.quedadasDetail

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import kotlinx.coroutines.*

class QuedadasDetailPresenter(
    private val view: QuedadasDetailView,
    private val remoteRepository: RemoteRepository,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    @RequiresApi(Build.VERSION_CODES.N)
    fun manageData(list: List<Any>) {
        CoroutineScope(ioDispatcher).launch {

            try {
                val nombre = list[5].toString()
                val lugar= list[1].toString()
                val fecha = list[0].toString()
                val calle = list[2].toString()
                val usuariosArray= list[4] as List<Any>
                val usuarios = usuariosArray.joinToString(",")
                val quedadaPhoto =  list[6].toString()

                withContext(mainDispatcher) {
                    view.showData(nombre,lugar,fecha,calle,usuarios,quedadaPhoto)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Wrong", "sometging went wrong  quedadas detail presenter")
                }
            }
        }
    }
}

interface QuedadasDetailView {
    fun showData(nombre: String,lugar: String,fecha: String,calle: String,usuarios: String,quedadaPhoto: String)
}