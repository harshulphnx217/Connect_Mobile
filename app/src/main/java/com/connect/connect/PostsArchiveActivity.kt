package com.connect.connect

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.appbar.MaterialToolbar
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class PostsArchiveActivity : AppCompatActivity() {

    private lateinit var materialToolbar: MaterialToolbar
    private lateinit var postArchiveRefreshLayout: SwipeRefreshLayout
    private lateinit var postsArchiveRecyclerView: RecyclerView

    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREF_NAME = "myPref"
    private val KEY_APIKEY = "APIKey"

    private val postArray: ArrayList<Post> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts_archive)

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE)
        val apiKey = sharedPreferences.getString(KEY_APIKEY, null)

        materialToolbar = findViewById(R.id.materialToolbar)
        postArchiveRefreshLayout = findViewById(R.id.post_archive_refresh_layout)
        postsArchiveRecyclerView = findViewById(R.id.post_archive_recycler_view)

        if (apiKey != null) {
            getPosts(apiKey)
        }

        postArchiveRefreshLayout.setOnRefreshListener {
            if (apiKey != null) {
                getPosts(apiKey)
            }
        }

        materialToolbar.setNavigationOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }


    }

    private fun getPosts(apiKey:String){
        val url = "https://connect-api-social.herokuapp.com/user/get-posts"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                postArchiveRefreshLayout.isRefreshing = false
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
                    postsArchiveRecyclerView.adapter = PostAdapter(postArray,apiKey)
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