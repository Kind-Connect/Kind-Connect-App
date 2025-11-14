package com.example.kindconnectapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResourceAdapter(
    private val context: Context,
    private val resources: List<Resource>
) : RecyclerView.Adapter<ResourceAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.resourceImage)
        val name: TextView = itemView.findViewById(R.id.resourceName)
        val description: TextView = itemView.findViewById(R.id.resourceDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_resource, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resource = resources[position]

        holder.name.text = resource.name
        holder.description.text = resource.description

        // Just show a static drawable for now
        holder.image.setImageResource(R.drawable.logo_681f9768_2fbc_4be9_b8a7_ab3797ed3351_removebg_preview_2)
    }

    override fun getItemCount(): Int = resources.size
}