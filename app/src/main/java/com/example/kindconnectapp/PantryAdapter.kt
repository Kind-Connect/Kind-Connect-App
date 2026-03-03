package com.example.kindconnectapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class PantryAdapter(private val items: MutableList<PantryItem>,private val onRemoveClick: (PantryItem) -> Unit) :
    RecyclerView.Adapter<PantryAdapter.PantryViewHolder>() {

    class PantryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.itemName)
        val desc: TextView = itemView.findViewById(R.id.itemDescription)
        val quantity: TextView = itemView.findViewById(R.id.itemQuantity)
        val expiration: TextView = itemView.findViewById(R.id.itemExpiration)
        val btnRemove: ImageView = itemView.findViewById(R.id.btnRemove)
        val image: ImageView = itemView.findViewById(R.id.itemImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PantryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pantry, parent, false)
        return PantryViewHolder(view)
    }

    override fun onBindViewHolder(holder: PantryViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.desc.text = item.description
        holder.quantity.text = "Qty: ${item.quantity}"
        holder.expiration.text = "Expires: ${item.expirationDate}"

        Glide.with(holder.itemView.context)
            .load(item.imageUrl?.let { File(it) })
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.image)

        // Set click listener for the Remove button
        holder.btnRemove.setOnClickListener {
            onRemoveClick(item)   // delegate to fragment
        }
    }
    override fun getItemCount(): Int = items.size
}