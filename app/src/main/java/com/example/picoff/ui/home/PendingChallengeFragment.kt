package com.example.picoff.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picoff.MainViewModel
import com.example.picoff.R
import com.example.picoff.adapters.PendingChallengesAdapter
import com.example.picoff.databinding.FragmentPendingChallengeBinding
import com.example.picoff.models.PendingChallengeModel
import kotlinx.coroutines.launch


class PendingChallengeFragment : Fragment() {

    private var _binding: FragmentPendingChallengeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var rvPendingChallenges: RecyclerView
    private lateinit var tvLoadingPendingChallenges: TextView

    private lateinit var pendingChallengesAdapter: PendingChallengesAdapter

    private val viewModel: MainViewModel by activityViewModels()

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

        pendingChallengesAdapter = PendingChallengesAdapter()
        rvPendingChallenges.adapter = pendingChallengesAdapter

        pendingChallengesAdapter.setOnItemClickListener(object :
            PendingChallengesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // TODO add on click handling
                // TODO Accept challenge from friend and “PIC OFF”
                // onClick -> open challenge description -> accept -> camera -> append text
                // -> VOTING: picture challenger -> picture recipient -> VS -> halfscreen voting -> WINNER
                // TODO Adapt colors of RV items
                // TODO later on also implement RESULT and VOTE items
                val pendingChallenge = pendingChallengesAdapter.getItemForPosition(position)
                val dialog = PendingChallengeDialogFragment(pendingChallenge)
                dialog.show(parentFragmentManager, "pendingChallengeDialog")


            }
        })

        // Observe mainViewModel.pendingChallengesList and update recycler view on change
        lifecycleScope.launch {
            viewModel.pendingChallengesList.collect { pendingChallengesList ->
                var challengesList = arrayListOf<PendingChallengeModel>()
                val isActiveFragmentReceivedScreen = viewModel.homeActiveFragment == ActiveFragment.RECEIVED
                val isActiveFragmentSentScreen = viewModel.homeActiveFragment == ActiveFragment.SENT
                if ( isActiveFragmentReceivedScreen ) {
                    challengesList = pendingChallengesList.filter {
                        it.uidRecipient ==  viewModel.auth.currentUser!!.uid
                    } as ArrayList<PendingChallengeModel>
                } else if ( isActiveFragmentSentScreen ) {
                    println("test")
                    challengesList = pendingChallengesList.filter {
                        it.uidChallenger ==  viewModel.auth.currentUser!!.uid && it.status == "sent"
                    } as ArrayList<PendingChallengeModel>
                }

                pendingChallengesAdapter.updatePendingChallengeList(challengesList, viewModel.homeActiveFragment)
                rvPendingChallenges.visibility = View.VISIBLE
                tvLoadingPendingChallenges.visibility = View.GONE
            }
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

enum class ActiveFragment {
    RECEIVED, SENT
}
