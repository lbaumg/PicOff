package com.example.picoff.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.picoff.R
import com.example.picoff.models.PendingChallengeModel

class PendingChallengesAdapter :
    RecyclerView.Adapter<PendingChallengesAdapter.ViewHolder>() {

    private var pendingChallengesList: ArrayList<PendingChallengeModel> = arrayListOf()
    private lateinit var mListener: OnItemClickListener

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
        holder.tvCreatorName.text = currentPendingChallenge.nameChallenger
    }

    override fun getItemCount(): Int {
        return pendingChallengesList.size
    }

    fun updatePendingChallengeList(newPendingChallengesList: ArrayList<PendingChallengeModel>) {
        pendingChallengesList = newPendingChallengesList
        notifyDataSetChanged()
    }

    class ViewHolder (itemView: View, clickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView){

        val tvChallengeTitle: TextView = itemView.findViewById(R.id.tvPendingChallengeTitle)
        val tvCreatorName: TextView = itemView.findViewById(R.id.tvChallengerName)
        val ivCreatorAvatar: ImageView = itemView.findViewById(R.id.ivChallengerAvatar)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(bindingAdapterPosition)
            }
        }
    }

}