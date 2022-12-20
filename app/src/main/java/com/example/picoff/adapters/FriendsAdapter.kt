package com.example.picoff.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picoff.R
import com.example.picoff.models.UserModel

class FriendsAdapter :
    RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {
    private var userList: ArrayList<UserModel> = arrayListOf()
    private lateinit var mListener: OnItemClickListener

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
        if (currentUser.photoUrl != null)
            Glide.with(holder.ivFriendsAvatar.context).load(currentUser.photoUrl)
                .into(holder.ivFriendsAvatar)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun getItemForPosition(position: Int): UserModel {
        return userList[position]
    }

    fun updateData(newUserList: ArrayList<UserModel>) {
        userList = newUserList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View, clickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val tvFriendsName: TextView = itemView.findViewById(R.id.tvFriendsName)
        val ivFriendsAvatar: ImageView = itemView.findViewById(R.id.ivFriendsAvatar)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(bindingAdapterPosition)
            }
        }
    }

}