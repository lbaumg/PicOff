package com.example.picoff.ui.challenges

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.picoff.R

class ChallengesAdapter(private val challengesList: ArrayList<ChallengeModel>) :
    RecyclerView.Adapter<ChallengesAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener) {
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.challenge_list_item, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentChallenge = challengesList[position]
        holder.tvChallengeTitle.text = currentChallenge.challengeTitle
    }

    override fun getItemCount(): Int {
        return challengesList.size
    }

    class ViewHolder (itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView){

        val tvChallengeTitle: TextView = itemView.findViewById(R.id.tvChallengeTitle)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(bindingAdapterPosition)
            }
        }
    }

}