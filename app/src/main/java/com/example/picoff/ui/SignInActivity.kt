package com.example.picoff.ui

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.picoff.R
import com.example.picoff.models.GoogleAccountModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*

class SignInActivity : AppCompatActivity() {

    private lateinit var btnSignIn: MaterialButton
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRefUsers: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users")

        // Instantiate auth object and googleSignInClient
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnSignIn = findViewById(R.id.btnSignIn)
        btnSignIn.setOnClickListener {
            signInGoogle()
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    // Launches google signInIntent and calls handleResult with account data
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResult(task)
            }
        }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                // Get credentials and sign in with google
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (auth.currentUser != null) {
                            saveAccountInUserDatabase()
                            finish()
                        }
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // Stores google account info in Users firebase
    private fun saveAccountInUserDatabase() {
        dbRefUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Add to Users firebase if not already exists
                if (!dataSnapshot.child(auth.currentUser!!.uid).exists()) {
                    dbRefUsers.child(auth.currentUser!!.uid).setValue(
                        GoogleAccountModel(
                            auth.currentUser!!.uid,
                            auth.currentUser!!.displayName,
                            auth.currentUser!!.email,
                            auth.currentUser!!.photoUrl.toString()
                        )
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "Error", Toast.LENGTH_SHORT).show();
                Toast.makeText(baseContext, error.message, Toast.LENGTH_SHORT).show();

            }
        })
    }

    override fun onBackPressed() = Unit
}