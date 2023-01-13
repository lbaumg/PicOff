package com.example.picoff.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picoff.R
import com.example.picoff.models.PendingChallengeModel
import com.example.picoff.ui.home.ActiveFragment
import com.google.firebase.auth.FirebaseAuth

class PendingChallengesAdapter() :
    RecyclerView.Adapter<PendingChallengesAdapter.ViewHolder>() {

    private var pendingChallengesList: List<PendingChallengeModel> = arrayListOf()
    private lateinit var mListener: OnItemClickListener
    private var activeFragment: ActiveFragment = ActiveFragment.RECEIVED

    private val auth = FirebaseAuth.getInstance()

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_pending_challenge, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPendingChallenge = pendingChallengesList[position]
        holder.tvChallengeTitle.text = currentPendingChallenge.challengeTitle
        val statusText = "Status: ${currentPendingChallenge.status}"
        holder.tvStatus.text = statusText

        // Adjust background color of item according to status
        var color = Color.WHITE
        when (currentPendingChallenge.status) {
            "sent" -> color = ContextCompat.getColor(holder.cvPendingChallengeItem.context, R.color.item_sent)
            "open" -> color = ContextCompat.getColor(holder.cvPendingChallengeItem.context, R.color.item_open)
            "voteRecipient", "voteChallenger" -> color = ContextCompat.getColor(holder.cvPendingChallengeItem.context, R.color.item_vote)
            "result" -> color = ContextCompat.getColor(holder.cvPendingChallengeItem.context, R.color.item_result)
            "done" -> color = ContextCompat.getColor(holder.cvPendingChallengeItem.context, R.color.item_done)
        }
        holder.cvPendingChallengeItem.setCardBackgroundColor(color)

        val isUserChallenger = auth.currentUser!!.uid == currentPendingChallenge.uidChallenger
        var opponentName = currentPendingChallenge.nameChallenger
        var opponentPhotoUrl = currentPendingChallenge.photoUrlChallenger
        if (isUserChallenger) {
            opponentName = currentPendingChallenge.nameRecipient
            opponentPhotoUrl = currentPendingChallenge.photoUrlRecipient
        }

        holder.tvName.text = opponentName
        Glide.with(holder.ivAvatar.context)
            .load(opponentPhotoUrl).into(holder.ivAvatar)
    }

    fun getItemForPosition(position: Int): PendingChallengeModel {
        return pendingChallengesList[position]
    }

    override fun getItemCount(): Int {
        return pendingChallengesList.size
    }

    fun updatePendingChallengeList(newPendingChallengesList: List<PendingChallengeModel>, newActiveFragment: ActiveFragment) {
        activeFragment = newActiveFragment
        pendingChallengesList = newPendingChallengesList
        notifyDataSetChanged()
    }

    class ViewHolder (itemView: View, clickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView){

        val tvChallengeTitle: TextView = itemView.findViewById(R.id.tvPendingChallengeTitle)
        val tvName: TextView = itemView.findViewById(R.id.tvChallengerName)
        val ivAvatar: ImageView = itemView.findViewById(R.id.ivChallengerAvatar)
        val cvPendingChallengeItem: CardView = itemView.findViewById(R.id.cvPendingChallengeItem)
        val tvStatus: TextView = itemView.findViewById(R.id.tvPendingChallengeStatus)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(bindingAdapterPosition)
            }
        }
    }

}