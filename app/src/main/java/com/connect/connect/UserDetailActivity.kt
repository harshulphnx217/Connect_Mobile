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
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.util.concurrent.TimeUnit

class UserDetailActivity : AppCompatActivity() {

    private lateinit var userProfilePic: CircleImageView
    private lateinit var userIdTv: TextView
    private lateinit var bioTv: TextView
    private lateinit var userEmailTv: TextView
    private lateinit var userDobTv: TextView
    private lateinit var userFriendsTv: TextView
    private lateinit var userNameTv: TextView
    private lateinit var postsRv: RecyclerView
    private lateinit var postNotFoundTv:TextView
    private lateinit var userDetailToolbar: MaterialToolbar

    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREF_NAME = "myPref"
    private val KEY_APIKEY = "APIKey"

    private val postArray: ArrayList<Post> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE)
        val apiKey = sharedPreferences.getString(KEY_APIKEY, null)

        val userId:String = intent.getStringExtra("user_id").toString()

        userProfilePic = findViewById(R.id.user_profile_image_view)
        userIdTv = findViewById(R.id.detail_user_id_tv)
        bioTv = findViewById(R.id.detail_bio_tv)
        userEmailTv = findViewById(R.id.user_email_tv)
        userDobTv = findViewById(R.id.user_dob_tv)
        userFriendsTv = findViewById(R.id.user_friends_tv)
        postsRv = findViewById(R.id.posts_rv)
        userNameTv = findViewById(R.id.user_name_tv)
        postNotFoundTv = findViewById(R.id.posts_not_found_tv)
        userDetailToolbar = findViewById(R.id.user_detail_toolbar)

        userDetailToolbar.setNavigationOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }

        if (apiKey != null) {
            getOtherUserInfo(userId,apiKey)
            getPosts(userId,apiKey)
        }
    }

    private fun getOtherUserInfo(userId:String, apiKey:String){
        val url = "https://connect-social-api-prod.herokuapp.com/user/get-other-user-info"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 200){
                    val resultObj: JSONObject = responseJson.getJSONObject("result")
                    userIdTv.text = resultObj.getString("user_id")
                    bioTv.text = resultObj.getString("bio")
                    userNameTv.text = resultObj.getString("first_name") + " " + resultObj.getString("last_name")
                    userEmailTv.text = resultObj.getString("email_address")
                    userDobTv.text = resultObj.getString("dob")
                    userFriendsTv.text = "Friends: " + resultObj.getString("no_of_friends")
                    Glide.with(this)
                        .load(resultObj.getString("profile_pic"))
                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                        .error(R.drawable.ic_baseline_account_circle_24)
                        .override(120,120)
                        .centerCrop()
                        .into(userProfilePic)
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
                parameters["user"] =  userId
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

    private fun getPosts(userId:String,apiKey: String){
        val url = "https://connect-social-api-prod.herokuapp.com/user/fetch-other-user-posts"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
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
                    postsRv.adapter = PostAdapter(postArray,apiKey)
                    if(postArray.size == 0){
                        postsRv.visibility = View.GONE
                        postNotFoundTv.visibility = View.VISIBLE
                    }
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
                parameters["user"] =  userId
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