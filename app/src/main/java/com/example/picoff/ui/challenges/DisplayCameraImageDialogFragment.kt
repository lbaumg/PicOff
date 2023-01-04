package com.example.picoff.ui.challenges

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.picoff.MainViewModel
import com.example.picoff.R
import com.example.picoff.models.PendingChallengeModel
import com.example.picoff.ui.home.VoteActivity

class DisplayCameraImageDialogFragment(
    private val pendingChallenge: PendingChallengeModel,
    private val bitmap: Bitmap,
    private val responseMode: Boolean = false
) : DialogFragment() {

    private lateinit var ivCameraImage: ImageView
    private lateinit var etAdditionalInfo: EditText
    private lateinit var btnContinue: Button

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_display_camera_image, container, false)

        ivCameraImage = rootView.findViewById(R.id.ivCameraImage)
        etAdditionalInfo = rootView.findViewById(R.id.etAdditionalInfo)
        btnContinue = rootView.findViewById(R.id.btnContinue)

        ivCameraImage.setImageBitmap(bitmap)

        btnContinue.setOnClickListener {
            val additionalInfo = etAdditionalInfo.text.toString()

            if (responseMode) {
                pendingChallenge.additionalInfoRecipient = additionalInfo
                viewModel.respondToChallengeAndVote(
                    pendingChallenge = pendingChallenge,
                    bitmap = bitmap
                )
            } else {
                // If not in response mode (not responding to previously sent challenge) open SelectFriendDialogFragment
                pendingChallenge.additionalInfoChallenger = additionalInfo
                val dialog = SelectFriendDialogFragment(
                    newPendingChallenge = pendingChallenge,
                    bitmap = bitmap,
                )
                dialog.show(parentFragmentManager, "selectFriendDialog")
                dismiss()
            }
        }

        viewModel.statusRespondedToChallenge.observe(this) { status ->
            status?.let { successful ->
                if (successful) {
                    Toast.makeText(
                        context, "Successfully responded to challenge!", Toast.LENGTH_SHORT
                    ).show()
                    // TODO Jump to vote screen
                    val intent = Intent(context, VoteActivity::class.java)
                    intent.putExtra("urlImgChallenger", pendingChallenge.challengeImageUrlChallenger)
                    intent.putExtra("urlImgRecipient", pendingChallenge.challengeImageUrlRecipient)
                    launcher.launch(intent)
                } else {
                    Toast.makeText(
                        context, "Error: challenge response failed", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        return rootView
    }


    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val vote = result.data?.extras?.getInt("vote")
                pendingChallenge.voteRecipient = vote
                pendingChallenge.status = "result"
                viewModel.updatePendingChallengeInFirebase(pendingChallenge)
            }
            dismiss()
        }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT;
            val height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog!!.window?.setLayout(width, height);

        }
    }


}
