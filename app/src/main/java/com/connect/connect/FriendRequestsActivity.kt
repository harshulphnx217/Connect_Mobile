package com.connect.connect

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.appbar.MaterialToolbar
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class FriendRequestsActivity : AppCompatActivity() {

    private lateinit var friendRequestToolbar: MaterialToolbar
    private lateinit var friendRequestRecyclerView: RecyclerView

    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREF_NAME = "myPref"
    private val KEY_APIKEY = "APIKey"

    private lateinit var userId:String

    private val friendRequestArray:ArrayList<FriendRequest> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_requests)

        friendRequestToolbar = findViewById(R.id.friend_request_toolbar)
        friendRequestRecyclerView = findViewById(R.id.friend_request_rv)

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE)
        val apiKey = sharedPreferences.getString(KEY_APIKEY, null)

        userId = intent.getStringExtra("user_id").toString()

        if (apiKey != null) {
            getFriendRequests(apiKey)
        }

        friendRequestToolbar.setNavigationOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }
    }

    private fun getFriendRequests(apiKey:String){
        val url = "https://connect-api-social.herokuapp.com/user/get-friend-req-list"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                Log.d("res",response)
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 200){
                    friendRequestArray.clear()
                    val resultArray: JSONArray = responseJson.getJSONArray("result")
                    for(i in 0 until resultArray.length()){
                        val friendReqTemp: JSONObject = resultArray.getJSONObject(i)
                        val friendReqObj = FriendRequest(
                            friendReqTemp.getString("friend_id"),
                            friendReqTemp.getString("user_id"),
                            friendReqTemp.getString("request_date"),
                            friendReqTemp.getString("request_status")
                        )
                        friendRequestArray.add(friendReqObj)
                    }
                    if(friendRequestArray.size == 0){
                        Toast.makeText(this,"No Requests available",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        friendRequestRecyclerView.adapter = FriendRequestItemAdapter(apiKey,friendRequestArray,userId)
                    }
                }
                else{
                    Toast.makeText(this,responseJson.getString("result"), Toast.LENGTH_LONG).show()
                }
            }, Response.ErrorListener {
                    error ->
                Log.d("Volley Error",error.toString())
            }){
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                // Add your parameters in HashMap
                parameters["api_key"] = apiKey
                return parameters
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            TimeUnit.SECONDS.toMillis(20).toInt(),  //After the set time elapses the request will timeout
            1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        SingletonRequestQueue.getInstance(this).addToRequestQueue(stringRequest)
    }
}