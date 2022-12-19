package com.example.picoff.ui.challenges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.picoff.R
import com.example.picoff.models.ChallengeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateNewChallengeDialogFragment : DialogFragment() {

    private lateinit var etChallengeTitle: EditText
    private lateinit var etChallengeDesc: EditText
    private lateinit var btnUpload: Button

    private lateinit var dbRefChallenges: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_dialog_create_new_challenge, container, false)


        btnUpload = rootView.findViewById(R.id.btnNewChallengeUpload)
        etChallengeTitle = rootView.findViewById(R.id.etNewChallengeChallengeName)
        etChallengeDesc = rootView.findViewById(R.id.etNewChallengeDescription)

        dbRefChallenges = FirebaseDatabase.getInstance().getReference("Challenges")
        auth = FirebaseAuth.getInstance()

        btnUpload.setOnClickListener {
            saveChallengeData()
        }

        val btnCancel = rootView.findViewById<Button>(R.id.btnNewChallengeCancel)
        btnCancel.setOnClickListener {
            dismiss()
        }
        return rootView
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

        // Create unique key for firebase
        val challengeId = dbRefChallenges.push().key!!

        // Save challenge into firebase RTDB under "Challenges"
        val challenge = ChallengeModel(challengeId, challengeTitle, challengeDesc, auth.currentUser!!.uid)
        dbRefChallenges.child(challengeId).setValue(challenge)
            .addOnCompleteListener{
                Toast.makeText(context, "Challenge uploaded!", Toast.LENGTH_LONG).show()

                etChallengeTitle.text.clear()
                etChallengeDesc.text.clear()
            }.addOnFailureListener { err ->
                Toast.makeText(context, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }
    }
}