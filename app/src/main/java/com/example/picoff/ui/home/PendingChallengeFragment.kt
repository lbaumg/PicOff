package com.example.picoff.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picoff.R
import com.example.picoff.adapters.PendingChallengesAdapter
import com.example.picoff.databinding.FragmentPendingChallengeBinding
import com.example.picoff.models.PendingChallengeModel
import com.google.firebase.database.*


class PendingChallengeFragment : Fragment() {

    private var _binding: FragmentPendingChallengeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var rvPendingChallenges: RecyclerView
    private lateinit var tvLoadingPendingChallenges: TextView
    private lateinit var pendingChallengesList: ArrayList<PendingChallengeModel>

    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPendingChallengeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        rvPendingChallenges = root.findViewById(R.id.rvPendingChallenges)
        rvPendingChallenges.layoutManager = LinearLayoutManager(context)
        rvPendingChallenges.setHasFixedSize(true)

        tvLoadingPendingChallenges = root.findViewById(R.id.tvLoadingPendingChallenges)

        pendingChallengesList = arrayListOf()

        getPendingChallengesData()

        return root
    }

    private fun getPendingChallengesData() {
        rvPendingChallenges.visibility = View.GONE
        tvLoadingPendingChallenges.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("Pending Challenges")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pendingChallengesList.clear()
                if (snapshot.exists()) {
                    for (challengeSnap in snapshot.children) {
                        val challengeData = challengeSnap.getValue(PendingChallengeModel::class.java)
                        pendingChallengesList.add(challengeData!!)
                    }
                    val mAdapter = PendingChallengesAdapter(pendingChallengesList)
                    rvPendingChallenges.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : PendingChallengesAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {

                        }

                    })

                    rvPendingChallenges.visibility = View.VISIBLE
                    tvLoadingPendingChallenges.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

enum class ActiveFragment {
    RECEIVED, SENT
}
