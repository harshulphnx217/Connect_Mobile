package com.connect.connect

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import org.json.JSONObject
import org.w3c.dom.Text
import java.util.concurrent.TimeUnit

class ProfileActivity : AppCompatActivity() {

    private lateinit var logoutLayout: LinearLayout
    private lateinit var profilePostArchiveLayout: LinearLayout
    private lateinit var changePasswordLayout: LinearLayout

    private lateinit var backIconProfile: ImageView
    private lateinit var profileImageView: ImageView

    private lateinit var userIdTv: TextView
    private lateinit var bioTv: TextView
    private lateinit var profileNameTv: TextView
    private lateinit var profilePhoneTv: TextView
    private lateinit var profileEmailTv: TextView
    private lateinit var profileDobTv: TextView
    private lateinit var profileCountryTv: TextView
    private lateinit var profileFriendsTv: TextView

    private lateinit var friendListLinearLayout: LinearLayout


    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREF_NAME = "myPref"
    private val KEY_APIKEY = "APIKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE)
        val apiKey = sharedPreferences.getString(KEY_APIKEY, null)

        logoutLayout = findViewById(R.id.logout_layout)
        backIconProfile = findViewById(R.id.back_icon_profile)
        changePasswordLayout = findViewById(R.id.change_password_layout)
        userIdTv = findViewById(R.id.user_id_tv)
        bioTv = findViewById(R.id.bio_tv)
        profileNameTv = findViewById(R.id.profile_name_tv)
        profilePhoneTv = findViewById(R.id.profile_phone_tv)
        profileEmailTv = findViewById(R.id.profile_email_tv)
        profileDobTv = findViewById(R.id.profile_dob_tv)
        profileCountryTv = findViewById(R.id.profile_country_tv)
        profileFriendsTv = findViewById(R.id.profile_friends_tv)
        profileImageView = findViewById(R.id.profile_image_view)
        profilePostArchiveLayout = findViewById(R.id.profile_post_archive_layout)
        friendListLinearLayout = findViewById(R.id.friend_list_ll)

        if (apiKey != null) {
            getUserInfo(apiKey)
        }

        logoutLayout.setOnClickListener{
            val editor = sharedPreferences.edit()
            editor.putString(KEY_APIKEY,null)
            editor.apply()
            startActivity(Intent(this,MainActivity::class.java))
        }

        backIconProfile.setOnClickListener{
            startActivity(Intent(this,HomeScreen::class.java))
        }

        changePasswordLayout.setOnClickListener {
            startActivity(Intent(this,ChangePassword::class.java))
        }

        profilePostArchiveLayout.setOnClickListener {
            startActivity(Intent(this,PostsArchiveActivity::class.java))
        }

        friendListLinearLayout.setOnClickListener {
            startActivity(Intent(this,FriendListActivity::class.java))
        }

    }
    // user/get-user-info
    private fun getUserInfo(apiKey:String){
        val url = "https://connect-api-social.herokuapp.com/user/get-user-info"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 200){
                    val resultObj: JSONObject = responseJson.getJSONObject("result")
                    Log.d("response",response)
                    userIdTv.text = resultObj.getString("user_id")
                    bioTv.text = resultObj.getString("bio")
                    profileNameTv.text = resultObj.getString("first_name") + " " + resultObj.getString("last_name")
                    profilePhoneTv.text = resultObj.getString("phone_number")
                    profileEmailTv.text = resultObj.getString("email_address")
                    profileCountryTv.text = resultObj.getString("country")
                    profileDobTv.text = resultObj.getString("dob")
                    profileFriendsTv.text = "Friends: " + resultObj.getString("no_of_friends")
                    Glide.with(this)
                        .load(resultObj.getString("profile_pic"))
                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                        .error(R.drawable.ic_baseline_account_circle_24)
                        .override(120,120)
                        .centerCrop()
                        .into(profileImageView)
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
        startActivity(Intent(this,HomeScreen::class.java))
    }
}