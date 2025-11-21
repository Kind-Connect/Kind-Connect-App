package com.example.kindconnectapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        
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

        ViewCompat.setOnApplyWindowInsetsListener(bottom) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = v.paddingBottom + sys.bottom)
            insets
        }
    }
}


