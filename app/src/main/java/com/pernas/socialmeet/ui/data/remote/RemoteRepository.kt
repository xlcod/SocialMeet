package com.pernas.socialmeet.ui.data.remote

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface RemoteRepository {
    suspend fun doLogin(email: String, password: String): FirebaseUser?
    suspend fun doRegister(email: String, password: String): FirebaseUser?
}

