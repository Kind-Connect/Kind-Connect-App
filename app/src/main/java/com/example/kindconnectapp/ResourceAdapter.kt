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
    resources: List<Resource>,
    private val onItemClick: (Resource) -> Unit
) : RecyclerView.Adapter<ResourceAdapter.ViewHolder>() {

    private var displayList: MutableList<Resource> = resources.toMutableList()
    private val fullList: MutableList<Resource> = ArrayList(resources)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.resourceImage)
        val name: TextView = itemView.findViewById(R.id.resourceName)
        val description: TextView = itemView.findViewById(R.id.resourceDescription)

        fun bind(resource: Resource) {
            name.text = resource.name
            description.text = resource.description
            image.setImageResource(R.drawable.logo_681f9768_2fbc_4be9_b8a7_ab3797ed3351_removebg_preview_2)

            itemView.setOnClickListener { onItemClick(resource) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_resource, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(displayList[position])
    }

    override fun getItemCount(): Int = displayList.size

    fun filterList(query: String) {
        val lower = query.trim().lowercase()
        displayList = if (lower.isEmpty()) {
            fullList.toMutableList()
        } else {
            fullList.filter { resource ->
                resource.name.lowercase().contains(lower) ||
                        resource.description.lowercase().contains(lower) ||
                        resource.urlOrAddress.lowercase().contains(lower)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }
}