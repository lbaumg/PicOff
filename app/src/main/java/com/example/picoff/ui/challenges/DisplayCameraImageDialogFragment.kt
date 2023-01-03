package com.example.picoff.ui.challenges

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.picoff.R

class DisplayCameraImageDialogFragment(val challengeTitle: String, val challengeDesc: String, private val bitmap: Bitmap) : DialogFragment() {

    private lateinit var ivCameraImage: ImageView
    private lateinit var etAdditionalInfo: EditText
    private lateinit var btnContinue: Button

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

            val dialog = SelectFriendDialogFragment(
                challengeTitle = challengeTitle,
                challengeDesc = challengeDesc,
                bitmap = bitmap,
                additionalInfo = additionalInfo
            )
            dialog.show(parentFragmentManager, "selectFriendDialog")

            dismiss()
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
