package com.pernas.socialmeet.ui.data.remote


import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pernas.socialmeet.ui.model.User
import com.pernas.socialmeet.ui.quedadas.QuedadasActivity
import kotlinx.coroutines.tasks.await
import java.util.ArrayList


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

    override suspend fun signOut(auth: FirebaseAuth) {
        auth.signOut()
    }

    override suspend fun getUserData(auth: FirebaseAuth): User? {
        val db = FirebaseFirestore.getInstance()

        var uid: String? = auth.currentUser?.uid



        return try {
            val data = db
                .collection("users")
                .document(uid.toString())
                .get()
                .await()
                .toObject<User>()
            data

        } catch (e: Exception) {
            null
        }

    }

    override suspend fun checkifUserExist(
        user: FirebaseUser?,
        name: String?,
        email: String?,
        photoUrl: String?,
        uid: String?
    ): Boolean {
        val db = Firebase.firestore
        var condition = true

        try {
            val answer = db
                .collection("users")
                .whereEqualTo("id", uid.toString())
                .get()
                .await()
                .documents
                .isEmpty()
            Log.e("varible", answer.toString())

            if (answer == true) {
                saveFirestore(
                    email.toString(),
                    name.toString(),
                    uid.toString(),
                    photoUrl.toString()
                )
                condition = false
            } else {
                condition = false
            }
        } catch (e: Exception) {
            Log.e("eerorcheckuserifexist", e.toString())
        }
        return condition
    }

    override suspend fun getQuedadas() {
        val db = Firebase.firestore
        var auth = FirebaseAuth.getInstance()
        val hashMap = HashMap<Any, Any>()
        var uid: String? = auth.currentUser?.uid

        try {

            var docRef = db.document("users/${uid.toString()}")

            var dataa = docRef
                .get()
                .await()
                .get("quedadas") as ArrayList<*>


            Log.d("TST QUEDADAS", dataa?.toString())
            for (element in dataa) {
                println(element)
            }
            var index = dataa.count()



            if (index != 0) {
                for (i in 0 until index) {
                    var quedadasReference = dataa[i] as DocumentReference
                    var test = quedadasReference.get().await().data
                    if (test != null) {
                        for (element in test) {
                            //print(test["id"])
                            //print(element)
                            var image  = test["imageQuedada"]!!
                            hashMap.put(test["id"]!!,test)
                            //println(element)

                            println(image.toString())

                        }

                        //println(hashMap)
                    }
                }
                Log.e("holi",hashMap.toString())
                for(key in hashMap.keys){
                    println(key.toString())
                    println("Element at key $key : ${hashMap[key]}")
                }
            } else {
            Log.e("ERROR QUEDADAS","NO SE HA ENCONTRADO RESULTADO DE QUEDADAS")
            }


        } catch (e: Exception) {
            Log.e("eerorcheckuserifexist", e.toString())
        }
    }


    override suspend fun saveFirestore(
        email: String,
        username: String,
        uid: String?,
        imageUrl: String
    ) {
        db = Firebase.firestore


        /*val nestedData = hashMapOf(
            "a" to 5,
            "b" to true
        )*/
        val list = arrayListOf<String>()
        //arrayListOf("")

        val user: HashMap<String, Any> = hashMapOf(
            "email" to "${email}",
            "id" to "${uid}",
            "imageProfile" to "${imageUrl}",
            "quedadas" to list,
            "username" to "${username}"
        )



        db.collection("users").document("${uid}")
            .set(user)
            .await()

    }
}
