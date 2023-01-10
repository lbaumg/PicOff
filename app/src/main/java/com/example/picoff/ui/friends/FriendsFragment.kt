package com.example.picoff.ui.friends

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picoff.R
import com.example.picoff.adapters.FriendsAdapter
import com.example.picoff.databinding.FragmentFriendsBinding
import com.example.picoff.models.UserModel
import com.example.picoff.ui.SignInActivity
import com.example.picoff.viewmodels.MainViewModel
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

    private val viewModel: MainViewModel by activityViewModels {
    SavedStateViewModelFactory(requireActivity().application, requireActivity())
}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()

        // Set user name and avatar
        binding.tvAccountName.text = auth.currentUser?.displayName
        val imgUrl = auth.currentUser?.photoUrl.toString()
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

        // Observe status of add friend operation
        viewModel.statusAddFriend.observe(viewLifecycleOwner) { status ->
            status?.let {
                Toast.makeText(
                    context, "Add friend ${if (it) "successful" else " failed"}", Toast.LENGTH_SHORT
                ).show()
                viewModel.statusAddFriend.value = null
            }
        }

        val friendsColor = ContextCompat.getColor(requireContext(), R.color.already_friend)
        friendsAdapter.updateData(viewModel.friends.value, friendsColor)
        friendsAdapter.setOnItemClickListener(object : FriendsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val user = friendsAdapter.getItemForPosition(position)
                if (friendsAdapter.isInSearchMode) {
                    // Add friend
                    val isAlreadyFriend = viewModel.friends.value.contains(user)
                    if (isAlreadyFriend) {
                        Toast.makeText(context, "Already friends!", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.addFriend(user)
                    }
                } else {
                    // Show friends stats
                }
            }
        })

        binding.ibSearchButton.setOnClickListener {
            val name = binding.etSearchFriend.text.toString()
            searchForUser(name)
        }


        binding.btnInviteFriend.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, auth.currentUser!!.displayName)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }

        if (viewModel.sharedUserName.value != null) {
            binding.etSearchFriend.setText(viewModel.sharedUserName.value)
            viewModel.sharedUserName.value = null
        } else {
            binding.etSearchFriend.setText(viewModel.friendsSearchQuery.value)
        }

        if (binding.etSearchFriend.text.isNotBlank()) {
            searchForUser(binding.etSearchFriend.text.toString())
        }

        return root
    }

    private fun searchForUser(name: String) {
        var color = Color.WHITE
        val userList: ArrayList<UserModel> = try {
            val user = viewModel.users.value.first { it.displayName == name }
            val isUserHimself = user.uid == auth.currentUser?.uid
            if (isUserHimself) {
                arrayListOf()
            } else {
                color = ContextCompat.getColor(
                    requireContext(),
                    R.color.search_friend
                )
                arrayListOf(user)
            }
        } catch (exc: NoSuchElementException) {
            arrayListOf()
        }
        friendsAdapter.updateData(userList, color, true)
    }

    override fun onPause() {
        super.onPause()
        viewModel.friendsSearchQuery.value = binding.etSearchFriend.text.toString()
//        binding.etSearchFriend.text.clear()
        friendsAdapter.clearSearch()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}