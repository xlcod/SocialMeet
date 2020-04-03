package com.pernas.socialmeet.ui.data.remote

import android.net.Uri
import com.google.firebase.auth.FirebaseUser

interface RemoteRepository {
    suspend fun doLogin(email: String, password: String): FirebaseUser?
    suspend fun doRegister(email: String, password: String,username: String,image: ByteArray?): FirebaseUser?
    suspend fun saveFirestore(email: String,username: String,uid: String?,imageUrl : String)
    suspend fun saveImageFirestore(email: String,username: String,image: ByteArray?,uid: String?)
}

