package com.pernas.socialmeet.ui.data.remote


import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pernas.socialmeet.ui.model.User
import kotlinx.coroutines.awaitAll
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

    //Saves Image and then gets url
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

        } catch (e: Exception) {
            Log.e("ERROR", "SOMETHING WENT WRONG")
        }
    }

    override suspend fun signOut(auth: FirebaseAuth) {
        auth.signOut()
    }

    //gets user data
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

    //check for google sign in register or log in
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

    //gets quedadas from the current user, and adds
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

            if (index != 0) {
                for (i in 0 until index) {
                    val quedadasReference = dataa[i] as DocumentReference
                    val test = quedadasReference.get().await().data?.toMutableMap()
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

    //add a quedada
    override suspend fun addQuedada(
        name: String,
        place: String,
        street: String,
        image: ByteArray?,
        date: String,
        time: String,
        users: ArrayList<String>,
        usersId: ArrayList<String>
    ) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val id = user?.uid

        saveQuedadasImage(name, place, street, image, id, date, time, users, usersId)
    }

    //saves image then get url
    override suspend fun saveQuedadasImage(
        name: String,
        place: String,
        street: String,
        image: ByteArray?,
        uid: String?,
        date: String,
        time: String,
        users: ArrayList<String>,
        usersId: ArrayList<String>
    ) {

        if (image == null) return
        val db = FirebaseFirestore.getInstance()
        val myId = db.collection("quedadas").document().id
        val storage = Firebase.storage
        val storageRef = storage.reference
        val quedadasRef = storageRef.child("quedadas/${myId}")


        try {
            val url = quedadasRef
                .putBytes(image)
                .await()
                .storage
                .downloadUrl
                .await()
                .toString()

            saveQuedadasFirestore(name, place, street, url, myId, date, time, users, usersId)

        } catch (e: Exception) {
            Log.e("ERROR", "SOMETHING WENT WRONG")
        }
    }

    // saves quedadas
    override suspend fun saveQuedadasFirestore(
        name: String,
        place: String,
        street: String,
        url: String,
        myId: String?,
        date: String,
        time: String,
        users: ArrayList<String>,
        usersId: ArrayList<String>
    ) {
        db = Firebase.firestore
        val myId = db.collection("quedadas").document().id
        Log.e("MYUSERS", users.toString())

        val quedada: HashMap<String, Any> = hashMapOf(
            "calle" to street,
            "fecha" to date + "," + time,
            "id" to myId,
            "imageQuedada" to url,
            "lugar" to place,
            "nombre" to name,
            "usuarios" to users
        )


        db.collection("quedadas").document(myId)
            .set(quedada)
            .await()


        val ref = db.document("quedadas/${myId}")

        try {
            Log.d("REFERENCIA?", ref.toString())
            updateQuedadas(myId, usersId)
        } catch (e: java.lang.Exception) {
            Log.e("ERROR", e.toString())
        }
    }

    //gets all users for checkbox
    override suspend fun getUsers(): HashMap<Any, Any> {
        val db = Firebase.firestore
        val hashPrueba = hashMapOf<Any, Any>()
        try {
            val data = db.collection("users")
                .get()
                .await()
                .documents
                .forEach {
                    val id = it.get("id")
                    val names = it.get("username")
                    hashPrueba.put(id.toString(), names!!)
                }
        } catch (e: Exception) {
            Log.e("ERRORGETUSERS", e.toString())
        }

        return hashPrueba
    }

    //gets ref and add to array
    override suspend fun updateQuedadas(ref: String, usersId: ArrayList<String>) {
        val auth = FirebaseAuth.getInstance()
        val uid: String? = auth.currentUser?.uid

        val docRef = db.collection("quedadas").document(ref)

        try {
            Log.d("REFERENCIUPDATEQUEDADAS", ref)

            db.document("users/${uid.toString()}")
                .update("quedadas", FieldValue.arrayUnion(docRef))

            for (id in usersId) {
                db.document("users/${id}")
                    .update("quedadas", FieldValue.arrayUnion(docRef))
            }
        } catch (e: java.lang.Exception) {
            Log.e("ERROR update", e.toString())
        }
    }

    //delete a quedada when pressed
    override suspend fun deleteQuedadas(id: String) {
        try {
            val db = Firebase.firestore
            val ref = db.collection("quedadas").document(id)
            db.collection("users")
                .get()
                .await()
                .documents
                .forEach {
                    it.reference.update("quedadas", FieldValue.arrayRemove(ref))

                }
            db.collection("quedadas").document(id)
                .delete()
                .await()

        } catch (e: Exception) {
            Log.e("ERROR BORRAR QUEDADA", e.toString())
        }
    }

    //update detail data when pressed
    override suspend fun updateDetailData(
        nombre: String,
        fecha: String,
        lugar: String,
        calle: String,
        quedadaId: String
    ): Boolean {
        var condition = false
        try {
            val db = Firebase.firestore
            var test = db.document("quedadas/${quedadaId}")
                .update(
                    mapOf(
                        "calle" to calle,
                        "fecha" to fecha,
                        "id" to quedadaId,
                        "lugar" to lugar,
                        "nombre" to nombre
                    )
                ).await()
            condition = true
            return condition
        } catch (e: Exception) {
            Log.e("FAIL UPDATEDETAIL", e.toString())
            condition = false
            return condition
        }
    }

    //updates user password
    override suspend fun changePassword(oldpass: String, newPass: String) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        try {
            val userEmail = user?.email

            val credential = userEmail?.let {
                EmailAuthProvider
                    .getCredential(it, oldpass)
            }

            if (credential != null) {
                user.reauthenticate(credential)
                    .await()
                user.updatePassword(newPass).await()
            }
        } catch (e: java.lang.Exception) {
            Log.e("Error updating password", e.toString())
        }
    }

    //update email when pressed
    override suspend fun updateEmail(newEmail: String, pass: String) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val db = Firebase.firestore
        val userUid = auth.currentUser?.uid
        try {
            val userEmail = user?.email

            val credential = userEmail?.let {
                EmailAuthProvider
                    .getCredential(it, pass)
            }

            if (credential != null) {
                user.reauthenticate(credential)
                    .await()
                user.updateEmail(newEmail).await()
            }
            db.document("users/${userUid}").update("email", newEmail).await()

        } catch (e: java.lang.Exception) {
            Log.e("Error updating Email", e.toString())
        }
    }

    // remove user from quedada
    override suspend fun userOutOfQuedada(id: String) {
        try {
            val db = Firebase.firestore
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser
            val userUid = auth.currentUser?.uid
            val ref = db.collection("quedadas").document(id)

            db.document("users/${userUid.toString()}")
                .update("quedadas", FieldValue.arrayRemove(ref))

            val userName = user?.displayName

            db.collection("quedadas").document(id)
                .update("usuarios", FieldValue.arrayRemove(userName))


        } catch (e: Exception) {
            Log.e("ERROR OUT FROM QUEDADA", e.toString())
        }
    }

    //saves profile user data in firestore
    override suspend fun saveFirestore(
        email: String,
        username: String,
        uid: String?,
        imageUrl: String
    ) {
        db = Firebase.firestore

        val list = arrayListOf<String>()

        val user: HashMap<String, Any> = hashMapOf(
            "email" to email,
            "id" to "${uid}",
            "imageProfile" to imageUrl,
            "quedadas" to list,
            "username" to username
        )

        db.collection("users").document("${uid}")
            .set(user)
            .await()
    }
}
