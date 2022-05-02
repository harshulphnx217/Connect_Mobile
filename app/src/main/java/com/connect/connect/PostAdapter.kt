package com.connect.connect

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(): RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    /**
     * This is an adapter to Display the post details here we need to pass in the array of the
     * elements as an argument, to the adapter and render the result
     */

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val mainLayout: LinearLayout = view.findViewById(R.id.post_ll)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mainLayout.setOnClickListener{
            val intent = Intent(holder.itemView.context, PostDetailsActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return 10
    }
}