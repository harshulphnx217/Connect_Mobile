package com.connect.connect

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class FriendRequestItemAdapter(private val apiKey:String,private val friendRequestList:ArrayList<FriendRequest>,private val userId:String):RecyclerView.Adapter<FriendRequestItemAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val reqProfilePic: CircleImageView = view.findViewById(R.id.req_profile_pic)
        val reqNameTv:TextView = view.findViewById(R.id.req_name_tv)
        val requestedToTv: TextView = view.findViewById(R.id.requested_to_tv)
        val requestStatusTv: TextView = view.findViewById(R.id.req_status_tv)
        val requestAcceptBtn: MaterialButton = view.findViewById(R.id.req_accept_btn)
        val requestRejectBtn: MaterialButton = view.findViewById(R.id.req_reject_btn)
        val requestDateTv: TextView = view.findViewById(R.id.req_date_tv)
        val btnLayout: LinearLayout = view.findViewById(R.id.req_btn_layout)
        val msg:TextView = view.findViewById(R.id.msg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.friend_request_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friendRequestItem: FriendRequest = friendRequestList[position]
        holder.reqNameTv.text = "From: " + friendRequestItem.userId
        holder.requestedToTv.text = "To: " + friendRequestItem.friendId
        holder.requestStatusTv.text = "Status: "+friendRequestItem.requestStatus
        holder.requestDateTv.text = "Requested on: " + friendRequestItem.requestDate

        if (friendRequestItem.requestStatus == "REJECTED" || friendRequestItem.requestStatus == "ACCEPTED" || friendRequestItem.userId.toString().trim() == userId){
            holder.btnLayout.visibility = View.GONE
            if(friendRequestItem.requestStatus == "REJECTED"){
                holder.msg.visibility = View.VISIBLE
            }
            else{
                holder.msg.visibility = View.GONE
            }
        }
        else{
            holder.btnLayout.visibility = View.VISIBLE
        }

        holder.requestRejectBtn.setOnClickListener {
            updateFriendRequest(apiKey,friendRequestItem.userId,holder.itemView.context,"REJECTED")
            holder.btnLayout.visibility = View.GONE
        }

        holder.requestAcceptBtn.setOnClickListener {
            updateFriendRequest(apiKey,friendRequestItem.userId,holder.itemView.context,"ACCEPTED")
            holder.btnLayout.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return friendRequestList.size
    }

    private fun updateFriendRequest(apiKey:String, friendId:String,context: Context,requestStatus:String){
        val url = "https://connect-social-api-prod.herokuapp.com/user/update-friend-request"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 201){
//                    val resultObj: JSONObject = responseJson.getJSONObject("result")
                    Toast.makeText(context,"Completed!", Toast.LENGTH_LONG).show()
                }
                else{
                    Log.wtf("res",response)
                    Toast.makeText(context,"Please try again later", Toast.LENGTH_LONG).show()
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
                parameters["request_status"] = requestStatus
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