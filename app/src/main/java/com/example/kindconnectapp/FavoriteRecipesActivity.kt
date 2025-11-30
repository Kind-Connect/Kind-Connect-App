package com.example.kindconnectapp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class FavoriteRecipesActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyMessage: TextView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_recipes)

        recyclerView = findViewById(R.id.favoritesRecyclerView)
        emptyMessage = findViewById(R.id.emptyMessage)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.topToolbar)
        setSupportActionBar(toolbar)

        // Enable back arrow
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        // Make arrow white
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.navigationIcon?.setTint(Color.WHITE)

        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val name = prefs.getString("name", "User") ?: "User"

        db.collection("users").document(name).collection("favorites")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    emptyMessage.text = "Failed to load favorites."
                    emptyMessage.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    return@addSnapshotListener
                }
                // ✅ Add this log
                Log.d("Favorites", "Snapshot size=${snapshot?.size()}")
                snapshot?.documents?.forEach { doc ->
                    Log.d("Favorites", "Doc=${doc.data}")
                }


                val favorites = snapshot?.toObjects(Recipe::class.java) ?: emptyList()
                if (favorites.isEmpty()) {
                    emptyMessage.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyMessage.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    recyclerView.adapter = RecipeAdapter(favorites)
                }
            }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}