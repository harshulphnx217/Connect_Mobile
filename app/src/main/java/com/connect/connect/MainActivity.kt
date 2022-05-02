package com.connect.connect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {

    private lateinit var goToRegister: Button
    private lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goToRegister = findViewById(R.id.register_btn)
        goToRegister.setOnClickListener{
            startActivity(Intent(this,Register::class.java))
        }

        loginBtn = findViewById(R.id.login_btn)
        loginBtn.setOnClickListener{
            startActivity(Intent(this,HomeScreen::class.java))
        }
    }
}