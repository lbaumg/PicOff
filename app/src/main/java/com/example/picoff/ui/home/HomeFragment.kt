package com.example.picoff.ui.home

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.SavedStateViewModelFactory
import com.example.picoff.R
import com.example.picoff.databinding.FragmentHomeBinding
import com.example.picoff.ui.dialogs.ChallengeDialogFragment
import com.example.picoff.ui.dialogs.CreateNewChallengeDialogFragment
import com.example.picoff.viewmodels.MainViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var btnSent: Button
    private lateinit var btnReceived: Button

    private val viewModel: MainViewModel by activityViewModels {
        SavedStateViewModelFactory(requireActivity().application, requireActivity())
    }

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

        binding.fabBase.setOnClickListener {
            viewModel.isFabMenuOpen.value = !(viewModel.isFabMenuOpen.value?:false)
        }
        binding.fabCreateNew.setOnClickListener {
            val dialog = CreateNewChallengeDialogFragment.newInstance(true)
            dialog.show(parentFragmentManager, "createNewChallengeDialog")
        }
        binding.fabListChallenges.setOnClickListener {
            viewModel.jumpToChallengeList.value = true
        }
        binding.fabRandomChallenge.setOnClickListener {
            val challenge = viewModel.challengeList.value.random()
            val dialog = ChallengeDialogFragment.newInstance(challenge)
            dialog.show(parentFragmentManager, "challengeDialog")
        }

        viewModel.isFabMenuOpen.observe(viewLifecycleOwner) {
            it?.let { isFabMenuOpen ->
                if (isFabMenuOpen)
                    expandFabMenu()
                else
                    collapseFabMenu()
            }
        }

        // Restore previous active fragment
        if (viewModel.homeActiveFragment.value == ActiveFragment.SENT) {
            onButtonSentClicked(true)
        }

        return view
    }

    private fun onButtonSentClicked(force: Boolean = false) {
        if (viewModel.homeActiveFragment.value == ActiveFragment.RECEIVED || force) {
            btnSent.paintFlags = btnSent.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            btnReceived.paintFlags = btnReceived.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()

            val fragment = PendingChallengeFragment()
            childFragmentManager.commit {
                setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                replace(R.id.fragmentContainerView, fragment)
                addToBackStack(null)
            }
            viewModel.setHomeActiveFragment(ActiveFragment.SENT)
        }
    }

    private fun onButtonReceivedClicked() {
        if (viewModel.homeActiveFragment.value == ActiveFragment.SENT) {
            btnReceived.paintFlags = btnReceived.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            btnSent.paintFlags = btnSent.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()

            val fragment = PendingChallengeFragment()
            childFragmentManager.commit {
                setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                replace(R.id.fragmentContainerView, fragment)
                addToBackStack(null)
            }
//            viewModel.homeActiveFragment = ActiveFragment.RECEIVED
            viewModel.setHomeActiveFragment(ActiveFragment.RECEIVED)
        }
    }

    private fun expandFabMenu() {
        ViewCompat.animate(binding.fabBase).rotation(45.0f).withLayer()
            .setDuration(300).setInterpolator(OvershootInterpolator(10.0f)).start()

        val fabOpenAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        binding.randomChallengeLayout.startAnimation(fabOpenAnimation)
        binding.createNewLayout.startAnimation(fabOpenAnimation)
        binding.listChallengesLayout.startAnimation(fabOpenAnimation)

        binding.fabCreateNew.isClickable = true
        binding.fabRandomChallenge.isClickable = true
        binding.fabListChallenges.isClickable = true
    }

    private fun collapseFabMenu() {
        ViewCompat.animate(binding.fabBase).rotation(0.0f).withLayer()
            .setDuration(300).setInterpolator(OvershootInterpolator(10.0f)).start()

        val fabCloseAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_close)
        binding.createNewLayout.startAnimation(fabCloseAnimation)
        binding.randomChallengeLayout.startAnimation(fabCloseAnimation)
        binding.listChallengesLayout.startAnimation(fabCloseAnimation)

        binding.fabCreateNew.isClickable = false
        binding.fabRandomChallenge.isClickable = false
        binding.fabListChallenges.isClickable = false
    }

    override fun onResume() {
        super.onResume()
        viewModel.showBottomNav()
    }

    override fun onPause() {
        super.onPause()
//        viewModel.homeActiveFragment = ActiveFragment.RECEIVED
//        viewModel.setHomeActiveFragment1(ActiveFragment.RECEIVED)
    }

    override fun onDestroyView() {
        viewModel.isFabMenuOpen.value = null
        super.onDestroyView()
        _binding = null
    }

}
