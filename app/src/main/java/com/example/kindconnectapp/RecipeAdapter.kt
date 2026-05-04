package com.example.kindconnectapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import com.google.gson.Gson
import java.io.IOException

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

        val heartRes = if (recipe.isFavorite) R.drawable.ic_heart_filled
        else R.drawable.ic_heart_outline
        holder.favoriteIcon.setImageResource(heartRes)

        holder.favoriteIcon.setOnClickListener {
            val prefs = holder.itemView.context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val name = prefs.getString("name", "User") ?: "User"

            val db = FirebaseFirestore.getInstance()
            val favoritesRef = db.collection("users").document(name).collection("favorites")

            Log.d("RecipeAdapter", "Saving favorite for user=$name, recipeId=${recipe.id}")

            recipe.isFavorite = !recipe.isFavorite
            val newIcon = if (recipe.isFavorite) R.drawable.ic_heart_filled
            else R.drawable.ic_heart_outline
            holder.favoriteIcon.setImageResource(newIcon)


            if (recipe.isFavorite) {
                val recipeData = hashMapOf(
                    "id" to recipe.id,
                    "title" to recipe.title,
                    "image" to recipe.image,
                    "usedIngredientCount" to (recipe.usedIngredientCount ?: 0),
                    "missedIngredientCount" to recipe.missedIngredientCount,
                    "summary" to recipe.summary,
                    "instructions" to recipe.instructions,
                    "isFavorite" to recipe.isFavorite
                )
                favoritesRef.document(recipe.id.toString()).set(recipeData)
            } else {
                favoritesRef.document(recipe.id.toString()).delete()
            }
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
    private fun fetchFullRecipeDetails(id: Int, callback: (Recipe) -> Unit) {
        val apiKey = "5df89a2562b54f9fbfde47f5e19ac90a"
        val url = "https://api.spoonacular.com/recipes/$id/information?apiKey=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("DETAIL_ERROR", "Failed to fetch recipe details", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { json ->
                    val gson = Gson()
                    val detailedRecipe = gson.fromJson(json, Recipe::class.java)
                    callback(detailedRecipe)
                }
            }
        })
    }

}