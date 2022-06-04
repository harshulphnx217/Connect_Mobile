package com.connect.connect

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout

class ProfileActivity : AppCompatActivity() {

    private lateinit var logoutLayout: LinearLayout
    private lateinit var backIconProfile: ImageView

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

        logoutLayout.setOnClickListener{
            val editor = sharedPreferences.edit()
            editor.putString(KEY_APIKEY,null)
            editor.apply()
            startActivity(Intent(this,MainActivity::class.java))
        }

        backIconProfile.setOnClickListener{
            startActivity(Intent(this,HomeScreen::class.java))
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this,HomeScreen::class.java))
    }
}