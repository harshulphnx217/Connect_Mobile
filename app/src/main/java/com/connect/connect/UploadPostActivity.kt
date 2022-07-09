package com.connect.connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.SharedPreferences
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView

class UploadPostActivity : AppCompatActivity() {

    private lateinit var post : ImageView
    private lateinit var post_heading_et : EditText
    private lateinit var post_desc_et : EditText
    private lateinit var post_btn : Button

    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREF_NAME = "myPref"
    private val KEY_APIKEY = "APIKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_post)

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val apiKey = sharedPreferences.getString(KEY_APIKEY, null)

        post=findViewById(R.id.post)
        post_heading_et=findViewById(R.id.post_heading_et)
        post_desc_et=findViewById(R.id.post_desc_et)
        post_btn=findViewById(R.id.post_btn)

        post.setOnClickListener{

        }
        post_heading_et.setOnClickListener{

        }
        post_desc_et.setOnClickListener{

        }
        post_btn.setOnClickListener{

        }

    }
}