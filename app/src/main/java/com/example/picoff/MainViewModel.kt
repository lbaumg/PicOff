package com.example.picoff

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Environment
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.picoff.models.ChallengeModel
import com.example.picoff.models.PendingChallengeModel
import com.example.picoff.models.UserModel
import com.example.picoff.ui.home.ActiveFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val PREFIX_IMAGE_STORAGE_PATH = "challenges"

class MainViewModel : ViewModel() {

    val challengesLoaded = MutableLiveData(false)

    var homeActiveFragment = ActiveFragment.RECEIVED

    val isFabMenuOpen = MutableLiveData<Boolean?>()
    val statusNewChallengeUploaded = MutableLiveData<Boolean?>()
    val statusRespondedToChallenge = MutableLiveData<Boolean?>()
    val jumpToChallengeList = MutableLiveData<Boolean?>()
    val statusAddFriend = MutableLiveData<Boolean?>()
    val statusUploadChallenge = MutableLiveData<Boolean?>()

    private val _bottomNavigationVisibility = MutableLiveData<Int>()
    val bottomNavigationVisibility: LiveData<Int>
        get() = _bottomNavigationVisibility

    private val _challengeList = MutableStateFlow<ArrayList<ChallengeModel>>(arrayListOf())
    val challengeList = _challengeList.asStateFlow()

    private val _pendingChallengesList = MutableStateFlow<ArrayList<PendingChallengeModel>>(arrayListOf())
    val pendingChallengesList = _pendingChallengesList.asStateFlow()

    private val _users = MutableStateFlow<ArrayList<UserModel>>(arrayListOf())
    val users = _users.asStateFlow()

    private val _friends = MutableStateFlow<ArrayList<UserModel>>(arrayListOf())
    val friends = _friends.asStateFlow()

    private var storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://picoff-5abdb.appspot.com/")
    private var dbRef = FirebaseDatabase.getInstance().reference
    private var dbRefChallenges = dbRef.child("Challenges")
    private var dbRefPendingChallenges = dbRef.child("Pending Challenges")
    private var dbRefUsers = dbRef.child("Users")
    private var dbRefFriends = dbRef.child("Friends")

    val auth = FirebaseAuth.getInstance()

    init {
        getUsers()
        getChallengesData()
    }

    fun showBottomNav() {
        _bottomNavigationVisibility.value = View.VISIBLE
    }

    fun hideBottomNav() {
        _bottomNavigationVisibility.value = View.GONE
    }

    private fun getFriends() {
        if (auth.currentUser == null) {
            return
        }
        dbRefFriends.child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val tempList = arrayListOf<UserModel>()
                        for (friend in snapshot.children) {
                            tempList.add(users.value.first { it.uid == friend.key!! })
                        }
                        _friends.value = tempList
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun addFriend(user: UserModel) {
        if (auth.currentUser == null) {
            return
        }
        dbRefFriends.child(auth.currentUser!!.uid).child(user.uid).setValue(true)
            .addOnCompleteListener {
                statusAddFriend.value = true
            }.addOnFailureListener {
                statusAddFriend.value = false
            }
    }

    private fun getUsers() {
        dbRefUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val tempList = arrayListOf<UserModel>()
                    for (userSnap in snapshot.children) {
                        val userData = userSnap.getValue(UserModel::class.java)
                        tempList.add(userData!!)
                    }
                    _users.value = tempList
                }

                // Wait for users to be loaded -> then load friends
                getFriends()
                getPendingChallengesData()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getChallengesData() {
        dbRefChallenges.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val tempList = arrayListOf<ChallengeModel>()
                    for (challengeSnap in snapshot.children) {
                        val challengeData = challengeSnap.getValue(ChallengeModel::class.java)
                        tempList.add(challengeData!!)
                    }
                    _challengeList.value = tempList
                    challengesLoaded.value = true
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getPendingChallengesData() {
        dbRefPendingChallenges.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val tempList = arrayListOf<PendingChallengeModel>()

                    for (challengeSnap in snapshot.children) {
                        val challengeData =
                            challengeSnap.getValue(PendingChallengeModel::class.java)

                        val isUserRecipient = challengeData?.uidRecipient == auth.currentUser?.uid
                        val isUserChallenger = challengeData?.uidChallenger == auth.currentUser?.uid

                        if (!isUserChallenger && !isUserRecipient) continue

                        if (isUserRecipient && challengeData?.status == "sent") {
                            challengeData.status = "open"
                        }

                        // Load challenger and recipient user data from users and store into challengeData
//                        if (challengeData?.uidChallenger != null && challengeData.uidRecipient != null) {
//                            val challenger = users.value.first { it.uid == challengeData.uidChallenger }
//                            val recipient = users.value.first { it.uid == challengeData.uidRecipient }
//
//                            challengeData.nameChallenger = challenger.displayName
//                            challengeData.photoUrlChallenger = challenger.photoUrl
//                            challengeData.nameRecipient = recipient.displayName
//                            challengeData.photoUrlRecipient = recipient.photoUrl
//                        }
                        println("CHALLENGE:" + challengeData?.status + " " + challengeData?.challengeTitle + " " + challengeData?.nameChallenger + " "+ challengeData?.nameRecipient)
                        tempList.add(challengeData!!)
                    }

                    _pendingChallengesList.value = tempList
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    fun uploadChallenge(challengeTitle: String, challengeDesc: String) {
        // Create unique key for firebase
        val challengeId = dbRefChallenges.push().key!!

        // Save challenge into firebase RTDB under "Challenges"
        val challenge = ChallengeModel(challengeId, challengeTitle, challengeDesc, auth.currentUser!!.uid)
        dbRefChallenges.child(challengeId).setValue(challenge)
            .addOnCompleteListener{
                statusUploadChallenge.value = true
            }.addOnFailureListener {
                statusUploadChallenge.value = false
            }
    }

    fun startNewChallenge(
        newPendingChallenge: PendingChallengeModel,
        bitmap: Bitmap,
    ) {
        if (auth.currentUser != null) {
            val challengeId = dbRefPendingChallenges.push().key!!

            val imagePath = "${PREFIX_IMAGE_STORAGE_PATH}/${challengeId}-${auth.currentUser!!.uid}.jpg"
            val imageRef = storageRef.child(imagePath)

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data: ByteArray = baos.toByteArray()

            val uploadTask: UploadTask = imageRef.putBytes(data)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        statusNewChallengeUploaded.value = false
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val challengeImageChallenger = task.result
                    newPendingChallenge.challengeId = challengeId
                    newPendingChallenge.challengeImageUrlChallenger = challengeImageChallenger.toString()
                    dbRefPendingChallenges.child(challengeId).setValue(newPendingChallenge)
                    statusNewChallengeUploaded.value = true
                } else {
                    // handle failure
                    statusNewChallengeUploaded.value = false
                }
            }
        }
    }

    fun respondToChallengeAndVote(pendingChallenge: PendingChallengeModel, bitmap: Bitmap) {
        if (auth.currentUser != null) {
            val imagePath = "${PREFIX_IMAGE_STORAGE_PATH}/${pendingChallenge.challengeId}-${pendingChallenge.uidRecipient}.jpg"
            val imageRef = storageRef.child(imagePath)

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data: ByteArray = baos.toByteArray()

            val uploadTask: UploadTask = imageRef.putBytes(data)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        statusRespondedToChallenge.value = false
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val challengeImageRecipientUrl = task.result
                    pendingChallenge.challengeImageUrlRecipient = challengeImageRecipientUrl.toString()
                    pendingChallenge.status = "vote1"
                    dbRefPendingChallenges.child(pendingChallenge.challengeId!!).setValue(pendingChallenge)
                    statusRespondedToChallenge.value = true
                } else {
                    // handle failure
                    statusRespondedToChallenge.value = false
                }
            }
        }
    }

    fun updatePendingChallengeInFirebase(pendingChallenge: PendingChallengeModel) {
        if (pendingChallenge.challengeId != null) {
            dbRefPendingChallenges.child(pendingChallenge.challengeId!!).setValue(pendingChallenge)
        }
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

    fun getBitmapFromMediaPath(mediaPath: File): Bitmap {
        val myBitmap = BitmapFactory.decodeFile(mediaPath.absolutePath)
        val height = myBitmap.height * 512 / myBitmap.width
        val scale = Bitmap.createScaledBitmap(myBitmap, 512, height, true)
        var rotate = 0F
        try {
            val exif = ExifInterface(mediaPath.absolutePath)

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
        return Bitmap.createBitmap(
            scale, 0, 0, scale.width,
            scale.height, matrix, true
        )
    }
}
