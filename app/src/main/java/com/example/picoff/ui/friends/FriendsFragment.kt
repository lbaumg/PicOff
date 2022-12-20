package com.example.picoff.ui.friends

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picoff.MainViewModel
import com.example.picoff.R
import com.example.picoff.adapters.FriendsAdapter
import com.example.picoff.databinding.FragmentFriendsBinding
import com.example.picoff.ui.SignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private lateinit var rvFriends: RecyclerView
    private val friendsAdapter = FriendsAdapter()

    private val mainViewModel: MainViewModel by activityViewModels()

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
        val imgUrl = auth.currentUser!!.photoUrl.toString()
        Glide.with(this).load(imgUrl).into(binding.ivUserAvatar)

        binding.btnLogOut.setOnClickListener {
            // Sign out of google
            auth.signOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
            googleSignInClient.signOut()

            Toast.makeText(requireContext(), "Signed out!", Toast.LENGTH_SHORT).show()

            // Launch SignInActivity
            val intent = Intent(requireActivity(), SignInActivity::class.java)
            startActivity(intent)
        }

        // Setup friends recycler view
        rvFriends = root.findViewById(R.id.rvFriends)
        rvFriends.layoutManager = GridLayoutManager(context, 3)
        rvFriends.setHasFixedSize(true)
        rvFriends.adapter = friendsAdapter

        friendsAdapter.updateData(mainViewModel.users.value)
        friendsAdapter.setOnItemClickListener(object : FriendsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // TODO add friend
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}