package com.example.picoff.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picoff.R
import com.example.picoff.adapters.PendingChallengesAdapter
import com.example.picoff.databinding.FragmentPendingChallengeBinding
import com.example.picoff.models.PendingChallengeModel
import com.example.picoff.ui.dialogs.PendingChallengeDialogFragment
import com.example.picoff.viewmodels.MainViewModel


class PendingChallengeFragment : Fragment() {

    private var _binding: FragmentPendingChallengeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var rvPendingChallenges: RecyclerView
    private lateinit var tvLoadingPendingChallenges: TextView

    private lateinit var pendingChallengesAdapter: PendingChallengesAdapter

    private val viewModel: MainViewModel by activityViewModels {
        SavedStateViewModelFactory(requireActivity().application, requireActivity())
    }

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
                val pendingChallenge = pendingChallengesAdapter.getItemForPosition(position)

                when (pendingChallenge.status) {
                    "sent" -> { // Show pending challenge in dialog
                        openPendingChallengesInfo(pendingChallenge, true)
                    }
                    "open"  -> { // Show pending challenge in dialog
                        openPendingChallengesInfo(pendingChallenge, false)
                    }
                    "voteRecipient", "voteChallenger" -> { // Jump to vote screen
                        if (viewModel.homeActiveFragment.value == ActiveFragment.RECEIVED) {
                            viewModel.hideBottomNav()
                            val action = HomeFragmentDirections.actionNavigationHomeToVoteFragment(pendingChallenge)
                            findNavController().navigate(action)
                        } else {
                            openPendingChallengesInfo(pendingChallenge, true)
                        }
                    }
                    "result", "done" -> { // Open result screen
                        viewModel.hideBottomNav()
                        val action = HomeFragmentDirections.actionNavigationHomeToWinnerFragment(pendingChallenge)
                        findNavController().navigate(action)
                    }
                }


            }
        })

        // Observe viewModel.pendingChallengesLoaded and update recycler view on change
        viewModel.pendingChallengesLoaded.observe(viewLifecycleOwner) { pChLoaded ->
            if (pChLoaded) {
                var challengesList = listOf<PendingChallengeModel>()
                val isActiveFragmentReceivedScreen = viewModel.homeActiveFragment.value == ActiveFragment.RECEIVED
                val isActiveFragmentSentScreen = viewModel.homeActiveFragment.value == ActiveFragment.SENT
                if (isActiveFragmentReceivedScreen) {
                    challengesList = viewModel.pendingChallengeList.value.filter { pCh ->
                        pCh.status != "done" && (
                                (pCh.uidRecipient == viewModel.auth.currentUser!!.uid && pCh.status == "open") // user is recipient and status is open
                                        || (pCh.uidRecipient == viewModel.auth.currentUser!!.uid && pCh.status == "voteRecipient") // user is recipient and status is vote1
                                        || (pCh.uidChallenger == viewModel.auth.currentUser!!.uid && pCh.status == "voteChallenger") // user is challenger and status is vote2
                                        || pCh.status == "result" // status is result
                                )
                    }
                } else if (isActiveFragmentSentScreen) {
                    challengesList = viewModel.pendingChallengeList.value.filter { pCh ->
                        (pCh.uidChallenger == viewModel.auth.currentUser!!.uid && pCh.status == "sent") // user is challenger and status is sent
                                || (pCh.uidChallenger == viewModel.auth.currentUser!!.uid && pCh.status == "voteRecipient") // user is recipient and status is vote1
                                || (pCh.uidRecipient == viewModel.auth.currentUser!!.uid && pCh.status == "voteChallenger") // user is recipient and status is vote1
                                || pCh.status == "done"
                    }
                }

                val sortOrder = listOf("result", "voteRecipient", "voteChallenger", "open", "sent", "done")
                val sortedChallengesList = challengesList.sortedBy { sortOrder.indexOf(it.status) }

                pendingChallengesAdapter.updatePendingChallengeList(sortedChallengesList, viewModel.homeActiveFragment.value!!)
                rvPendingChallenges.visibility = View.VISIBLE
                tvLoadingPendingChallenges.visibility = View.GONE
            }
        }


        return root
    }

    fun openPendingChallengesInfo(pCh: PendingChallengeModel, showOnlyInfo: Boolean) {
        val dialog = PendingChallengeDialogFragment.newInstance(pCh, showOnlyInfo)
        dialog.show(parentFragmentManager, "pendingChallengeDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

enum class ActiveFragment {
    RECEIVED, SENT
}
