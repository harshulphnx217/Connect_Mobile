package com.connect.connect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class FriendListActivity : AppCompatActivity() {

    private lateinit var friendListToolbar: MaterialToolbar
    private lateinit var searchFriends: androidx.appcompat.widget.SearchView
    private lateinit var friendListRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)
        friendListToolbar = findViewById(R.id.friend_list_toolbar)
        searchFriends = findViewById(R.id.search_friends)
        friendListRecyclerView = findViewById(R.id.friend_list_rv)

        friendListRecyclerView.adapter = FriendListAdapter()

        friendListToolbar.setNavigationOnClickListener{
            startActivity(Intent(this,ProfileActivity::class.java))
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this,ProfileActivity::class.java))
    }
}