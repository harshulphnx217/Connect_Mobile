package com.connect.connect

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class HomeScreen : AppCompatActivity() {

    private lateinit var postRecyclerView: RecyclerView
    private lateinit var profileBtn: CircleImageView
    private lateinit var homeRefreshLayout: SwipeRefreshLayout
    private lateinit var postNotFoundTv: TextView

    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREF_NAME = "myPref"
    private val KEY_APIKEY = "APIKey"

    private val postArray: ArrayList<Post> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE)
        val apiKey = sharedPreferences.getString(KEY_APIKEY, null)

        postNotFoundTv = findViewById(R.id.post_not_fount_tv)

        profileBtn = findViewById(R.id.profile_btn)

        if (apiKey != null) {
            getPosts(apiKey)
            getProfilePic(apiKey)
        }

        profileBtn.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }

        postRecyclerView = findViewById(R.id.post_recycler_view)
        homeRefreshLayout = findViewById(R.id.home_refresh_layout)

        homeRefreshLayout.setOnRefreshListener {
            if (apiKey != null) {
                getPosts(apiKey)
                getProfilePic(apiKey)
            }
        }

    }

    private fun getPosts(apiKey:String){
        val url = "https://connect-api-social.herokuapp.com/friend/get-posts"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                homeRefreshLayout.isRefreshing = false
                if(responseJson.getInt("status") == 200){
                    val resultArray: JSONArray = responseJson.getJSONArray("result")
                    postArray.clear()
                    for(i in 0 until resultArray.length()){
                        val postTemp: JSONObject = resultArray.getJSONObject(i)
                        val postObj = Post(postTemp.getString("posted_by"),
                            postTemp.getString("post_id"),
                            postTemp.getString("post_title"),
                            postTemp.getString("post_img"),
                            postTemp.getString("post_desc"),
                            postTemp.getString("post_upload_date_time"),
                            postTemp.getString("number_of_likes"),
                            postTemp.getString("number_of_comments"),
                            postTemp.getString("profile_pic"))
                        postArray.add(postObj)
                    }
                    Log.wtf("Post",postArray.size.toString())
                    postRecyclerView.adapter = PostAdapter(postArray,apiKey)
                    if(postArray.size == 0){
                        postRecyclerView.visibility = View.GONE
                        postNotFoundTv.visibility = View.VISIBLE
                    }
                }
                else{
                    Toast.makeText(this,responseJson.getString("result"),Toast.LENGTH_LONG).show()
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

    private fun getProfilePic(apiKey: String){
        val url = "https://connect-api-social.herokuapp.com/user/fetch-personal-profile"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                Log.wtf("res",response)
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 200){
                    val resultObj: JSONObject = responseJson.getJSONObject("result")
                    Glide.with(this)
                        .load(resultObj.getString("profile_pic"))
                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                        .error(R.drawable.ic_baseline_account_circle_24)
                        .override(50,50)
                        .centerCrop()
                        .into(profileBtn)

                }
                else{
//                    Toast.makeText(this,"Invalid API Key",Toast.LENGTH_LONG).show()
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