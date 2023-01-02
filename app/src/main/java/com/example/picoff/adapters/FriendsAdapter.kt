package com.example.picoff.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picoff.R
import com.example.picoff.models.UserModel

class FriendsAdapter :
    RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {
    private var userList: ArrayList<UserModel> = arrayListOf()
    private lateinit var mListener: OnItemClickListener
    private var cardBackground = Color.WHITE
    var isInSearchMode = false

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_friend, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.tvFriendsName.text = currentUser.displayName
        if (currentUser.photoUrl != null) {
            Glide.with(holder.ivFriendsAvatar.context).load(currentUser.photoUrl)
                .into(holder.ivFriendsAvatar)
        }
        holder.cvFriend.setCardBackgroundColor(cardBackground)

    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun getItemForPosition(position: Int): UserModel {
        return userList[position]
    }

    fun updateData(newUserList: ArrayList<UserModel>, backgroundColor: Int = Color.WHITE, searchMode: Boolean = false) {
        isInSearchMode = searchMode
        userList = newUserList
        cardBackground = backgroundColor
        notifyDataSetChanged()
    }

    fun clearSearch() {
        isInSearchMode = false
    }

    class ViewHolder(itemView: View, clickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val cvFriend: CardView = itemView.findViewById(R.id.cvFriend)
        val tvFriendsName: TextView = itemView.findViewById(R.id.tvFriendsName)
        val ivFriendsAvatar: ImageView = itemView.findViewById(R.id.ivFriendsAvatar)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(bindingAdapterPosition)
            }
        }
    }

}