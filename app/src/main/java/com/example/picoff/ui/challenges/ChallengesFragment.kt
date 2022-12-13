package com.example.picoff.ui.challenges

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picoff.R
import com.example.picoff.databinding.FragmentChallengesBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class ChallengesFragment : Fragment() {

    private var _binding: FragmentChallengesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var btnNewChallenge: FloatingActionButton
    private lateinit var rvChallenges: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var challengeList: ArrayList<ChallengeModel>

    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChallengesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        btnNewChallenge = root.findViewById(R.id.buttonCreateNew)
        btnNewChallenge.setOnClickListener {
            val intent = Intent(root.context, NewChallengeActivity::class.java)
            startActivity(intent)
        }


        rvChallenges = root.findViewById(R.id.rvChallenges)
        rvChallenges.layoutManager = LinearLayoutManager(context)
        rvChallenges.setHasFixedSize(true)

        tvLoadingData = root.findViewById(R.id.tvLoadingData)

        challengeList = arrayListOf()

        getChallengesData()

        return root
    }

    private fun getChallengesData() {
        rvChallenges.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("Challenges")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                challengeList.clear()
                if (snapshot.exists()) {
                    for (challengeSnap in snapshot.children) {
                        val challengeData = challengeSnap.getValue(ChallengeModel::class.java)
                        challengeList.add(challengeData!!)
                    }
                    val mAdapter = ChallengesAdapter(challengeList)
                    rvChallenges.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : ChallengesAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {

                        }

                    })

                    rvChallenges.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
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