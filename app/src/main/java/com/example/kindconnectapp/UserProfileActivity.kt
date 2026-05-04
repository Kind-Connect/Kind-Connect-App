package com.example.kindconnectapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileActivity : AppCompatActivity() {

    private lateinit var nameText: TextView
    private lateinit var emailText: TextView
    private lateinit var recipesContainer: LinearLayout
    private lateinit var sheltersContainer: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private var userNameKey: String = "User"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        nameText = findViewById(R.id.userNameText)
        emailText = findViewById(R.id.userEmailText)
        recipesContainer = findViewById(R.id.recipesContainer)
        sheltersContainer = findViewById(R.id.sheltersContainer)

        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val name = prefs.getString("name", "User") ?: "User"
        val email = prefs.getString("email", "No email saved") ?: "No email saved"

        userNameKey = name

        nameText.text = name
        emailText.text = email

        val logoutButton = findViewById<Button>(R.id.btnLogout)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            prefs.edit()
                .remove("name")
                .remove("email")
                .apply()

            val intent = Intent(this, ProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        loadRecipeFavorites()
        loadShelterFavorites()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_profile

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomePage::class.java))
                    true
                }
                R.id.nav_pantry -> {
                    startActivity(Intent(this, PantryActivity::class.java))
                    true
                }
                R.id.nav_resources -> {
                    startActivity(Intent(this, ResourcesActivity::class.java))
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    private fun loadRecipeFavorites() {
        recipesContainer.removeAllViews()
        addLoadingCard(recipesContainer, "Loading favorite recipes...")

        db.collection("users")
            .document(userNameKey)
            .collection("favorites")
            .get()
            .addOnSuccessListener { result ->
                recipesContainer.removeAllViews()

                val titles = result.documents.mapNotNull { it.getString("title") }.sorted()

                if (titles.isEmpty()) {
                    addEmptyCard(
                        recipesContainer,
                        "No favorite recipes yet",
                        "Save recipes to see them here."
                    )
                    return@addOnSuccessListener
                }

                titles.forEach { title ->
                    addFavoriteCard(
                        container = recipesContainer,
                        title = title,
                        subtitle = "Saved recipe",
                        emoji = "🍲",
                        imageResId = null,
                        onClick = { showRemoveRecipeDialog(title) }
                    )
                }
            }
            .addOnFailureListener {
                recipesContainer.removeAllViews()
                addEmptyCard(recipesContainer, "Could not load recipes", "Please try again later.")
            }
    }

    private fun loadShelterFavorites() {
        sheltersContainer.removeAllViews()
        addLoadingCard(sheltersContainer, "Loading favorite resources...")

        db.collection("users")
            .document(userNameKey)
            .collection("shelter_favorites")
            .get()
            .addOnSuccessListener { result ->
                sheltersContainer.removeAllViews()

                if (result.isEmpty) {
                    addEmptyCard(
                        sheltersContainer,
                        "No favorite resources yet",
                        "Favorite resources will appear here."
                    )
                    return@addOnSuccessListener
                }

                result.documents
                    .sortedBy { it.getString("name") ?: "" }
                    .forEach { doc ->
                        val name = doc.getString("name") ?: return@forEach
                        val category = doc.getString("category") ?: "Saved resource"
                        val imageResId = doc.getLong("imageResId")?.toInt()

                        addFavoriteCard(
                            container = sheltersContainer,
                            title = name,
                            subtitle = category,
                            emoji = "🏠",
                            imageResId = imageResId,
                            onClick = null
                        )
                    }
            }
            .addOnFailureListener {
                sheltersContainer.removeAllViews()
                addEmptyCard(sheltersContainer, "Could not load resources", "Please try again later.")
            }
    }

    private fun addFavoriteCard(
        container: LinearLayout,
        title: String,
        subtitle: String,
        emoji: String,
        imageResId: Int? = null,
        onClick: (() -> Unit)?
    ) {
        val card = LayoutInflater.from(this)
            .inflate(R.layout.item_favorite_card, container, false) as MaterialCardView

        val emojiText = card.findViewById<TextView>(R.id.favoriteEmoji)
        val titleText = card.findViewById<TextView>(R.id.favoriteTitle)
        val subtitleText = card.findViewById<TextView>(R.id.favoriteSubtitle)
        val actionText = card.findViewById<TextView>(R.id.favoriteAction)
        val imageView = card.findViewById<ImageView?>(R.id.favoriteImage)

        titleText.text = title
        subtitleText.text = subtitle

        if (imageView != null && imageResId != null) {
            imageView.setImageResource(imageResId)
            imageView.visibility = ImageView.VISIBLE
            emojiText.visibility = TextView.GONE
        } else {
            emojiText.text = emoji
            emojiText.visibility = TextView.VISIBLE
            imageView?.visibility = ImageView.GONE
        }

        if (onClick != null) {
            actionText.text = "Tap to remove"
            card.setOnClickListener { onClick() }
        } else {
            actionText.text = "Saved"
        }

        card.alpha = 0f
        container.addView(card)

        card.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(250)
            .start()
    }

    private fun addLoadingCard(container: LinearLayout, message: String) {
        addStatusCard(container, "⏳", message, "Please wait...")
    }

    private fun addEmptyCard(container: LinearLayout, title: String, subtitle: String) {
        addStatusCard(container, "✨", title, subtitle)
    }

    private fun addStatusCard(
        container: LinearLayout,
        emoji: String,
        title: String,
        subtitle: String
    ) {
        val card = LayoutInflater.from(this)
            .inflate(R.layout.item_favorite_card, container, false) as MaterialCardView

        card.findViewById<TextView>(R.id.favoriteEmoji).text = emoji
        card.findViewById<TextView>(R.id.favoriteTitle).text = title
        card.findViewById<TextView>(R.id.favoriteSubtitle).text = subtitle
        card.findViewById<TextView>(R.id.favoriteAction).text = ""

        val imageView = card.findViewById<ImageView?>(R.id.favoriteImage)
        imageView?.visibility = ImageView.GONE

        container.addView(card)
    }

    private fun showRemoveRecipeDialog(selectedTitle: String) {
        AlertDialog.Builder(this)
            .setTitle("Remove Favorite Recipe")
            .setMessage("Remove \"$selectedTitle\" from your favorites?")
            .setPositiveButton("Remove") { _, _ ->
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
}