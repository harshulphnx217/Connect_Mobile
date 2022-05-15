package com.connect.connect

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class HomeScreen : AppCompatActivity() {

    private lateinit var postRecyclerView: RecyclerView
    private lateinit var profileBtn: ImageView

    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREF_NAME = "myPref"
    private val KEY_APIKEY = "APIKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE)
        val apiKey = sharedPreferences.getString(KEY_APIKEY, null)

        profileBtn = findViewById(R.id.profile_btn)

        // TODO:: Change this part it will log out instead go to
        //  profile activity, there clear the shared prefs
        profileBtn.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putString(KEY_APIKEY,null)
            editor.apply()
            startActivity(Intent(this,MainActivity::class.java))
        }

        postRecyclerView = findViewById(R.id.post_recycler_view);
        postRecyclerView.adapter = PostAdapter()

    }
}