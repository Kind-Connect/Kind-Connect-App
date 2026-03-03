package com.example.kindconnectapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PantryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantry)

        // --- Bottom Navigation Setup ---
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottom.selectedItemId = R.id.nav_pantry  // highlight Pantry tab

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomePage::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_pantry -> true  // already here
                R.id.nav_resources -> {
                    startActivity(Intent(this, ResourcesActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    true
                }

                else -> false
            }
        }

        // --- Buttons ---
        findViewById<Button>(R.id.goPantryButton).setOnClickListener {
            val intent = Intent(this, MyPantryActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.goRgButton).setOnClickListener {
            val intent = Intent(this, RecipeGeneratorActivity::class.java)
            startActivity(intent)
        }
    }
}
