package com.example.picoff.ui.challenges

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.picoff.R
import com.example.picoff.models.ChallengeModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class NewChallengeActivity : AppCompatActivity() {

    private lateinit var etChallengeTitle: EditText
    private lateinit var etChallengeDesc: EditText
    private lateinit var btnUpload: Button

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_challenge)

        btnUpload = findViewById(R.id.buttonUpload)
        etChallengeTitle = findViewById(R.id.etChallengeName)
        etChallengeDesc = findViewById(R.id.etDescription)

        dbRef = FirebaseDatabase.getInstance().getReference("Challenges")

        btnUpload.setOnClickListener {
            saveChallengeData()
        }
    }

    private fun saveChallengeData() {
        val challengeTitle = etChallengeTitle.text.toString()
        val challengeDesc = etChallengeDesc.text.toString()

        if (challengeTitle.isEmpty()) {
            etChallengeTitle.error = "Please enter challenge name"
            return
        }

        if (challengeDesc.isEmpty()) {
            etChallengeDesc.error = "Please enter challenge description"
            return
        }

        val challengeId = dbRef.push().key!!

        // TODO add creator name from google account
        val challenge = ChallengeModel(challengeId, challengeTitle, challengeDesc)

        dbRef.child(challengeId).setValue(challenge)
            .addOnCompleteListener{
                Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_LONG).show()

                etChallengeTitle.text.clear()
                etChallengeDesc.text.clear()
            }.addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }
    }
}