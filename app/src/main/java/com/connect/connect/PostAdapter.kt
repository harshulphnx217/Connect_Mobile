package com.connect.connect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(): RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    /**
     * This is an adapter to Display the post details here we need to pass in the array of the
     * elements as an argument, to the adapater and render the result
     */

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //  TODO:: Here we will be integrating the UI Elements with the data received
    }

    override fun getItemCount(): Int {
        return 10
    }
}