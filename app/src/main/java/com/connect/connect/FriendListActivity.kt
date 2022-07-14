package com.connect.connect

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.appbar.MaterialToolbar
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class FriendListActivity : AppCompatActivity() {

    private lateinit var friendListToolbar: MaterialToolbar
    private lateinit var searchFriends: androidx.appcompat.widget.SearchView
    private lateinit var friendListRecyclerView: RecyclerView

    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREF_NAME = "myPref"
    private val KEY_APIKEY = "APIKey"

    private val friendArray: ArrayList<Friend> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE)
        val apiKey = sharedPreferences.getString(KEY_APIKEY, null)

        friendListToolbar = findViewById(R.id.friend_list_toolbar)
        searchFriends = findViewById(R.id.search_friends)
        friendListRecyclerView = findViewById(R.id.friend_list_rv)

        if (apiKey != null) {
            getFriendsList(apiKey)
        }

        friendListToolbar.setNavigationOnClickListener{
            startActivity(Intent(this,ProfileActivity::class.java))
        }

    }

    private fun getFriendsList(apiKey: String){
        val url = "https://connect-social-api-prod.herokuapp.com/user/get-friends-list"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 200){
                    val resultArray: JSONArray = responseJson.getJSONArray("result")
                    friendArray.clear()
                    for(i in 0 until resultArray.length()){
                        val friendTemp: JSONObject = resultArray.getJSONObject(i)
                        val friendObj = Friend(
                            friendTemp.getString("friend_id"),
                            friendTemp.getString("country"),
                            friendTemp.getString("profile_pic")
                        )
                        friendArray.add(friendObj)
                    }
                    friendListRecyclerView.adapter = FriendListAdapter(friendArray,apiKey)
                }
                else{
                    Toast.makeText(this,"Invalid API Key", Toast.LENGTH_LONG).show()
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

    override fun onBackPressed() {
        startActivity(Intent(this,ProfileActivity::class.java))
    }
}