package com.example.picoff.ui.dialogs

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picoff.R
import com.example.picoff.adapters.FriendsAdapter
import com.example.picoff.models.PendingChallengeModel
import com.example.picoff.viewmodels.MainViewModel

class SelectFriendDialogFragment() : DialogFragment() {

    companion object {
        private const val PENDING_CHALLENGE = "pendingChallenge"
        private const val FILE_PATH = "filePath"

        fun newInstance(newPendingChallenge: PendingChallengeModel, filePath: String) = SelectFriendDialogFragment().apply {
            arguments = bundleOf(
                PENDING_CHALLENGE to newPendingChallenge,
                FILE_PATH to filePath
            )
        }
    }

    private lateinit var newPendingChallenge: PendingChallengeModel
    private lateinit var filePath: String

    private lateinit var rvSelectFriend: RecyclerView
    private var friendsAdapter = FriendsAdapter()

    private val viewModel: MainViewModel by activityViewModels {
        SavedStateViewModelFactory(requireActivity().application, requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_select_friend, container, false)

        // Get arguments
        filePath = requireArguments().getString(FILE_PATH)!!
        newPendingChallenge = if (Build.VERSION.SDK_INT >= 33) {
            requireArguments().getParcelable(PENDING_CHALLENGE, PendingChallengeModel::class.java)!!
        } else {
            requireArguments().getParcelable(PENDING_CHALLENGE)!!
        }

        rvSelectFriend = rootView.findViewById(R.id.rvSelectFriend)

        rvSelectFriend.layoutManager = GridLayoutManager(context, 3)
        rvSelectFriend.setHasFixedSize(true)
        rvSelectFriend.adapter = friendsAdapter

        viewModel.friendsLoaded.observe(viewLifecycleOwner) {
            if (it) {
                friendsAdapter.updateData(viewModel.friends.value)
            }
        }

        friendsAdapter.setOnItemClickListener(object : FriendsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val recipient = friendsAdapter.getItemForPosition(position)
                newPendingChallenge.nameRecipient = recipient.displayName
                newPendingChallenge.photoUrlRecipient = recipient.photoUrl
                newPendingChallenge.uidRecipient = recipient.uid
                viewModel.startNewChallenge(
                    newPendingChallenge = newPendingChallenge,
                    filePath = filePath,
                )
            }
        })

        viewModel.statusNewChallengeUploaded.observe(this) { status ->
            status?.let {
                Toast.makeText(
                    context, if (it) "Successfully started challenge!" else "Error: challenge start failed", Toast.LENGTH_SHORT
                ).show()
                viewModel.statusNewChallengeUploaded.value = null
            }
            dismiss()
        }

        return rootView
    }
}
