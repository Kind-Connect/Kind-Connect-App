package com.example.kindconnectapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

        // TEAMMATE TOOLBAR CODE (KEEP THIS)
        val toolbar = findViewById<Toolbar>(R.id.topToolbar)

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val greeting = findViewById<TextView>(R.id.greetingText)

        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val name = prefs.getString("name", "User")
        greeting.text = "$name"

        // Set toolbar behaviors
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size)
        toolbar.setNavigationOnClickListener {
            Toast.makeText(this, "Menu clicked", Toast.LENGTH_SHORT).show()
        }
//code was giving me errors so I commented it out.
//        toolbar.setOnMenuItemClickListener { item ->
//            when (item.itemId) {
//                R.id.action_search -> {
//                    Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                R.id.action_profile -> {
//                    startActivity(Intent(this, ProfileActivity::class.java))
//                    true
//                }
//                else -> false
//            }
//        }

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
                    startActivity(Intent(this, FavoriteRecipesActivity::class.java))
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }

        bottom.selectedItemId = R.id.nav_home
        bottom.setOnItemSelectedListener { it ->
            when (it.itemId) {
                R.id.nav_home -> true
                R.id.nav_pantry -> { startActivity(Intent(this, PantryActivity::class.java)); true }
                R.id.nav_resources -> { startActivity(Intent(this, ResourcesActivity::class.java)); true }
                else -> false
            }
        }

        // Handle safe area padding for toolbar + bottom nav
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