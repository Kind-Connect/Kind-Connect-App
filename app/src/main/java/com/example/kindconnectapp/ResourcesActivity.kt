package com.example.kindconnectapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent

class ResourcesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resources)

        // --- Bottom Navigation Setup ---
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottom.selectedItemId = R.id.nav_resources

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomePage::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_pantry -> {
                    startActivity(Intent(this, PantryActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_map -> {
                    startActivity(Intent(this, MapActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_resources -> true
                else -> false
            }
        }

        // --- RecyclerView Setup ---
        val recyclerView = findViewById<RecyclerView>(R.id.resourcesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val resourceList = listOf(
            Resource(
                name = "Petersburg Mental Health Support Center",
                description = "Provides crisis services, evaluations, and outpatient counseling.",
                imageUrl = "https://via.placeholder.com/150"
            ),
            Resource(
                name = "Petersburg Community Shelter",
                description = "Emergency shelter offering temporary housing and support services.",
                imageUrl = "https://via.placeholder.com/150"
            ),
            Resource(
                name = "Hope Behavioral Wellness Clinic",
                description = "Therapy, substance abuse programs, and long-term treatment plans.",
                imageUrl = "https://via.placeholder.com/150"
            ),
            Resource(
                name = "River City Youth Support Center",
                description = "Youth counseling, after-school mentoring, and crisis intervention.",
                imageUrl = "https://via.placeholder.com/150"
            ),
            Resource(
                name = "Tri-Cities Women’s Resource Hub",
                description = "Shelter assistance, advocacy services, and housing resources for women.",
                imageUrl = "https://via.placeholder.com/150"
            ),
            Resource(
                name = "Better Pathways Addiction Recovery",
                description = "Offers outpatient and group substance abuse treatment programs.",
                imageUrl = "https://via.placeholder.com/150"
            ),
            Resource(
                name = "Southside Family Health Services",
                description = "Family mental health services, behavioral therapy, and medical support.",
                imageUrl = "https://via.placeholder.com/150"
            ),
            Resource(
                name = "Petersburg Crisis Support Hotline Center",
                description = "24/7 crisis intervention and referral services for individuals in need.",
                imageUrl = "https://via.placeholder.com/150"
            ),
            Resource(
                name = "Community Outreach Recovery Program",
                description = "Free counseling services, community outreach, and wellness education.",
                imageUrl = "https://via.placeholder.com/150"
            ),
            Resource(
                name = "Healing Heart Support Foundation",
                description = "Support groups, guided therapy, and grief counseling resources.",
                imageUrl = "https://via.placeholder.com/150"
            )
        )

        recyclerView.adapter = ResourceAdapter(this, resourceList)
    }
}