package com.example.picoff.ui.challenges

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.picoff.R
import com.example.picoff.models.ChallengeModel
import com.example.picoff.models.PendingChallengeModel
import com.example.picoff.viewmodels.MainViewModel
import java.io.File

val REQUEST_IMAGE_CAPTURE = 100

class ChallengeDialogFragment(private val challengeModel: ChallengeModel) : DialogFragment() {

    private lateinit var tvChallengeCreator: TextView
    private lateinit var tvChallengeDialogTitle: TextView
    private lateinit var tvChallengeDialogDesc: TextView
    private lateinit var ivUserAvatar: ImageView
    private lateinit var btnCancel: Button
    private lateinit var btnChallengeFriend: Button

    private val viewModel: MainViewModel by activityViewModels()

    private var mediaPath: File? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_challenge, container, false)

        tvChallengeCreator = rootView.findViewById(R.id.tvChallengeCreator)
        ivUserAvatar = rootView.findViewById(R.id.ivChallengeCreatorAvatar)

        // Get display name + avatar of challenge creator from viewModel.users
        val creator = viewModel.users.value.first { it.uid == challengeModel.creatorId }
        tvChallengeCreator.text = creator.displayName
        Glide.with(requireContext()).load(creator.photoUrl).into(ivUserAvatar)
        rootView.findViewById<LinearLayout>(R.id.layoutAvatar).visibility = View.VISIBLE

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
            // Check for camera permission

            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                mediaPath = viewModel.createNewImageFile(requireContext())
                val uri = FileProvider.getUriForFile(requireContext(), "com.example.picoff.provider", mediaPath!!)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                launcher.launch(takePictureIntent)
            } catch (e: ActivityNotFoundException) {
                // display error state to the user
            }
        }

        return rootView
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (mediaPath != null) {
                    val bitmap = viewModel.getBitmapFromMediaPath(mediaPath!!)
                    val newChallenge = PendingChallengeModel(
                        challengeTitle = challengeModel.challengeTitle,
                        challengeDesc = challengeModel.challengeDesc,
                        uidChallenger = viewModel.auth.currentUser?.uid,
                        nameChallenger = viewModel.auth.currentUser?.displayName,
                        photoUrlChallenger = viewModel.auth.currentUser?.photoUrl.toString(),
                        status = "sent"
                    )
                    val dialog = DisplayCameraImageDialogFragment(
                        pendingChallenge = newChallenge,
                        bitmap = bitmap,
                        responseMode = false
                    )
                    dialog.show(parentFragmentManager, "displayCameraImageDialog")
                } else {
                    Toast.makeText(context, "Error: image could not be loaded", Toast.LENGTH_SHORT).show()
                }

                dismiss()
            }
        }


}
