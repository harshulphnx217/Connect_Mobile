package com.connect.connect

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
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

    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREF_NAME = "myPref"
    private val KEY_APIKEY = "APIKey"

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

        if (apiKey != null) {
            getOtherUserInfo(userId,apiKey)
        }

    }
    // TODO::POSTS FETCH FOR USER!

    private fun getOtherUserInfo(userId:String, apiKey:String){
        val url = "https://connect-api-social.herokuapp.com/user/get-other-user-info"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 200){
                    val resultObj: JSONObject = responseJson.getJSONObject("result")
//                    Log.d("response",response)
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
}