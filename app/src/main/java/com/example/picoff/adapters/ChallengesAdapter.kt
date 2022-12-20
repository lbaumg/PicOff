package com.example.picoff.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.picoff.R
import com.example.picoff.models.ChallengeModel

class ChallengesAdapter :
    RecyclerView.Adapter<ChallengesAdapter.ViewHolder>() {
    private var challengesList: ArrayList<ChallengeModel> = arrayListOf()
    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_challenge, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentChallenge = challengesList[position]
        holder.tvChallengeTitle.text = currentChallenge.challengeTitle
    }

    override fun getItemCount(): Int {
        return challengesList.size
    }

    fun getItemForPosition(position: Int): ChallengeModel {
        return challengesList[position]
    }

    fun updateChallengeList(newChallengesList: ArrayList<ChallengeModel>) {
        challengesList = newChallengesList
        notifyDataSetChanged()
    }

    class ViewHolder (itemView: View, clickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView){

        val tvChallengeTitle: TextView = itemView.findViewById(R.id.tvChallengeTitle)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(bindingAdapterPosition)
            }
        }
    }

}