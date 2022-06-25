package com.connect.connect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class AllUsersAdapter(private val allUserList:ArrayList<AllUser>, private val apiKey:String): RecyclerView.Adapter<AllUsersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val userProfilePic: CircleImageView = view.findViewById(R.id.user_profile_pic)
        val userNameTv: TextView = view.findViewById(R.id.user_name_tv)
        val userCountryTv: TextView = view.findViewById(R.id.user_country_tv)
        val addFriendBtn: ImageView = view.findViewById(R.id.add_friend_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.all_user_list_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: AllUser = allUserList[position]
        holder.userNameTv.text = user.user_id
        holder.userCountryTv.text = user.country

        Glide.with(holder.itemView.context)
            .load(user.profileImgLink)
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .error(R.drawable.ic_baseline_account_circle_24)
            .into(holder.userProfilePic)

    }

    override fun getItemCount(): Int {
        return allUserList.size
    }
}