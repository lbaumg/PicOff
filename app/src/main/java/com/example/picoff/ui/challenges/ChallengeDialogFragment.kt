package com.example.picoff.ui.challenges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.picoff.R
import com.example.picoff.models.ChallengeModel

class ChallengeDialogFragment(private val challengeModel: ChallengeModel) : DialogFragment() {

    private lateinit var tvChallengeCreator: TextView
    private lateinit var tvChallengeDialogTitle: TextView
    private lateinit var tvChallengeDialogDesc: TextView
    private lateinit var btnCancel: Button
    private lateinit var btnChallengeFriend: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_challenge_dialog, container, false)

        tvChallengeCreator = rootView.findViewById(R.id.tvChallengeCreator)
        tvChallengeCreator.text = challengeModel.creator

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
