package com.pernas.socialmeet.ui.data.remote


import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pernas.socialmeet.ui.model.User
import kotlinx.coroutines.tasks.await


class RemoteRepoCalls : RemoteRepository {

    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    private lateinit var db: FirebaseFirestore
    private val RC_SIGN_IN: Int = 1


    override suspend fun doLogin(email: String, password: String): FirebaseUser? {
        auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(
            email, password
        ).await()
        return auth.currentUser ?: throw FirebaseAuthException("error", "try later")
    }

    override suspend fun doRegister(
        email: String,
        password: String,
        username: String,
        image: ByteArray?
    ): FirebaseUser? {
        auth = FirebaseAuth.getInstance()

        try {
            auth.createUserWithEmailAndPassword(
                email, password
            ).await()
            val user = auth.currentUser

            var id = user?.uid


            saveImageFirestore(email, username, image, id)

            return auth.currentUser ?: throw FirebaseAuthException("error", "try later")


        } catch (e: Exception) {
            Log.e("ERROR", "SOMETHING WENT WRONG")
            return null
        }
    }


    override suspend fun saveImageFirestore(
        email: String,
        username: String,
        image: ByteArray?,
        uid: String?
    ) {
        if (image == null) return

        var storage = Firebase.storage

        val storageRef = storage.reference


        val usersRef = storageRef.child("users/${uid}")


        try {
            var url = usersRef
                .putBytes(image)
                .await()
                .storage
                .downloadUrl
                .await()
                .toString()

            saveFirestore(email, username, uid, url)

            Log.d("URL", "${url}")
        } catch (e: Exception) {
            Log.e("ERROR", "SOMETHING WENT WRONG")

        }


    }

    override suspend fun signOut(auth : FirebaseAuth) {
        auth.signOut()
    }

    override suspend fun getUserData(auth: FirebaseAuth): User? {
        val db = FirebaseFirestore.getInstance()

          var uid: String? =   auth.currentUser?.uid

        /*val docRef = db.collection("users").document(uid.toString())
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val city = documentSnapshot.toObject<User>()
            Log.d("user",city?.username.toString())
            Log.d("user3",city?.email.toString())
            Log.d("user3",city?.imageProfile.toString())
        }*/


        return try{
            val data = db
                .collection("users")
                .document(uid.toString())
                .get()
                .await()
                .toObject<User>()
                data

        }catch (e : Exception){
            null
        }

    }


    override suspend fun saveFirestore(
        email: String,
        username: String,
        uid: String?,
        imageUrl: String
    ) {
        db = Firebase.firestore

        var arr = emptyArray<Any>()

        val user: HashMap<String, Any> = hashMapOf(
            "email" to "${email}",
            "id" to "${uid}",
            "imageProfile" to "${imageUrl}",
            "quedadas" to "hola manu ",
            "username" to "${username}"
        )


        db.collection("users").document("${uid}")
            .set(user)
            .await()

    }
}
