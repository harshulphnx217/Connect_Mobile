package com.connect.connect

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class PostAdapter(private val postList: ArrayList<Post>,private val apiKey:String): RecyclerView.Adapter<PostAdapter.ViewHolder>() {

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
        val likeLayout: LinearLayout = view.findViewById(R.id.like_linear_layout)
        val commentLyt : LinearLayout = view.findViewById(R.id.comment_linear_layout)
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
        holder.postLikes.text =  postItem.noOfLikes

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
            holder.itemView.context.startActivity(intent.putExtra("post_id",postItem.postId))
        }

        holder.commentLyt.setOnClickListener{
            val intent = Intent(holder.itemView.context, PostDetailsActivity::class.java)
            holder.itemView.context.startActivity(intent.putExtra("post_id",postItem.postId))
        }

        holder.likeLayout.setOnClickListener{
            likePost(postItem.postId,holder.itemView.context)
            var newLike: Int = Integer.parseInt(holder.postLikes.text.toString())
            newLike += 1
            holder.postLikes.text = newLike.toString()
        }

    }

    override fun getItemCount(): Int {
        return postList.size
    }

    private fun likePost(postId:String,context:Context){
        val url = "https://connect-api-social.herokuapp.com/user/like_post"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 200){
                    Log.d("Result","Success")
                }
                else{
                    Log.d("Result",response)
                    Toast.makeText(context,response,Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                    error ->
                Log.d("Volley Error",error.toString())
            }){
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                // Add your parameters in HashMap
                parameters["api_key"] = apiKey
                parameters["post_id"] = postId
                return parameters
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            TimeUnit.SECONDS.toMillis(20).toInt(),  //After the set time elapses the request will timeout
            1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        SingletonRequestQueue.getInstance(context).addToRequestQueue(stringRequest)
    }
}