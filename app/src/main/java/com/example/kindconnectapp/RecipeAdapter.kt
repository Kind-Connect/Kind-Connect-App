package com.example.kindconnectapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(private val recipeList: List<Recipe>) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
        val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
        val ingredientInfo: TextView = itemView.findViewById(R.id.ingredientInfo)
        val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.recipeTitle.text = recipe.title
        holder.ingredientInfo.text = "Used: ${recipe.usedIngredientCount} | Missing: ${recipe.missedIngredientCount}"

        Glide.with(holder.recipeImage.context)
            .load(recipe.image)
            .into(holder.recipeImage)

        val heartRes = if (recipe.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
        holder.favoriteIcon.setImageResource(heartRes)

        holder.favoriteIcon.setOnClickListener {
            recipe.isFavorite = !recipe.isFavorite
            val newIcon = if (recipe.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
            holder.favoriteIcon.setImageResource(newIcon)
        }
        holder.itemView.setOnClickListener {
             val context = holder.itemView.context
             val intent = Intent(context, RecipeDetailActivity::class.java).apply {
                 putExtra("title", recipe.title)
                 putExtra("instructions", recipe.instructions)
                 putExtra("image", recipe.image)
             }
             context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int = recipeList.size
}