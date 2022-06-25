package com.connect.connect

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.appbar.MaterialToolbar
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AllUsersListActivity : AppCompatActivity() {

    private lateinit var userListToolbar: MaterialToolbar
    private lateinit var userListRecyclerView: RecyclerView

    private val userArray:ArrayList<AllUser> = ArrayList()

    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREF_NAME = "myPref"
    private val KEY_APIKEY = "APIKey"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_users_list)

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE)
        val apiKey = sharedPreferences.getString(KEY_APIKEY, null)

        userListToolbar = findViewById(R.id.user_list_toolbar)
        userListRecyclerView = findViewById(R.id.user_list_rv)

        userListToolbar.setNavigationOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }

        if (apiKey != null) {
            getUserList(apiKey)
        }
    }

    private fun getUserList(apiKey: String){
        val url = "https://connect-api-social.herokuapp.com/user/get-user-list"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 200){
                    val resultArray: JSONArray = responseJson.getJSONArray("result")
                    userArray.clear()
                    for(i in 0 until resultArray.length()){
                        val userTemp: JSONObject = resultArray.getJSONObject(i)
                        val userObj = AllUser(
                            userTemp.getString("user_id"),
                            userTemp.getString("country"),
                            userTemp.getString("profile_pic")
                        )
                        userArray.add(userObj)
                    }
                    userListRecyclerView.adapter = AllUsersAdapter(userArray,apiKey)
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