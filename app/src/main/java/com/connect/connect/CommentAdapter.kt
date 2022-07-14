package com.connect.connect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CommentAdapter(private val commentList: ArrayList<Comment>): RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val commentUserProfile: ImageView = view.findViewById(R.id.comment_user_profile_pic)
        val commentUserName: TextView = view.findViewById(R.id.comment_user_name_tv)
        val commentMessage: TextView = view.findViewById(R.id.comment_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val commentItem: Comment = commentList[position]
        holder.commentUserName.text = commentItem.userId
        holder.commentMessage.text = commentItem.commentMessage

        Glide.with(holder.itemView.context)
            .load(commentItem.userProfilePicLink)
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .error(R.drawable.ic_baseline_account_circle_24)
            .into(holder.commentUserProfile)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}