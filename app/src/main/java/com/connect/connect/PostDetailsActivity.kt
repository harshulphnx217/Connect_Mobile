package com.connect.connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var commentRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        commentRecyclerView = findViewById(R.id.commentRecyclerView)
        commentRecyclerView.adapter = CommentAdapter()
    }
}