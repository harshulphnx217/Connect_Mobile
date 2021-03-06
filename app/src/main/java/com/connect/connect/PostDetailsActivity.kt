package com.connect.connect


import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var commentRecyclerView: RecyclerView

    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREF_NAME = "myPref"
    private val KEY_APIKEY = "APIKey"
    private lateinit var postCommentBtn : ImageView
    private lateinit var profileImg: CircleImageView
    private lateinit var userNameTv: TextView
    private lateinit var postImageView: ImageView
    private lateinit var postTitle: TextView
    private lateinit var postDesc: TextView
    private lateinit var postLikesTv: TextView
    private lateinit var likeLayout: LinearLayout
    private lateinit var commentLayout: LinearLayout
    private lateinit var commentHereEt: EditText

    private lateinit var noCommentsMsgTv: TextView

    private val commentArray: ArrayList<Comment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE)
        val apiKey = sharedPreferences.getString(KEY_APIKEY, null)

        val postId:String = intent.getStringExtra("post_id").toString()
        postCommentBtn = findViewById(R.id.post_comment_iv)
        profileImg = findViewById(R.id.post_detail_user_profile_pic)
        userNameTv = findViewById(R.id.post_detail_user_name_tv)
        postImageView = findViewById(R.id.post_detail_img)
        postTitle = findViewById(R.id.post_detail_heading_tv)
        postDesc = findViewById(R.id.post_detail_desc_tv)
        likeLayout = findViewById(R.id.like_linear_layout)
        postLikesTv = findViewById(R.id.post_like_text_view)
        commentLayout = findViewById(R.id.comment_linear_layout)
        commentHereEt = findViewById(R.id.comment_here_et)
        noCommentsMsgTv = findViewById(R.id.no_comments_msg_tv)

        postCommentBtn.setOnClickListener{
            if(commentHereEt.text.toString().isEmpty())
            {
                commentHereEt.error="You have not commented yet"
                commentHereEt.requestFocus()
                return@setOnClickListener
            }
            if (apiKey != null) {
                postComment(apiKey,postId,commentHereEt.text.toString())
            }

        }

        if (apiKey != null) {
            getPostDetails(apiKey,postId)
            getComment(apiKey,postId)
        }

       likeLayout.setOnClickListener{
            if (apiKey != null) {
                likePost(apiKey,postId)
            }
            var newLike: Int = Integer.parseInt(postLikesTv.text.toString())
            newLike += 1
           postLikesTv.text = newLike.toString()
        }

        commentLayout.setOnClickListener{
            commentHereEt.requestFocus()
        }

        commentRecyclerView = findViewById(R.id.commentRecyclerView)
    }

    private fun getPostDetails(apiKey: String,postId: String){
        val url = "https://connect-social-api-prod.herokuapp.com/user/fetch_single_post"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 200){
                    val resultObj: JSONObject = responseJson.getJSONObject("result")
//                    Log.d("result",resultObj.toString())
                    userNameTv.text = resultObj.getString("posted_by")
                    postTitle.text = resultObj.getString("post_title")
                    postDesc.text = resultObj.getString("post_desc")

                    postLikesTv.text =  resultObj.getString("number_of_likes")
                    Glide.with(this)
                        .load(resultObj.getString("post_img"))
                        .placeholder(R.drawable.demo_pic)
                        .error(R.drawable.demo_pic)
                        .into(postImageView)

                    Glide.with(this)
                        .load(resultObj.getString("profile_pic"))
                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                        .error(R.drawable.ic_baseline_account_circle_24)
                        .into(profileImg)
                }
                else{
                    Log.d("Result",response)
                    Toast.makeText(this,response, Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                    error ->
                Log.d("Volley Error",error.toString())
            }){
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                // Add your parameters in HashMap
                parameters["api_key"] = apiKey
                parameters["post_id"] = postId
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


    private fun likePost(apiKey:String,postId:String){
        val url = "https://connect-social-api-prod.herokuapp.com/user/like_post"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 201){
                    Log.d("Result","Success")
                }
                else{
                    Log.d("Result",response)
                    Toast.makeText(this,response, Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                    error ->
                Log.d("Volley Error",error.toString())
            }){
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                // Add your parameters in HashMap
                parameters["api_key"] = apiKey
                parameters["post_id"] = postId
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
    private fun postComment(apiKey:String,postId:String,commentMessage:String){
        val url = "https://connect-social-api-prod.herokuapp.com/user/post_comment"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 201){
                    Log.d("Result","Success")
                    Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show()
                    getComment(apiKey,postId)
                    commentHereEt.setText("")
                }
                else{
                    Log.d("Result",response)
                    Toast.makeText(this,response, Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                    error ->
                Log.d("Volley Error",error.toString())
            }){
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                // Add your parameters in HashMap
                parameters["api_key"] = apiKey
                parameters["post_id"] = postId
                parameters["comment_message"] = commentMessage
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

    private fun getComment(apiKey:String,postId:String){
        val url = "https://connect-social-api-prod.herokuapp.com/post/get_comments"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 200){
                    try{
                        val resultArray: JSONArray = responseJson.getJSONArray("result")
                        commentArray.clear()
                        for(i in 0 until resultArray.length()){
                            val commentTemp: JSONObject = resultArray.getJSONObject(i)
                            val commentObj = Comment(
                                commentTemp.getString("user_id"),
                                commentTemp.getString("comment_id"),
                                commentTemp.getString("post_id"),
                                commentTemp.getString("comment_date_time"),
                                commentTemp.getString("comment_message"),
                                commentTemp.getString("user_profile_pic")
                            )
                            commentArray.add(commentObj)
                        }
                        if(commentArray.size == 0){
                            noCommentsMsgTv.visibility= View.VISIBLE
                            commentRecyclerView.visibility = View.GONE
                        }
                        else{
                            commentRecyclerView.adapter = CommentAdapter(commentArray)
                            commentRecyclerView.visibility = View.VISIBLE
                            noCommentsMsgTv.visibility= View.GONE
                        }
                    }catch (e:Exception){
                        noCommentsMsgTv.visibility= View.VISIBLE
                        commentRecyclerView.visibility = View.GONE
                    }
                }
                else{
                    Log.d("Result",response)
                    Toast.makeText(this,response, Toast.LENGTH_SHORT).show()
                    noCommentsMsgTv.visibility= View.VISIBLE
                    commentRecyclerView.visibility = View.GONE
                }
            }, Response.ErrorListener {
                    error ->
                Log.d("Volley Error",error.toString())
            }){
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                // Add your parameters in HashMap
                parameters["api_key"] = apiKey
                parameters["post_id"] = postId
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