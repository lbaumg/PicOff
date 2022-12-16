package com.example.picoff.ui.friends

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.picoff.MainActivity
import com.example.picoff.MainViewModel
import com.example.picoff.R
import com.example.picoff.SignInActivity
import com.example.picoff.databinding.FragmentFriendsBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var tvAccountName: TextView
    private lateinit var auth: FirebaseAuth
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()

        binding.btnLogOut.setOnClickListener {
            // Sign out of google
            auth.signOut()
            Toast.makeText(requireContext(), "Signed out!", Toast.LENGTH_SHORT).show()

            // Update account data in preferences datastore
            mainViewModel.updateAccount(null, null, null, null, false)

            // Launch SignInActivity
            val intent = Intent(requireActivity(), SignInActivity::class.java)
            (activity as MainActivity).resultLauncher.launch(intent)
        }

        tvAccountName = root.findViewById(R.id.tvAccountName)
        tvAccountName.text =
            if (mainViewModel.isLoggedIn.value == true) "Logged in as " + "${mainViewModel.accountName.value}"
            else "Not logged in"
        println("FRIENDS: ${mainViewModel.accountName.value}")

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}