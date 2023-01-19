package com.example.picoff.ui.dialogs

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.navigation.fragment.findNavController
import com.example.picoff.R
import com.example.picoff.models.PendingChallengeModel
import com.example.picoff.ui.home.HomeFragmentDirections
import com.example.picoff.viewmodels.MainViewModel

class DisplayCameraImageDialogFragment() : DialogFragment() {

    companion object {
        private const val PENDING_CHALLENGE = "pendingChallenge"
        private const val FILE_PATH = "filePath"
        private const val RESPONSE_MODE = "responseMode"

        fun newInstance(pendingChallenge: PendingChallengeModel, filePath: String, responseMode: Boolean = false) = DisplayCameraImageDialogFragment().apply {
            arguments = bundleOf(
                PENDING_CHALLENGE to pendingChallenge,
                FILE_PATH to filePath,
                RESPONSE_MODE to responseMode
            )
        }
    }

    private var responseMode: Boolean = false
    private lateinit var pendingChallenge: PendingChallengeModel
    private lateinit var filePath: String
    private lateinit var bitmap: Bitmap

    private lateinit var ivCameraImage: ImageView
    private lateinit var etAdditionalInfo: EditText
    private lateinit var btnContinue: Button

    private val viewModel: MainViewModel by activityViewModels {
        SavedStateViewModelFactory(requireActivity().application, requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_display_camera_image, container, false)

        // Get arguments
        responseMode = requireArguments().getBoolean(RESPONSE_MODE)
        filePath = requireArguments().getString(FILE_PATH)!!
        pendingChallenge = if (Build.VERSION.SDK_INT >= 33) {
            requireArguments().getParcelable(PENDING_CHALLENGE, PendingChallengeModel::class.java)!!
        } else {
            requireArguments().getParcelable(PENDING_CHALLENGE)!!
        }

        bitmap = viewModel.getBitmapFromMediaPath(filePath)

        ivCameraImage = rootView.findViewById(R.id.ivCameraImage)
        etAdditionalInfo = rootView.findViewById(R.id.etAdditionalInfo)
        btnContinue = rootView.findViewById(R.id.btnContinue)

        ivCameraImage.setImageBitmap(bitmap)

        btnContinue.setOnClickListener {
            val additionalInfo = etAdditionalInfo.text.toString()

            if (responseMode) {
                pendingChallenge.additionalInfoRecipient = additionalInfo
                viewModel.respondToChallenge(
                    pendingChallenge = pendingChallenge,
                    bitmap = bitmap
                )
            } else {
                // If not in response mode (not responding to previously sent challenge) open SelectFriendDialogFragment
                pendingChallenge.additionalInfoChallenger = additionalInfo
                val dialog = SelectFriendDialogFragment.newInstance(
                    newPendingChallenge = pendingChallenge,
                    filePath = filePath,
                )
                dialog.show(parentFragmentManager, "selectFriendDialog")
                dismiss()
            }
        }

        viewModel.statusOperation.value = null
        viewModel.statusOperation.observe(this) { status ->
            status?.let { successful ->
                if (successful) {
                    Toast.makeText(
                        context, "Successfully responded to challenge!", Toast.LENGTH_SHORT
                    ).show()

                    viewModel.hideBottomNav()
                    val action = HomeFragmentDirections.actionNavigationHomeToVoteFragment(pendingChallenge)
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(
                        context, "Error: challenge response failed", Toast.LENGTH_SHORT
                    ).show()
                }
                dismiss()
            }
        }

        return rootView
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
