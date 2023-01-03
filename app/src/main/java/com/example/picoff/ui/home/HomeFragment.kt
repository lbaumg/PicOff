package com.example.picoff.ui.home

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.picoff.MainViewModel
import com.example.picoff.R
import com.example.picoff.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var btnSent: Button
    private lateinit var btnReceived: Button

    private val mainViewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        btnSent = view.findViewById(R.id.buttonSent)
        btnSent.setOnClickListener {
            onButtonSentClicked()
        }

        btnReceived = view.findViewById(R.id.buttonReceived)
        btnReceived.paintFlags = btnReceived.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        btnReceived.setOnClickListener {
            onButtonReceivedClicked()
        }

        return view
    }

    private fun onButtonSentClicked() {
        if (mainViewModel.homeActiveFragment == ActiveFragment.RECEIVED) {
            btnSent.paintFlags = btnSent.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            btnReceived.paintFlags = btnReceived.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()

            val fragment = PendingChallengeFragment()
            childFragmentManager.commit {
                setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                replace(R.id.fragmentContainerView, fragment)
                addToBackStack(null)
            }
            mainViewModel.homeActiveFragment = ActiveFragment.SENT
        }
    }

    private fun onButtonReceivedClicked() {
        if (mainViewModel.homeActiveFragment == ActiveFragment.SENT) {
            btnReceived.paintFlags = btnReceived.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            btnSent.paintFlags = btnSent.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()

            val fragment = PendingChallengeFragment()
            childFragmentManager.commit {
                setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                replace(R.id.fragmentContainerView, fragment)
                addToBackStack(null)
            }
            mainViewModel.homeActiveFragment = ActiveFragment.RECEIVED
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
