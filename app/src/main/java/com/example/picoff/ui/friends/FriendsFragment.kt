package com.example.picoff.ui.friends

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.picoff.databinding.FragmentFriendsBinding
import com.example.picoff.ui.SignInActivity
import com.google.firebase.auth.FirebaseAuth

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()

        // Set user name and avatar
        binding.tvAccountName.text = auth.currentUser!!.displayName
        var imgUrl = auth.currentUser!!.photoUrl.toString()
        Glide.with(this).load(imgUrl).into(binding.ivUserAvatar)

        binding.btnLogOut.setOnClickListener {
            // Sign out of google
            auth.signOut()
            Toast.makeText(requireContext(), "Signed out!", Toast.LENGTH_SHORT).show()

            // Launch SignInActivity
            val intent = Intent(requireActivity(), SignInActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}