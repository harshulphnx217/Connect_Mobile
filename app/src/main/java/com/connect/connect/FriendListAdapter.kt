package com.connect.connect

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class FriendListAdapter(private val friendList:ArrayList<Friend>, private val apiKey:String) : RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {
    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val friendProfilePic: CircleImageView = view.findViewById(R.id.friend_user_profile_pic)
        val friendUserNameTv: TextView = view.findViewById(R.id.friend_user_name_tv)
        val friendCountryTv: TextView = view.findViewById(R.id.friend_country_tv)
        val removeFriendBtn: ImageView = view.findViewById(R.id.remove_friend_btn)
        val friendsLayout: LinearLayout = view.findViewById(R.id.friends_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.friend_list_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend: Friend = friendList[position]
        holder.friendUserNameTv.text = friend.user_id
        holder.friendCountryTv.text = friend.country

        Glide.with(holder.itemView.context)
            .load(friend.profileImgLink)
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .error(R.drawable.ic_baseline_account_circle_24)
            .into(holder.friendProfilePic)

        holder.friendsLayout.setOnClickListener{
            val intent = Intent(holder.itemView.context, UserDetailActivity::class.java)
            holder.itemView.context.startActivity(intent.putExtra("user_id",friend.user_id))
        }
    }

    override fun getItemCount(): Int {
        return friendList.size
    }
}