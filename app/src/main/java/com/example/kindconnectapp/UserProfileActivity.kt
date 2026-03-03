package com.example.kindconnectapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileActivity : AppCompatActivity() {

    private lateinit var nameText: TextView
    private lateinit var emailText: TextView
    private lateinit var recipesListText: TextView
    private lateinit var sheltersListText: TextView

    private val db = FirebaseFirestore.getInstance()
    private var userNameKey: String = "User"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        nameText = findViewById(R.id.userNameText)
        emailText = findViewById(R.id.userEmailText)
        recipesListText = findViewById(R.id.recipesListText)
        sheltersListText = findViewById(R.id.sheltersListText)

        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val name = prefs.getString("name", "User") ?: "User"
        val email = prefs.getString("email", "No email saved") ?: "No email saved"

        userNameKey = name
        nameText.text = name
        emailText.text = email

        // Bottom nav so you can leave Profile
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_profile
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, HomePage::class.java)); true }
                R.id.nav_pantry -> { startActivity(Intent(this, PantryActivity::class.java)); true }
                R.id.nav_resources -> { startActivity(Intent(this, ResourcesActivity::class.java)); true }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadRecipeFavorites()
        loadShelterFavorites()
    }

    private fun loadRecipeFavorites() {
        recipesListText.text = "Loading favorite recipes..."

        db.collection("users")
            .document(userNameKey)
            .collection("favorites")
            .get()
            .addOnSuccessListener { result ->

                if (result.isEmpty) {
                    recipesListText.text = "No favorite recipes yet."
                    recipesListText.setOnClickListener(null)
                    return@addOnSuccessListener
                }

                val titles = result.documents.mapNotNull { it.getString("title") }.sorted()

                recipesListText.text = titles.joinToString(separator = "\n") { "• $it" }

                // Tap the list to remove one
                recipesListText.setOnClickListener {
                    showRemoveRecipeDialog(titles)
                }
            }
            .addOnFailureListener {
                recipesListText.text = "Could not load favorite recipes."
                recipesListText.setOnClickListener(null)
            }
    }

    private fun showRemoveRecipeDialog(titles: List<String>) {
        AlertDialog.Builder(this)
            .setTitle("Remove Favorite Recipe")
            .setItems(titles.toTypedArray()) { _, which ->
                val selectedTitle = titles[which]

                // Find and delete any docs that match this title
                db.collection("users")
                    .document(userNameKey)
                    .collection("favorites")
                    .whereEqualTo("title", selectedTitle)
                    .get()
                    .addOnSuccessListener { result ->
                        for (doc in result) {
                            doc.reference.delete()
                        }
                        loadRecipeFavorites()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadShelterFavorites() {
        sheltersListText.text = "Loading favorite shelters..."

        db.collection("users")
            .document(userNameKey)
            .collection("shelter_favorites")
            .get()
            .addOnSuccessListener { result ->
                val names = result.documents.mapNotNull { it.getString("name") }.sorted()
                sheltersListText.text =
                    if (names.isEmpty()) "No favorite shelters yet."
                    else names.joinToString(separator = "\n") { "• $it" }
            }
            .addOnFailureListener {
                sheltersListText.text = "Could not load favorite shelters."
            }
    }
}