package com.connect.connect

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit


class UploadPostActivity : AppCompatActivity() {

    private lateinit var postImageView : ImageView
    private lateinit var postHeadingEt : EditText
    private lateinit var postDescEt : EditText
    private lateinit var postBtn : Button

    private lateinit var inputlayout: LinearLayout
    private lateinit var secondaryLayout: LinearLayout

    private lateinit var progressBar:ProgressBar
    private lateinit var successAnimationView: LottieAnimationView
    private lateinit var failedAnimationView: LottieAnimationView
    private lateinit var progressTextView: TextView
    private lateinit var backToHomeBtn: Button

    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREF_NAME = "myPref"
    private val KEY_APIKEY = "APIKey"

    private var imageData: ByteArray? = null

    companion object {
        private const val IMAGE_PICK_CODE = 999
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_post)

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val apiKey = sharedPreferences.getString(KEY_APIKEY, null)

        postImageView=findViewById(R.id.postIv)
        postHeadingEt=findViewById(R.id.post_heading_et)
        postDescEt=findViewById(R.id.post_desc_et)
        postBtn=findViewById(R.id.post_btn)

        inputlayout = findViewById(R.id.input_layout)
        secondaryLayout = findViewById(R.id.secondary_lyt_post)
        progressBar = findViewById(R.id.post_progress_bar)
        successAnimationView = findViewById(R.id.success_animation_post)
        failedAnimationView = findViewById(R.id.failed_animation_post)
        progressTextView = findViewById(R.id.progress_text_post)
        backToHomeBtn = findViewById(R.id.back_to_home_btn_post)

        postImageView.setOnClickListener{
            launchGallery();
        }
        postBtn.setOnClickListener{

            if(imageData == null){
                Toast.makeText(this,"Please Upload a Image",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(postHeadingEt.text.toString().isEmpty()){
                postHeadingEt.error = "This is a required field"
                postHeadingEt.requestFocus()
                return@setOnClickListener
            }
            if(postDescEt.text.toString().isEmpty()){
                postDescEt.error = "This is a required field"
                postDescEt.requestFocus()
                return@setOnClickListener
            }

            if (apiKey != null) {
                uploadPosts(apiKey,postHeadingEt.text.toString(),postDescEt.text.toString())
                inputlayout.visibility = View.GONE
                secondaryLayout.visibility = View.VISIBLE
            }
        }

        backToHomeBtn.setOnClickListener{
            startActivity(Intent(this,HomeScreen::class.java))
        }

    }

    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun uploadPosts(apiKey:String,postHeading:String,postDesc:String){
        val url = "https://connect-api-social.herokuapp.com/user/upload-post"
        val multipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(
            Method.POST,
            url,
            Response.Listener {
                progressBar.visibility = View.GONE
                backToHomeBtn.visibility = View.VISIBLE
                val json = String(it.data)
//                Log.wtf("log",json)
                try{
                    val jsonObject:JSONObject = JSONObject(json)
                    if(jsonObject.getInt("status") == 201){
                        progressTextView.text = "Post Uploaded Successfully!"
                        successAnimationView.visibility = View.VISIBLE
                    }
                    else{
                        failedAnimationView.visibility = View.VISIBLE
                        progressTextView.text = "Post Upload Failed, please try again later!"
                    }
                }catch (e:Exception){
                    failedAnimationView.visibility = View.VISIBLE
                    progressTextView.text = "Post Upload Failed, please try again later!"

                }
            }, Response.ErrorListener {
                failedAnimationView.visibility = View.VISIBLE
                progressTextView.text = "Server Error!"
                Log.d("Volley error",it.toString())
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                // Add your parameters in HashMap
                parameters["api_key"] = apiKey
                parameters["post_title"] = postHeading
                parameters["post_desc"] = postDesc
                return parameters
            }

            override fun getByteData(): Map<String, FileDataPart> {
                val params = HashMap<String, FileDataPart>()
                val title:String = postHeading.replace(" ","-")
                params["post_img"] = FileDataPart(title.toString(), imageData!!, "jpeg")
                return params
            }
        }
        multipartRequest.retryPolicy = DefaultRetryPolicy(
            TimeUnit.SECONDS.toMillis(20).toInt(),  //After the set time elapses the request will timeout
            1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        SingletonRequestQueue.getInstance(baseContext).addToRequestQueue(multipartRequest)
    }

    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val uri = data?.data
            if (uri != null) {
                postImageView.setImageURI(uri)
                createImageData(uri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}