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

class AllUsersAdapter(private val allUserList:ArrayList<AllUser>, private val apiKey:String): RecyclerView.Adapter<AllUsersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val userProfilePic: CircleImageView = view.findViewById(R.id.user_profile_pic)
        val userNameTv: TextView = view.findViewById(R.id.user_name_tv)
        val userCountryTv: TextView = view.findViewById(R.id.user_country_tv)
        val addFriendBtn: ImageView = view.findViewById(R.id.add_friend_btn)
        val mainLayout: LinearLayout = view.findViewById(R.id.main_layout)
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

        holder.mainLayout.setOnClickListener{
            val intent = Intent(holder.itemView.context, UserDetailActivity::class.java)
            holder.itemView.context.startActivity(intent.putExtra("user_id",user.user_id))
        }

        holder.addFriendBtn.setOnClickListener{
            initiateFriendRequest(apiKey,user.user_id,holder.itemView.context)
        }

    }

    override fun getItemCount(): Int {
        return allUserList.size
    }

    private fun initiateFriendRequest(apiKey: String,userId: String,context:Context){
        val url = "https://connect-api-social.herokuapp.com/user/initiate-friend-request"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 201){
                    Toast.makeText(context,"Friend Request Sent!", Toast.LENGTH_LONG).show()
                }
                else{
                    Log.wtf("res",response)
                    Toast.makeText(context,responseJson.getString("result"), Toast.LENGTH_LONG).show()
                }
            }, Response.ErrorListener {
                    error ->
                Log.d("Volley Error",error.toString())
            }){
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                // Add your parameters in HashMap
                parameters["api_key"] = apiKey
                parameters["friend_id"] = userId
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