package com.connect.connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView

class HomeScreen : AppCompatActivity() {

    private lateinit var postRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        postRecyclerView = findViewById(R.id.post_recycler_view);
        postRecyclerView.adapter = PostAdapter()

    }
}