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

        holder.removeFriendBtn.setOnClickListener{
//            removeFriend(apiKey,friend.user_id,holder.itemView.context)
            Toast.makeText(holder.itemView.context,"Under Development!",Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    private fun removeFriend(apiKey:String, friendId:String,context: Context){
        val url = "https://connect-social-api-prod.herokuapp.com/user/update-friend-request"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 201){
//                    val resultObj: JSONObject = responseJson.getJSONObject("result")
                    Toast.makeText(context,"Friend Successfully Removed!",Toast.LENGTH_LONG).show()
                }
                else{
                    Log.wtf("res",response)
                    Toast.makeText(context,"Please try again later",Toast.LENGTH_LONG).show()
                }
            }, Response.ErrorListener {
                    error ->
                Log.d("Volley Error",error.toString())
            }){
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                // Add your parameters in HashMap
                parameters["api_key"] = apiKey
                parameters["friend_id"] = friendId
                parameters["request_status"] = "REJECTED"
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