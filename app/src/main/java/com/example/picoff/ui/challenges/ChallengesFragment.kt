package com.example.picoff.ui.challenges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picoff.R
import com.example.picoff.adapters.ChallengesAdapter
import com.example.picoff.databinding.FragmentChallengesBinding
import com.example.picoff.viewmodels.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ChallengesFragment : Fragment() {

    private var _binding: FragmentChallengesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var btnNewChallenge: FloatingActionButton
    private lateinit var rvChallenges: RecyclerView
    private lateinit var tvLoadingData: TextView

    private val challengesAdapter = ChallengesAdapter()

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChallengesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set onClickListener to new challenge create button
        btnNewChallenge = root.findViewById(R.id.buttonCreateNew)
        btnNewChallenge.setOnClickListener {
            val dialog = CreateNewChallengeDialogFragment(false)
            dialog.show(parentFragmentManager, "createNewChallengeDialog")
        }

        // Set layout manager for recycler view
        rvChallenges = root.findViewById(R.id.rvChallenges)
        rvChallenges.layoutManager = LinearLayoutManager(context)
        rvChallenges.setHasFixedSize(true)
        rvChallenges.adapter = challengesAdapter

        tvLoadingData = root.findViewById(R.id.tvLoadingData)


        // Update the recycler view when the challenges are loaded
        mainViewModel.challengesLoaded.observe(viewLifecycleOwner) {
            if (it) {
                challengesAdapter.updateChallengeList(mainViewModel.challengeList.value)

                // Hide loading screen and show recycler view
                rvChallenges.visibility = View.VISIBLE
                tvLoadingData.visibility = View.GONE
            }
        }

        // Override onItemClickListener to open ChallengeDialogFragment
        challengesAdapter.setOnItemClickListener(object : ChallengesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val dialog = ChallengeDialogFragment(challengesAdapter.getItemForPosition(position))
                dialog.show(parentFragmentManager, "challengeDialog")
            }
        })

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
