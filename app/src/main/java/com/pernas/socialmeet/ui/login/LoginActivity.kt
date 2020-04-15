package com.pernas.socialmeet.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pernas.socialmeet.R
import com.pernas.socialmeet.ui.data.remote.RemoteRepoCalls
import com.pernas.socialmeet.ui.data.remote.RemoteRepository
import com.pernas.socialmeet.ui.quedadas.QuedadasActivity
import com.pernas.socialmeet.ui.register.RegistrerActivity
import kotlinx.android.synthetic.main.activity_main.*


class LoginActivity : AppCompatActivity(), LoginView {

    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    private val RC_SIGN_IN: Int = 1
    lateinit var presenter: LoginPresenter


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val remoteRepository: RemoteRepository = RemoteRepoCalls()
        presenter = LoginPresenter(this, remoteRepository)


        registerButton.setOnClickListener {
            val intent = Intent(this, RegistrerActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            checkFields()
            presenter.loginClicked(emailField.text.toString(), passwordField.text.toString())

        }

        google_button.setOnClickListener { view: View? ->
            signInGoogle()
        }


        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        //google sign-in
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        openQuedadasActivity(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Log.e("algo mal", "muy mal")
            Toast.makeText(this, toString(), Toast.LENGTH_LONG).show()
            Log.e("ERRROR HANDLE RESULT ", e.toString())

        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        var db = Firebase.firestore

        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {

                //presenter.checkGUser()

                var uid: String? = auth.currentUser?.uid

                var db = Firebase.firestore

                db.collection("users")
                    .whereEqualTo("id", uid.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            Log.d("ENTRA ", "ENtra?")
                            Log.d("ha encontrado", "${document.id} => ${document.data}")
                        }
                        if (documents.isEmpty) {
                            //crea la cuenta
                            val user = FirebaseAuth.getInstance().currentUser
                            val name = user?.displayName
                            val email = user?.email
                            val photoUrl = user?.photoUrl
                            val uid = user?.uid
                            presenter.saveFirestore(
                                email.toString(),
                                name.toString(),
                                uid,
                                photoUrl.toString()
                            )
                            startActivity(Intent(this, QuedadasActivity::class.java))

                        } else {
                            startActivity(Intent(this, QuedadasActivity::class.java))
                        }

                    }
                    .addOnFailureListener { exception ->
                    }

            } else {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkFields() {
        if (emailField.text?.isEmpty()!!) {
            emailLogin.error = "Please enter an Email"
            return
        }
        if (passwordField.text?.isEmpty()!!) {
            passwordLogin.error = "Please enter password"
            return
        }
    }


    override fun showLoginSuccessful() {
        Toast.makeText(
            baseContext, "User logged successfully",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showLoginError() {
        Toast.makeText(
            baseContext, "Check password/email",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun openQuedadasActivity(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this, QuedadasActivity::class.java))
        } else {
            Toast.makeText(
                baseContext, "User not logged, please log in.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onProcessStarts() {
        progressBar.isVisible = true
    }

    override fun onProcessEnds() {
        progressBar.isVisible = false
    }

    override fun checkGoogleUser(gUser: String?): Boolean {
        Log.e("MI UID", gUser.toString())
        if (gUser == null) {
            return false
        } else {
            return true
        }
    }
}
