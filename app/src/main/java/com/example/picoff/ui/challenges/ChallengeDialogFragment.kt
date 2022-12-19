package com.example.picoff.ui.challenges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.picoff.R
import com.example.picoff.models.ChallengeModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChallengeDialogFragment(private val challengeModel: ChallengeModel) : DialogFragment() {

    private lateinit var tvChallengeCreator: TextView
    private lateinit var tvChallengeDialogTitle: TextView
    private lateinit var tvChallengeDialogDesc: TextView
    private lateinit var ivUserAvatar: ImageView
    private lateinit var btnCancel: Button
    private lateinit var btnChallengeFriend: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_dialog_challenge, container, false)

        tvChallengeCreator = rootView.findViewById(R.id.tvChallengeCreator)
        ivUserAvatar = rootView.findViewById(R.id.ivChallengeCreatorAvatar)

        // Get display name + avatar of challenge creator from firebase
        var dbRefUsers = FirebaseDatabase.getInstance().getReference("Users/${challengeModel.creatorId}")
        dbRefUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val displayName = snapshot.child("displayName").value.toString()
                tvChallengeCreator.text = displayName
                val imgUrl = snapshot.child("photoUrl").value.toString()
                Glide.with(requireContext()).load(imgUrl).into(ivUserAvatar)
                rootView.findViewById<LinearLayout>(R.id.layoutAvatar).visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                println("The read failed: " + error.code)
            }
        })

        tvChallengeDialogTitle = rootView.findViewById(R.id.tvChallengeDialogTitle)
        tvChallengeDialogTitle.text = challengeModel.challengeTitle

        tvChallengeDialogDesc = rootView.findViewById(R.id.tvChallengeDialogDesc)
        tvChallengeDialogDesc.text = challengeModel.challengeDesc


        btnCancel = rootView.findViewById(R.id.btnDialogCancel)
        btnCancel.setOnClickListener {
            dismiss()
        }

        btnChallengeFriend = rootView.findViewById(R.id.btnDialogChallengeFriend)
        btnChallengeFriend.setOnClickListener {
            // TODO open camera to challenge friend
        }

        return rootView
    }

}
