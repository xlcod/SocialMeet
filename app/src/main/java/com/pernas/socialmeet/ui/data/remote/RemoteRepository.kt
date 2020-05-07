package com.pernas.socialmeet.ui.data.remote


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pernas.socialmeet.ui.model.User

interface RemoteRepository {
    suspend fun doLogin(email: String, password: String): FirebaseUser?
    suspend fun doRegister(email: String, password: String,username: String,image: ByteArray?): FirebaseUser?
    suspend fun saveFirestore(email: String,username: String,uid: String?,imageUrl : String)
    suspend fun saveImageFirestore(email: String,username: String,image: ByteArray?,uid: String?)
    suspend fun signOut(auth : FirebaseAuth)
    suspend fun getUserData(auth : FirebaseAuth): User?
    suspend fun checkifUserExist(user: FirebaseUser?,name:String?,email: String?,photoUrl: String?,uid: String?): Boolean
    suspend fun getQuedadas(): HashMap<Any, Any>
    suspend fun addQuedada(name: String,place:String,street: String,image: ByteArray?,date:String,time: String)
    suspend fun saveQuedadasImage(name: String,place:String,street: String,image: ByteArray?,uid: String?,date: String,time : String)
    suspend fun saveQuedadasFirestore(name: String,place:String,street: String,url: String,uid: String?,date: String,time : String)
    suspend fun getUsers() : ArrayList<String>
}

