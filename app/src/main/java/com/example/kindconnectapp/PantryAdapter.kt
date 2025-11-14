package com.example.kindconnectapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PantryAdapter(private val items: List<PantryItem>) :
    RecyclerView.Adapter<PantryAdapter.PantryViewHolder>() {

    class PantryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.itemName)
        val desc: TextView = itemView.findViewById(R.id.itemDescription)
        val quantity: TextView = itemView.findViewById(R.id.itemQuantity)
        val expiration: TextView = itemView.findViewById(R.id.itemExpiration)
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
    }

    override fun getItemCount(): Int = items.size
}