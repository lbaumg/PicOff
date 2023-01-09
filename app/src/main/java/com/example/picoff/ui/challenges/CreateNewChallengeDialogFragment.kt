package com.example.picoff.ui.challenges

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.picoff.MainViewModel
import com.example.picoff.R
import com.example.picoff.models.PendingChallengeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.File

class CreateNewChallengeDialogFragment(private val withoutUpload: Boolean = false) : DialogFragment() {

    private lateinit var etChallengeTitle: EditText
    private lateinit var etChallengeDesc: EditText
    private lateinit var btnUpload: Button

    private lateinit var dbRefChallenges: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var mediaPath: File? = null
    private val viewModel: MainViewModel by activityViewModels()

    private var challengeTitle: String? = null
    private var challengeDesc: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_create_new_challenge, container, false)

        btnUpload = rootView.findViewById(R.id.btnNewChallengeUpload)
        etChallengeTitle = rootView.findViewById(R.id.etNewChallengeChallengeName)
        etChallengeDesc = rootView.findViewById(R.id.etNewChallengeDescription)

        dbRefChallenges = FirebaseDatabase.getInstance().getReference("Challenges")
        auth = FirebaseAuth.getInstance()

        if (withoutUpload) {
            val tvDialogTitle: TextView = rootView.findViewById(R.id.tvCreateNewChallengeDialogTitle)
            tvDialogTitle.text = "Start new challenge"
            btnUpload.text = "PIC OFF!"
            btnUpload.setOnClickListener {
                updateChallengeTitle()
                updateChallengeDescription()
                    println("YEAH")
                    println(challengeTitle)
                    println(challengeDesc)
                if (challengeTitle != null && challengeDesc != null) {
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

            }
        } else {
            btnUpload.setOnClickListener {
                saveChallengeData()
            }

            // Observe status of challenge upload
            viewModel.statusUploadChallenge.observe(viewLifecycleOwner) { status ->
                status?.let {
                    if (it) {
                        Toast.makeText(context, "Challenge uploaded!", Toast.LENGTH_SHORT).show()
                        etChallengeTitle.text.clear()
                        etChallengeDesc.text.clear()
                    } else {
                        Toast.makeText(context, "Error: challenge not uploaded", Toast.LENGTH_LONG).show()
                    }
                    viewModel.statusUploadChallenge.value = null
                }
            }
        }


        val btnCancel = rootView.findViewById<Button>(R.id.btnNewChallengeCancel)
        btnCancel.setOnClickListener {
            dismiss()
        }

        return rootView
    }

    private fun saveChallengeData() {
        updateChallengeTitle()
        updateChallengeDescription()
        if (challengeTitle == null || challengeDesc == null)
            return
        viewModel.uploadChallenge(challengeTitle!!, challengeDesc!!)
        dismiss()
    }

    private fun updateChallengeTitle() {
        if (etChallengeTitle.text.isEmpty()) {
            etChallengeTitle.error = "Please enter challenge name"
        } else {
            challengeTitle = etChallengeTitle.text.toString()
        }
    }

    private fun updateChallengeDescription() {
        if (etChallengeDesc.text.isEmpty()) {
            etChallengeDesc.error = "Please enter challenge description"
        } else {
            challengeDesc = etChallengeDesc.text.toString()
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (mediaPath != null) {
                    val bitmap = viewModel.getBitmapFromMediaPath(mediaPath!!)
                    val newChallenge = PendingChallengeModel(
                        challengeTitle = challengeTitle,
                        challengeDesc = challengeDesc,
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