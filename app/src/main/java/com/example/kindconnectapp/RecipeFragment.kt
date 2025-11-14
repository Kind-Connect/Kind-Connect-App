package com.example.kindconnectapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class RecipeFragment : Fragment() {

    private lateinit var pantryInput: EditText
    private lateinit var generateButton: Button
    private lateinit var recipeRecyclerView: RecyclerView

    private val client = OkHttpClient()
    private val gson = Gson()
    private val apiKey = "5df89a2562b54f9fbfde47f5e19ac90a"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recipe_generator_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pantryInput = view.findViewById(R.id.pantryInput)
        generateButton = view.findViewById(R.id.generateButton)
        recipeRecyclerView = view.findViewById(R.id.recipeRecyclerView)

        recipeRecyclerView.layoutManager = LinearLayoutManager(context)

        generateButton.setOnClickListener {
            val ingredients = pantryInput.text.toString()
            if (ingredients.isNotBlank()) {
                fetchRecipes(ingredients)
            }
        }
    }

    private fun fetchRecipes(ingredients: String) {
        val url = "https://api.spoonacular.com/recipes/findByIngredients?ingredients=$ingredients&number=5&apiKey=$apiKey"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API_ERROR", "Failed to fetch recipes", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { json ->
                    val type = object : TypeToken<List<Recipe>>() {}.type
                    val recipes: List<Recipe> = gson.fromJson(json, type)

                    val detailedRecipes = mutableListOf<Recipe>()
                    val total = recipes.size

                    recipes.forEach { basic ->
                        fetchRecipeDetails(basic.id) { detailed ->
                            // Merge basic and detailed data
                            val mergedRecipe = detailed.copy(
                                title = basic.title,
                                image = basic.image,
                                usedIngredientCount = basic.usedIngredientCount,
                                missedIngredientCount = basic.missedIngredientCount,
                                isFavorite = basic.isFavorite
                            )
                            detailedRecipes.add(mergedRecipe)
                            if (detailedRecipes.size == total) {
                                activity?.runOnUiThread {
                                    recipeRecyclerView.adapter = RecipeAdapter(detailedRecipes)
                                }
                            }
                        }
                    }
                }
            }
        })
    }
    private fun fetchRecipeDetails(id: Int, callback: (Recipe) -> Unit) {
        val url = "https://api.spoonacular.com/recipes/$id/information?apiKey=$apiKey"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("DETAIL_ERROR", "Failed to fetch recipe details", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { json ->
                    val detailedRecipe = gson.fromJson(json, Recipe::class.java)
                    callback(detailedRecipe)
                }
            }
        })
    }
}