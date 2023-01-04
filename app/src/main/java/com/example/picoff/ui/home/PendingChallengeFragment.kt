package com.example.picoff.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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

    private var selectedPendingChallenge: PendingChallengeModel? = null

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

//        pendingChallengesAdapter.setOnItemLongClickListener(object :
//        PendingChallengesAdapter.OnItemLongClickListener {
//            override fun onItemLongClick(position: Int) {
//                val pendingChallenge = pendingChallengesAdapter.getItemForPosition(position)
//                val dialog = PendingChallengeDialogFragment(pendingChallenge, true)
//                dialog.show(parentFragmentManager, "pendingChallengeDialog")
//            }
//        })
        pendingChallengesAdapter.setOnItemClickListener(object :
            PendingChallengesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val pendingChallenge = pendingChallengesAdapter.getItemForPosition(position)

                when (pendingChallenge.status) {
                    "sent" -> { // Show pending challenge in dialog
                        val dialog = PendingChallengeDialogFragment(pendingChallenge, true)
                        dialog.show(parentFragmentManager, "pendingChallengeDialog")
                    }
                    "open"  -> { // Show pending challenge in dialog
                        val dialog = PendingChallengeDialogFragment(pendingChallenge, false)
                        dialog.show(parentFragmentManager, "pendingChallengeDialog")
                    }
                    "vote", "vote1", "vote2" -> { // Jump to vote screen
                        val intent = Intent(context, VoteActivity::class.java)
                        intent.putExtra("urlImgChallenger", pendingChallenge.challengeImageUrlChallenger)
                        intent.putExtra("urlImgRecipient", pendingChallenge.challengeImageUrlRecipient)
                        selectedPendingChallenge = pendingChallenge
                        voteChallengerLauncher.launch(intent)
                    }
                    "result", "done" -> { // Open result screen
                        viewModel.hideBottomNav()
                        val action = HomeFragmentDirections.actionNavigationHomeToWinnerFragment(pendingChallenge)
                        findNavController().navigate(action)
                    }
                }


            }
        })

        // Observe mainViewModel.pendingChallengesList and update recycler view on change
        lifecycleScope.launch {
            viewModel.pendingChallengesList.collect { pendingChallengesList ->
                println("PENDING CHALLENGES ADAPTER UPDATE")
                var challengesList = listOf<PendingChallengeModel>()
                val isActiveFragmentReceivedScreen = viewModel.homeActiveFragment == ActiveFragment.RECEIVED
                val isActiveFragmentSentScreen = viewModel.homeActiveFragment == ActiveFragment.SENT
                if (isActiveFragmentReceivedScreen) {
                    challengesList = pendingChallengesList.filter {
                        it.status != "done" &&
                                (it.uidRecipient == viewModel.auth.currentUser!!.uid  // user is recipient
                                || (it.uidRecipient == viewModel.auth.currentUser!!.uid && it.status == "vote1") // user is recipient and status is vote1
                                || (it.uidChallenger == viewModel.auth.currentUser!!.uid && it.status == "vote2") // user is challenger and status is vote2
                                || it.status == "result") // status is result
                    }
                } else if (isActiveFragmentSentScreen) {
                    challengesList = pendingChallengesList.filter {
                        (it.uidChallenger == viewModel.auth.currentUser!!.uid && it.status == "sent") // user is challenger and status is sent
                                || (it.uidRecipient == viewModel.auth.currentUser!!.uid && it.status == "vote1") // user is recipient and status is vote1
                                || it.status == "done"
                    }
                }

                val sortOrder = listOf("result", "vote", "vote1", "vote2", "open", "sent", "done")
                val sortedChallengesList = challengesList.sortedBy { sortOrder.indexOf(it.status) }

                pendingChallengesAdapter.updatePendingChallengeList(sortedChallengesList, viewModel.homeActiveFragment)
                rvPendingChallenges.visibility = View.VISIBLE
                tvLoadingPendingChallenges.visibility = View.GONE
            }
        }

        return root
    }

    private val voteChallengerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (selectedPendingChallenge != null) {
                    val vote = result.data?.extras?.getInt("vote")
                    selectedPendingChallenge!!.voteChallenger = vote
                    selectedPendingChallenge!!.status = "result"
                    viewModel.updatePendingChallengeInFirebase(selectedPendingChallenge!!)
                }
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

enum class ActiveFragment {
    RECEIVED, SENT
}
