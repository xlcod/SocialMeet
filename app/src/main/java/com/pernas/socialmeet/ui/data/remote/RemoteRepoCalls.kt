package com.pernas.socialmeet.ui.data.remote


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pernas.socialmeet.ui.model.User
import kotlinx.coroutines.tasks.await


class RemoteRepoCalls : RemoteRepository {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

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
            val id = user?.uid

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

        val storage = Firebase.storage
        val storageRef = storage.reference
        val usersRef = storageRef.child("users/${uid}")

        try {
            val url = usersRef
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

        val uid: String? = auth.currentUser?.uid



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

            if (answer) {
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

    override suspend fun getQuedadas(): HashMap<Any, Any> {
        val db = Firebase.firestore
        val auth = FirebaseAuth.getInstance()
        val hashMap = HashMap<Any, Any>()
        val uid: String? = auth.currentUser?.uid

        try {
            val docRef = db.document("users/${uid.toString()}")
            val dataa = docRef
                .get()
                .await()
                .get("quedadas") as List<*>

            val index = dataa.count()

            Log.e("TEST",dataa?.toString())


            if (index != 0) {
                for (i in 0 until index) {
                    val quedadasReference = dataa[i] as DocumentReference
                    val test = quedadasReference.get().await().data?.toMutableMap()
                    Log.e("aqver",test.toString())
                    if (test != null) {
                       test.forEach {
                           val id = test["id"]
                           val data = test.values
                           hashMap.put(id.toString(), data)
                       }
                    }
                }
                return hashMap
            } else {
                Log.e("ERROR QUEDADAS", "NO SE HA ENCONTRADO RESULTADO DE QUEDADAS")
            }
        } catch (e: Exception) {
            Log.e("eerorcheckuserifexist", e.toString())
        }
        return hashMap
    }

    override suspend fun addQuedada(
        name: String,
        place: String,
        street: String,
        image: ByteArray?,
        date: String,
        time: String,
        users: ArrayList<String>
    ) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val id = user?.uid

        saveQuedadasImage(name, place, street, image, id, date, time, users)
    }

    override suspend fun saveQuedadasImage(
        name: String,
        place: String,
        street: String,
        image: ByteArray?,
        uid: String?,
        date: String,
        time: String,
        users: ArrayList<String>
    ) {

        if (image == null) return

        val storage = Firebase.storage
        val storageRef = storage.reference
        val quedadasRef = storageRef.child("quedadas/${uid}")


        try {
            val url = quedadasRef
                .putBytes(image)
                .await()
                .storage
                .downloadUrl
                .await()
                .toString()

            saveQuedadasFirestore(name, place, street, url, uid, date, time, users)

            Log.d("URL", "${url}")
        } catch (e: Exception) {
            Log.e("ERROR", "SOMETHING WENT WRONG")
        }
    }

    override suspend fun saveQuedadasFirestore(
        name: String,
        place: String,
        street: String,
        url: String,
        uid: String?,
        date: String,
        time: String,
        users: ArrayList<String>
    ) {
        db = Firebase.firestore
        val auth = FirebaseAuth.getInstance()
        val list = arrayListOf<String>()
        val myId = db.collection("quedadas").document().id

        val quedada: HashMap<String, Any> = hashMapOf(
            "calle" to "${street}",
            "fecha" to "${date}" + "," + "${time}",
            "id" to "${myId}",
            "imageQuedada" to "${url}",
            "lugar" to "${place}",
            "nombre" to "${name}",
            "usuarios" to users
        )


        db.collection("quedadas").document(myId)
            .set(quedada)
            .await()


        var ref = db.document("quedadas/${myId}")

        try {
            Log.d("REFERENCIA?", ref.toString())
            updateQuedadas(myId)
        } catch (e: java.lang.Exception) {
            Log.e("ERROR", e.toString())
        }


    }

    override suspend fun getUsers(): ArrayList<String> {
        val db = Firebase.firestore
        var firstLineArray = arrayListOf<String>()

        try {
            val data = db.collection("users")
                .get()
                .await()
                .documents
                .forEach { doc ->
                    var names = doc["username"] as String
                    firstLineArray.add(names)
                }
        } catch (e: Exception) {
            Log.e("ERROR", e.toString())
        }
        return firstLineArray
    }

    override suspend fun updateQuedadas(ref: String) {
        val auth = FirebaseAuth.getInstance()
        val uid: String? = auth.currentUser?.uid

        val docRef = db.collection("quedadas").document(ref)

        try {
            Log.d("REFERENCIUPDATEQUEDADAS", ref)

            db.document("users/${uid.toString()}")
                .update("quedadas", FieldValue.arrayUnion(docRef))
        } catch (e: java.lang.Exception) {
            Log.e("ERROR update", e.toString())
        }
    }


    override suspend fun saveFirestore(
        email: String,
        username: String,
        uid: String?,
        imageUrl: String
    ) {
        db = Firebase.firestore

        val list = arrayListOf<String>()

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
