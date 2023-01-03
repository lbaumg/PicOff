package com.example.picoff.ui.challenges

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.picoff.R
import com.example.picoff.models.ChallengeModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

val REQUEST_IMAGE_CAPTURE = 100

class ChallengeDialogFragment(private val challengeModel: ChallengeModel) : DialogFragment() {

    private lateinit var tvChallengeCreator: TextView
    private lateinit var tvChallengeDialogTitle: TextView
    private lateinit var tvChallengeDialogDesc: TextView
    private lateinit var ivUserAvatar: ImageView
    private lateinit var btnCancel: Button
    private lateinit var btnChallengeFriend: Button

    private var mediaPath: File? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_challenge, container, false)

        tvChallengeCreator = rootView.findViewById(R.id.tvChallengeCreator)
        ivUserAvatar = rootView.findViewById(R.id.ivChallengeCreatorAvatar)

        // Get display name + avatar of challenge creator from firebase
        val dbRefUsers = FirebaseDatabase.getInstance().reference.child("Users").child(challengeModel.creatorId)
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
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                mediaPath = createNewImageFile(requireContext())
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
                    val myBitmap = BitmapFactory.decodeFile(mediaPath!!.absolutePath)
                    val height = myBitmap.height * 512 / myBitmap.width
                    val scale = Bitmap.createScaledBitmap(myBitmap, 512, height, true)
                    var rotate = 0F
                    try {
                        val exif = ExifInterface(mediaPath!!.absolutePath)

                        val orientation: Int = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED
                        )
                        when (orientation) {
                            ExifInterface.ORIENTATION_NORMAL -> rotate = 0F
                            ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270F
                            ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180F
                            ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90F
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    val matrix = Matrix()
                    matrix.postRotate(rotate)
                    val rotateBitmap = Bitmap.createBitmap(
                        scale, 0, 0, scale.width,
                        scale.height, matrix, true
                    )
                    val dialog = DisplayCameraImageDialogFragment(
                        challengeTitle = challengeModel.challengeTitle,
                        challengeDesc = challengeModel.challengeDesc,
                        bitmap = rotateBitmap
                    )
                    dialog.show(parentFragmentManager, "displayCameraImageDialog")
                } else {
                    Toast.makeText(context, "Error: image could not be loaded", Toast.LENGTH_SHORT).show()
                }

                dismiss()
            }
        }


private fun saveBitmapToFile(bitmap: Bitmap?, mimeType: String, absolutePath: String?): File? {
    if(absolutePath == null || bitmap == null){
        return null
    }

    val file = File(absolutePath)
    val stream = FileOutputStream(file)

    if (mimeType.contains("jpg", true) || mimeType.contains("jpeg", true))
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    else if (mimeType.contains("png", true))
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

    stream.close()

    return file
}


    @Throws(IOException::class)
    fun createNewImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            absolutePath
        }
    }
}
