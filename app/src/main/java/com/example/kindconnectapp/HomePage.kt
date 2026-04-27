package com.example.kindconnectapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class HomePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 🔹 Toolbar + UI references
        val toolbar = findViewById<Toolbar>(R.id.topToolbar)
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val greeting = findViewById<TextView>(R.id.greetingText)

        // 🔹 Card references
        val cardPantry = findViewById<View>(R.id.cardPantry)
        val cardResources = findViewById<View>(R.id.cardResources)
        val cardProfile = findViewById<View>(R.id.cardProfile)
        val cardHome = findViewById<View>(R.id.cardHome)

        // 🔹 Load user name
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val name = prefs.getString("name", "User")
        greeting.text = "$name"

        // 🔹 Setup toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size)
        toolbar.setNavigationOnClickListener {
            Toast.makeText(this, "Menu clicked", Toast.LENGTH_SHORT).show()
        }

        // 🔹 Drawer setup
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_favorites -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }

        // 🔹 Bottom Navigation
        bottom.selectedItemId = R.id.nav_home
        bottom.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> true

                R.id.nav_pantry -> {
                    val intent = Intent(this, PantryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    true
                }

                R.id.nav_resources -> {
                    val intent = Intent(this, ResourcesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    true
                }

                R.id.nav_profile -> {
                    val intent = Intent(this, UserProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        // 🔥 🔥 CARD CLICK LOGIC (NEW)
        cardPantry.setOnClickListener {
            val intent = Intent(this, PantryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        cardResources.setOnClickListener {
            val intent = Intent(this, ResourcesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        cardProfile.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        cardHome.setOnClickListener {
            Toast.makeText(this, "You're already on Home", Toast.LENGTH_SHORT).show()
        }

        // 🔹 Handle safe area (status/nav bars)
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = v.paddingTop + sys.top)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(bottom) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = v.paddingBottom + sys.bottom)
            insets
        }
    }
}