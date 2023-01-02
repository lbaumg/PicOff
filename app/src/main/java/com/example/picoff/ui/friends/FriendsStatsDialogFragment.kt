package com.example.picoff.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.picoff.R

class FriendsStatsDialogFragment() : DialogFragment() {

    private lateinit var layoutStatsFriendAvatar: LinearLayout
    private lateinit var layoutStatsOwnAvatar: LinearLayout
    private lateinit var ivStatsFriendAvatar: ImageView
    private lateinit var ivStatsOwnAvatar: ImageView
    private lateinit var tvStatsFriendName: TextView
    private lateinit var tvStatsOwnName: TextView
    private lateinit var tvStats: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_friend_stats, container, false)


        layoutStatsFriendAvatar = rootView.findViewById(R.id.layoutStatsFriendAvatar)
        layoutStatsOwnAvatar = rootView.findViewById(R.id.layoutStatsOwnAvatar)
        ivStatsFriendAvatar = rootView.findViewById(R.id.ivStatsFriendAvatar)
        ivStatsOwnAvatar = rootView.findViewById(R.id.ivStatsOwnAvatar)
        tvStatsFriendName = rootView.findViewById(R.id.tvStatsFriendName)
        tvStatsOwnName = rootView.findViewById(R.id.tvStatsOwnName)
        tvStats = rootView.findViewById(R.id.tvStats)

        return rootView
    }

}
