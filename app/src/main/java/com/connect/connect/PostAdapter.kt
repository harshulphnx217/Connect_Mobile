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

class PostAdapter(private val postList: ArrayList<Post>): RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    /**
     * This is an adapter to Display the post details here we need to pass in the array of the
     * elements as an argument, to the adapter and render the result
     */

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val mainLayout: LinearLayout = view.findViewById(R.id.post_ll)
        val userProfilePic: CircleImageView = view.findViewById(R.id.post_user_profile_pic)
        val userName: TextView = view.findViewById(R.id.post_user_name_tv)
        val postImg: ImageView = view.findViewById(R.id.post_img)
        val postTitle: TextView = view.findViewById(R.id.post_title)
        val postLikes: TextView = view.findViewById(R.id.post_like_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val postItem:Post = postList[position]

        holder.postTitle.text = postItem.postTitle
        holder.userName.text = postItem.userName
        holder.postLikes.text =  postItem.noOfLikes  + " Likes"
        Glide.with(holder.itemView.context)
            .load(postItem.postImgLink)
            .placeholder(R.drawable.demo_pic)
            .error(R.drawable.demo_pic)
            .into(holder.postImg)

        Glide.with(holder.itemView.context)
            .load(postItem.profileImgLink)
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .error(R.drawable.ic_baseline_account_circle_24)
            .into(holder.userProfilePic)


        holder.mainLayout.setOnClickListener{
            val intent = Intent(holder.itemView.context, PostDetailsActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}