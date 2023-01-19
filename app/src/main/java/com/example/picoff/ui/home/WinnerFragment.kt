package com.example.picoff.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.picoff.databinding.FragmentWinnerBinding
import com.example.picoff.models.PendingChallengeModel
import com.example.picoff.viewmodels.MainViewModel

class WinnerFragment : Fragment() {

    private var _binding: FragmentWinnerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels {
        SavedStateViewModelFactory(requireActivity().application, requireActivity())
    }
    private val args: WinnerFragmentArgs by navArgs()

    private lateinit var pendingChallenge: PendingChallengeModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWinnerBinding.inflate(inflater, container, false)
        val view = binding.root

        pendingChallenge = args.pendingChallenge


        Glide.with(this).load(pendingChallenge.challengeImageUrlChallenger).centerCrop().into(binding.ivWinnerVs1)
        Glide.with(this).load(pendingChallenge.challengeImageUrlRecipient).centerCrop().into(binding.ivWinnerVs2)

        binding.tvWinnerVs1.text = pendingChallenge.additionalInfoChallenger.toString()
        binding.tvWinnerVs2.text = pendingChallenge.additionalInfoRecipient.toString()

        val isADraw = pendingChallenge.voteChallenger != pendingChallenge.voteRecipient
        if (isADraw) {
            binding.layoutWinnerVsScreen.setOnClickListener {
                viewModel.currentVoteAndWinnerPage.value = 2
            }
        } else {
            val winnerImgUrl: String?
            val winnerAvatarUrl: String?
            val winnerName: String?
            if (pendingChallenge.voteChallenger == 1) {
                winnerImgUrl = pendingChallenge.challengeImageUrlChallenger
                winnerAvatarUrl = pendingChallenge.photoUrlChallenger
                winnerName = pendingChallenge.nameChallenger
            } else {
                winnerImgUrl = pendingChallenge.challengeImageUrlRecipient
                winnerAvatarUrl = pendingChallenge.photoUrlRecipient
                winnerName = pendingChallenge.nameRecipient
            }
            Glide.with(this).load(winnerImgUrl!!).into(binding.ivImageWinner)
            Glide.with(this).load(winnerAvatarUrl!!).into(binding.ivWinnerAvatar)
            binding.tvWinnerName.text = winnerName

            binding.layoutWinnerVsScreen.setOnClickListener {
                viewModel.currentVoteAndWinnerPage.value = 1
            }

            binding.layoutWinnerScreen.setOnClickListener {
                setChallengeDone()
                closeFragment()
            }
        }

        viewModel.currentVoteAndWinnerPage.observe(viewLifecycleOwner) {
            if (it == 1) {
                binding.layoutWinnerVsScreen.visibility = View.INVISIBLE
                binding.layoutWinnerScreen.visibility = View.VISIBLE
            } else if (it == 2) {
                binding.tvWinnerScreenVs.text = "DRAW!"
                binding.layoutWinnerVsScreen.setOnClickListener {
                    setChallengeDone()
                    closeFragment()
                }
            }
        }

        return view
    }

    private fun setChallengeDone() {
        if (pendingChallenge.status != "done") {
            pendingChallenge.status = "done"
            viewModel.updatePendingChallengeInFirebase(pendingChallenge)
        }
    }

    private fun closeFragment() {
        findNavController().popBackStack()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.currentVoteAndWinnerPage.value = 0
        _binding = null
    }

}
