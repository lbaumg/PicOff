package com.example.picoff.ui.challenges

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picoff.MainViewModel
import com.example.picoff.R
import com.example.picoff.adapters.FriendsAdapter

class SelectFriendDialogFragment(val challengeTitle: String, val challengeDesc: String, val bitmap: Bitmap, val additionalInfo: String) : DialogFragment() {

    private lateinit var rvSelectFriend: RecyclerView

    private var friendsAdapter = FriendsAdapter()

    private val viewModel: MainViewModel by activityViewModels()

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
                val user = friendsAdapter.getItemForPosition(position)
                viewModel.startNewChallenge(
                    challengeTitle = challengeTitle,
                    challengeDesc = challengeDesc,
                    recipient = user,
                    bitmap = bitmap,
                    additionalInfo = additionalInfo
                )
                dismiss()
            }
        })

        return rootView
    }


//    override fun onStart() {
//        super.onStart()
//        if (dialog != null) {
//            val width = ViewGroup.LayoutParams.MATCH_PARENT;
//            val height = ViewGroup.LayoutParams.MATCH_PARENT;
//            dialog!!.window?.setLayout(width, height);
//
//        }
//    }


}
