package com.example.picoff.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.picoff.databinding.FragmentVoteBinding
import com.example.picoff.models.PendingChallengeModel
import com.example.picoff.viewmodels.MainViewModel

class VoteFragment : Fragment() {

    private var _binding: FragmentVoteBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var pendingChallenge: PendingChallengeModel

    private lateinit var ivImgChallenger: ImageView
    private lateinit var ivImgRecipient: ImageView
    private lateinit var ivVs1: ImageView
    private lateinit var ivVs2: ImageView
    private lateinit var layoutImgChallenger: RelativeLayout
    private lateinit var layoutImgRecipient: RelativeLayout
    private lateinit var layoutVsScreen: RelativeLayout

    private val viewModel: MainViewModel by activityViewModels {
        SavedStateViewModelFactory(requireActivity().application, requireActivity())
    }
    private val args: VoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoteBinding.inflate(inflater, container, false)
        val view = binding.root

        pendingChallenge = args.pendingChallenge

        ivImgChallenger = binding.ivChallengeImageChallenger
        ivImgRecipient = binding.ivChallengeImageRecipient
        ivVs1 = binding.ivChallengeVs1
        ivVs2 = binding.ivChallengeVs2
        layoutVsScreen = binding.layoutVsScreen
        layoutImgChallenger = binding.rlChallengeImageChallenger
        layoutImgRecipient = binding.rlChallengeImageRecipient

        Glide.with(this).load(pendingChallenge.challengeImageUrlChallenger).into(ivImgChallenger)
        Glide.with(this).load(pendingChallenge.challengeImageUrlRecipient).into(ivImgRecipient)
        Glide.with(this).load(pendingChallenge.challengeImageUrlChallenger).centerCrop().into(ivVs1)
        Glide.with(this).load(pendingChallenge.challengeImageUrlRecipient).centerCrop().into(ivVs2)

        binding.tvChallengeImageChallenger.text = pendingChallenge.additionalInfoChallenger
        binding.tvChallengeImageRecipient.text = pendingChallenge.additionalInfoRecipient
        binding.tvChallengeVs1.text = pendingChallenge.additionalInfoChallenger
        binding.tvChallengeVs2.text = pendingChallenge.additionalInfoRecipient

        ivImgChallenger.setOnClickListener {
            viewModel.currentVoteAndWinnerPage.value = 1
        }

        ivImgRecipient.setOnClickListener {
            viewModel.currentVoteAndWinnerPage.value = 2
        }

        viewModel.currentVoteAndWinnerPage.observe(viewLifecycleOwner) {
            if (it == 1) {
                layoutImgChallenger.visibility = View.INVISIBLE
                layoutImgRecipient.visibility = View.VISIBLE
            } else if (it == 2) {
                layoutImgRecipient.visibility = View.INVISIBLE
                layoutVsScreen.visibility = View.VISIBLE
            }
        }


        ivVs1.setOnClickListener { // vote for challenger
            voteAndClose(1)
        }

        ivVs2.setOnClickListener { // vote for recipient
            voteAndClose(2)
        }

        return view
    }

    private fun voteAndClose(vote: Int) {
        if (pendingChallenge.status == "voteRecipient") {
            pendingChallenge.voteRecipient = vote
            pendingChallenge.status = "voteChallenger"
        } else {
            pendingChallenge.voteChallenger = vote
            pendingChallenge.status = "result"
        }
        viewModel.updatePendingChallengeInFirebase(pendingChallenge)

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.currentVoteAndWinnerPage.value = 0
        _binding = null
    }
}
