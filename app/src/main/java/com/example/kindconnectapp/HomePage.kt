package com.example.kindconnectapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // top bar
        val toolbar = findViewById<Toolbar>(R.id.topToolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size)
        toolbar.setNavigationOnClickListener {
            Toast.makeText(this, "Menu clicked", Toast.LENGTH_SHORT).show()
        }
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_search -> { Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show(); true }
                R.id.action_profile -> { startActivity(Intent(this, ProfileActivity::class.java)); true }
                else -> false
            }
        }

        // bottom nav → route to pages in the same order as Figma
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottom.selectedItemId = R.id.nav_home
        bottom.setOnItemSelectedListener { it ->
            when (it.itemId) {
                R.id.nav_home -> true // already here
                R.id.nav_pantry -> { startActivity(Intent(this, PantryActivity::class.java)); true }
                R.id.nav_map -> { startActivity(Intent(this, MapActivity::class.java)); true }
                R.id.nav_resources -> { startActivity(Intent(this, ResourcesActivity::class.java)); true }
                else -> false
            }
        }
    }
}



