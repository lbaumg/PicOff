package com.example.picoff.ui.home

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
import com.example.picoff.models.PendingChallengeModel
import com.example.picoff.ui.challenges.DisplayCameraImageDialogFragment
import com.example.picoff.viewmodels.MainViewModel
import java.io.File


class PendingChallengeDialogFragment(private val pendingChallenge: PendingChallengeModel, private val showOnlyInfo: Boolean = false) : DialogFragment() {

    private lateinit var tvChallengerName: TextView
    private lateinit var tvChallengeTitle: TextView
    private lateinit var tvChallengeDesc: TextView
    private lateinit var tvStatus: TextView
    private lateinit var ivUserAvatar: ImageView
    private lateinit var btnCancel: Button
    private lateinit var btnAccept: Button

    private val viewModel: MainViewModel by activityViewModels()

    private var mediaPath: File? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_pending_challenge, container, false)

        tvChallengerName = rootView.findViewById(R.id.tvDlgPendingChallengerName)
        ivUserAvatar = rootView.findViewById(R.id.ivDlgPendingChallengeAvatar)
        tvChallengeTitle = rootView.findViewById(R.id.tvDlgPendingChallengeTitle)
        tvChallengeDesc = rootView.findViewById(R.id.tvDlgPendingChallengeDesc)
        btnCancel = rootView.findViewById(R.id.btnDlgPendingChallengeCancel)
        tvStatus = rootView.findViewById(R.id.tvDlgPendingChallengeStatus)
        btnAccept = rootView.findViewById(R.id.btnDlgPendingChallengeAccept)

        // Get display name + avatar of challenge creator from firebase
        val isUserChallenger = viewModel.auth.currentUser!!.uid == pendingChallenge.uidChallenger
        var opponentName = pendingChallenge.nameChallenger
        var opponentPhotoUrl = pendingChallenge.photoUrlChallenger
        if (isUserChallenger) {
            opponentName = pendingChallenge.nameRecipient
            opponentPhotoUrl = pendingChallenge.photoUrlRecipient
        }

        tvChallengerName.text = opponentName
        Glide.with(requireContext()).load(opponentPhotoUrl).into(ivUserAvatar)
        rootView.findViewById<LinearLayout>(R.id.lytAvatar).visibility = View.VISIBLE

        tvChallengeTitle.text = pendingChallenge.challengeTitle
        tvChallengeDesc.text = pendingChallenge.challengeDesc

        btnCancel.setOnClickListener {
            dismiss()
        }

        val statusText = "Status: ${pendingChallenge.status}"
        tvStatus.text = statusText

        if (showOnlyInfo) {
            btnAccept.visibility = View.INVISIBLE
        } else {
            btnAccept.setOnClickListener {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    mediaPath = viewModel.createNewImageFile(requireContext())
                    val uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.picoff.provider",
                        mediaPath!!
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    launcher.launch(takePictureIntent)
                } catch (e: ActivityNotFoundException) {
                    // display error state to the user
                }
            }
        }

        return rootView
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (mediaPath != null) {
                    val bitmap = viewModel.getBitmapFromMediaPath(mediaPath!!)
                    val dialog = DisplayCameraImageDialogFragment(
                        pendingChallenge = pendingChallenge,
                        bitmap = bitmap,
                        responseMode = true
                    )
                    dialog.show(parentFragmentManager, "displayCameraImageDialog")
                } else {
                    Toast.makeText(context, "Error: image could not be loaded", Toast.LENGTH_SHORT).show()
                }

                dismiss()
            }
        }
}
