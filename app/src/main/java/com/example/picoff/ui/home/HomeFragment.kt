package com.example.picoff.ui.home

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.picoff.R
import com.example.picoff.databinding.FragmentHomeBinding
import com.example.picoff.helpers.OnSwipeTouchListener

class HomeFragment : Fragment() {

    private var onReceiveFragment = true
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var btnSent: Button
    private lateinit var btnReceived: Button
    private lateinit var cvChallengeList: FragmentContainerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        btnSent =  view.findViewById(R.id.buttonSent)
        btnSent.setOnClickListener {
            onButtonSentClicked()
        }

        btnReceived = view.findViewById(R.id.buttonReceived)
        btnReceived.paintFlags = btnReceived.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        btnReceived.setOnClickListener {
            onButtonReceivedClicked()
        }

/*
        cvChallengeList = view.findViewById(R.id.fragmentContainerView)
        cvChallengeList.setOnTouchListener(object: OnSwipeTouchListener(context) {
            override fun onSwipeRight() {
                onButtonSentClicked()
            }

            override fun onSwipeLeft() {
                onButtonReceivedClicked()
            }
        } )
*/

        return view
    }

    private fun onButtonSentClicked() {
        if (onReceiveFragment) {
            btnSent.paintFlags = btnSent.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            btnReceived.paintFlags = btnReceived.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()

            childFragmentManager.commit {
                setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                replace<PendingChallengeFragment>(R.id.fragmentContainerView)
                addToBackStack(null)
            }

            onReceiveFragment = false
        }
    }

    private fun onButtonReceivedClicked() {
        btnReceived.paintFlags = btnReceived.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        btnSent.paintFlags = btnSent.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()

        childFragmentManager.commit {
            setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
            replace<PendingChallengeFragment>(R.id.fragmentContainerView)
            if (!onReceiveFragment) {
                addToBackStack(null)
            }

            onReceiveFragment = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}