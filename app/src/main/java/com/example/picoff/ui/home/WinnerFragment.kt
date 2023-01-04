package com.example.picoff.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.picoff.MainViewModel
import com.example.picoff.databinding.FragmentWinnerBinding
import com.example.picoff.models.PendingChallengeModel

class WinnerFragment : Fragment() {

    private var _binding: FragmentWinnerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    val args: WinnerFragmentArgs by navArgs()

    private lateinit var pendingChallenge: PendingChallengeModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWinnerBinding.inflate(inflater, container, false)
        val view = binding.root

        pendingChallenge = args.pendingChallenge

        binding.ivWinnerVs1

        Glide.with(this).load(pendingChallenge.challengeImageUrlChallenger).centerCrop().into(binding.ivWinnerVs1)
        Glide.with(this).load(pendingChallenge.challengeImageUrlRecipient).centerCrop().into(binding.ivWinnerVs2)
        val isADraw = pendingChallenge.voteChallenger != pendingChallenge.voteRecipient
        if (isADraw) {
            binding.tvWinnerScreenVs.text = "DRAW!"
            binding.layoutWinnerVsScreen.setOnClickListener {
                setChallengeDone()
                closeFragment()
            }
        } else {
            val winnerImgUrl = if (pendingChallenge.voteChallenger == 1) pendingChallenge.challengeImageUrlChallenger else pendingChallenge.challengeImageUrlRecipient
            Glide.with(this).load(winnerImgUrl).into(binding.ivImageWinner)

            binding.layoutWinnerVsScreen.setOnClickListener {
                binding.layoutWinnerVsScreen.visibility = View.INVISIBLE
                binding.layoutWinnerScreen.visibility = View.VISIBLE
            }
            binding.layoutWinnerScreen.setOnClickListener {
                setChallengeDone()
                closeFragment()
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
        _binding = null
    }

}
