package com.example.kindconnectapp

import android.widget.TextView
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecommendedRecipesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noPantryMessage: TextView
    private val apiKey = "5df89a2562b54f9fbfde47f5e19ac90a"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recommended_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recommendedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        noPantryMessage = view.findViewById(R.id.noPantryMessage)

        loadRecommendedRecipes()
    }

    private fun loadRecommendedRecipes() {
        fetchPantryItems { pantryItems ->
            if (pantryItems.isEmpty()) {
                noPantryMessage.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                return@fetchPantryItems
            }

            noPantryMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            val ingredientQuery = pantryItems.joinToString(",") { it.lowercase() }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Get basic recommended recipes
                    val recipeResponses = SpoonacularApi.retrofitService.getRecipesByIngredients(
                        ingredients = ingredientQuery,
                        number = 10,
                        apiKey = apiKey
                    )

                    // Fetch full details AND merge used/missed counts
                    val detailedRecipes = recipeResponses.map { r ->
                        val full = SpoonacularApi.retrofitService.getRecipeInformation(
                            r.id,
                            apiKey
                        )

                        // Merge used/missed counts into the full recipe
                        full.copy(
                            usedIngredientCount = r.usedIngredientCount,
                            missedIngredientCount = r.missedIngredientCount
                        )
                    }

                    //Load adapter with full recipes (includes instructions!)
                    withContext(Dispatchers.Main) {
                        recyclerView.adapter = RecipeAdapter(detailedRecipes)
                    }

                } catch (e: Exception) {
                    Log.e("RecommendedRecipes", "Error fetching recommended recipes", e)
                }
            }
        }
    }

    private fun fetchPantryItems(onResult: (List<String>) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("pantryItems")
            .get()
            .addOnSuccessListener { result ->
                val items = result.documents.mapNotNull { it.getString("name") }
                onResult(items)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}