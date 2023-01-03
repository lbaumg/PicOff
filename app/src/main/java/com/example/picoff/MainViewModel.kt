package com.example.picoff

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.picoff.models.ChallengeModel
import com.example.picoff.models.PendingChallengeModel
import com.example.picoff.models.UserModel
import com.example.picoff.ui.home.ActiveFragment
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class MainViewModel : ViewModel() {

    val challengesLoaded = MutableLiveData(false)

    var homeActiveFragment = ActiveFragment.RECEIVED

    val statusAddFriend = MutableLiveData<Boolean?>()
    val statusUploadChallenge = MutableLiveData<Boolean?>()

    private val _challengeList = MutableStateFlow<ArrayList<ChallengeModel>>(arrayListOf())
    val challengeList = _challengeList.asStateFlow()

    private val _pendingChallengesList = MutableStateFlow<ArrayList<PendingChallengeModel>>(arrayListOf())
    val pendingChallengesList = _pendingChallengesList.asStateFlow()

    private val _users = MutableStateFlow<ArrayList<UserModel>>(arrayListOf())
    val users = _users.asStateFlow()

    private val _friends = MutableStateFlow<ArrayList<UserModel>>(arrayListOf())
    val friends = _friends.asStateFlow()


    private var dbRef = FirebaseDatabase.getInstance().reference
    private var dbRefChallenges = dbRef.child("Challenges")
    private var dbRefPendingChallenges = dbRef.child("Pending Challenges")
    private var dbRefUsers = dbRef.child("Users")
    private var dbRefFriends = dbRef.child("Friends")

    val auth = FirebaseAuth.getInstance()

    init {
        getChallengesData()
        getPendingChallengesData()
        getUsers()
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

                        // Load challenger and recipient user data from firebase and store into challengeData
                        if (challengeData?.uidChallenger != null && challengeData.uidRecipient != null) {
                            val taskList = mutableListOf<Task<DataSnapshot>>(
                                dbRefUsers.child(challengeData.uidChallenger).get(),
                                dbRefUsers.child(challengeData.uidChallenger).get()
                            )

                            val resultTask = Tasks.whenAll(taskList)
                            resultTask.addOnCompleteListener {
                                for ((i, task) in taskList.withIndex()) {
                                    val user = task.result.getValue(UserModel::class.java)
                                    if (user != null) {
                                        if (i == 0) {
                                            challengeData.nameChallenger = user.displayName
                                            challengeData.photoUrlChallenger = user.photoUrl
                                        } else {
                                            challengeData.nameRecipient = user.displayName
                                            challengeData.photoUrlRecipient = user.photoUrl
                                        }
                                    }
                                }
                            }
                        }
                        tempList.add(challengeData!!)
                    }

                    _pendingChallengesList.value = tempList
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

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

    fun startNewChallenge(challengeTitle: String, challengeDesc: String, recipient: UserModel, bitmap: Bitmap, additionalInfo: String) {
        if (auth.currentUser != null) {
            val challengeId = dbRefPendingChallenges.push().key!!
            val newChallenge = PendingChallengeModel(
                challengeId = challengeId,
                challengeTitle = challengeTitle,
                challengeDesc = challengeDesc,
                uidChallenger = auth.currentUser!!.uid,
                uidRecipient = recipient.uid,
                additionalInfoChallenger = additionalInfo,
                status = "sent"
            )
            dbRefPendingChallenges.child(challengeId).setValue(newChallenge)
            // TODO store image in firebase cloud storage
        }
    }
}
