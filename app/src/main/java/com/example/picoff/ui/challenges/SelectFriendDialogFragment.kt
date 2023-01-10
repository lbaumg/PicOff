package com.example.picoff.ui.challenges

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picoff.R
import androidx.lifecycle.SavedStateViewModelFactory
import com.example.picoff.adapters.FriendsAdapter
import com.example.picoff.models.PendingChallengeModel
import com.example.picoff.viewmodels.MainViewModel

class SelectFriendDialogFragment(
    val newPendingChallenge: PendingChallengeModel,
    val bitmap: Bitmap
) : DialogFragment() {

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

        rvSelectFriend = rootView.findViewById(R.id.rvSelectFriend)

        rvSelectFriend.layoutManager = GridLayoutManager(context, 3)
        rvSelectFriend.setHasFixedSize(true)
        rvSelectFriend.adapter = friendsAdapter

        friendsAdapter.updateData(viewModel.friends.value)
        friendsAdapter.setOnItemClickListener(object : FriendsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val recipient = friendsAdapter.getItemForPosition(position)
                newPendingChallenge.nameRecipient = recipient.displayName
                newPendingChallenge.photoUrlRecipient = recipient.photoUrl
                newPendingChallenge.uidRecipient = recipient.uid
                viewModel.startNewChallenge(
                    newPendingChallenge = newPendingChallenge,
                    bitmap = bitmap,
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
