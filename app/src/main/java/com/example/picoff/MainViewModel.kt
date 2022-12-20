package com.example.picoff

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.picoff.models.ChallengeModel
import com.example.picoff.models.PendingChallengeModel
import com.example.picoff.models.UserModel
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class MainViewModel : ViewModel() {

    val challengesLoaded = MutableLiveData(false)

    private val _challengeList = MutableStateFlow<ArrayList<ChallengeModel>>(arrayListOf())
    val challengeList = _challengeList.asStateFlow()

    private val _pendingChallengesList = MutableStateFlow<ArrayList<PendingChallengeModel>>(arrayListOf())
    val pendingChallengesList = _pendingChallengesList.asStateFlow()

    private val _users = MutableStateFlow<ArrayList<UserModel>>(arrayListOf())
    val users = _users.asStateFlow()

    private var dbRefChallenges: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Challenges")
    private var dbRefPendingChallenges =
        FirebaseDatabase.getInstance().getReference("Pending Challenges")
    private var dbRefUsers =
        FirebaseDatabase.getInstance().getReference("Users")

    init {
        getChallengesData()
        getPendingChallengesData()
        getUsers()
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
                        tempList.add(challengeData!!)
                    }

                    _pendingChallengesList.value = tempList
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}
