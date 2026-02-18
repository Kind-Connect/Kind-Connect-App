package com.example.kindconnectapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ResourcesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var typeSpinner: Spinner
    private lateinit var radiusSpinner: Spinner

    private data class ResourceMeta(
        val resource: Resource,
        val type: String,
        val distanceMiles: Double
    )

    private lateinit var allResources: List<ResourceMeta>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resources)

        val toolbar = findViewById<Toolbar>(R.id.topToolbar)
        setSupportActionBar(toolbar)

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

        recyclerView = findViewById(R.id.resourcesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        typeSpinner = findViewById(R.id.typeFilterSpinner)
        radiusSpinner = findViewById(R.id.radiusFilterSpinner)

        val typeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.resource_type_filter,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        typeSpinner.adapter = typeAdapter

        val radiusAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.radius_filter,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        radiusSpinner.adapter = radiusAdapter

        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        radiusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        loadResources()
        applyFilters()
    }

    private fun loadResources() {
        allResources = listOf(
            ResourceMeta(
                resource = Resource(
                    name = "Petersburg Mental Health Support Center",
                    description = "Provides crisis services, evaluations, and outpatient counseling.",
                    imageUrl = "https://via.placeholder.com/150"
                ),
                type = "Mental health",
                distanceMiles = 1.2
            ),
            ResourceMeta(
                resource = Resource(
                    name = "Petersburg Community Shelter",
                    description = "Emergency shelter offering temporary housing and support services.",
                    imageUrl = "https://via.placeholder.com/150"
                ),
                type = "Shelter",
                distanceMiles = 2.8
            ),
            ResourceMeta(
                resource = Resource(
                    name = "Hope Behavioral Wellness Clinic",
                    description = "Therapy, substance abuse programs, and long-term treatment plans.",
                    imageUrl = "https://via.placeholder.com/150"
                ),
                type = "Mental health",
                distanceMiles = 4.5
            ),
            ResourceMeta(
                resource = Resource(
                    name = "River City Youth Support Center",
                    description = "Youth counseling, after-school mentoring, and crisis intervention.",
                    imageUrl = "https://via.placeholder.com/150"
                ),
                type = "Mental health",
                distanceMiles = 6.0
            ),
            ResourceMeta(
                resource = Resource(
                    name = "Tri-Cities Women’s Resource Hub",
                    description = "Shelter assistance, advocacy services, and housing resources for women.",
                    imageUrl = "https://via.placeholder.com/150"
                ),
                type = "Shelter",
                distanceMiles = 3.4
            ),
            ResourceMeta(
                resource = Resource(
                    name = "Better Pathways Addiction Recovery",
                    description = "Offers outpatient and group substance abuse treatment programs.",
                    imageUrl = "https://via.placeholder.com/150"
                ),
                type = "Mental health",
                distanceMiles = 5.7
            ),
            ResourceMeta(
                resource = Resource(
                    name = "Southside Family Health Services",
                    description = "Family mental health services, behavioral therapy, and medical support.",
                    imageUrl = "https://via.placeholder.com/150"
                ),
                type = "Mental health",
                distanceMiles = 4.1
            ),
            ResourceMeta(
                resource = Resource(
                    name = "Petersburg Crisis Support Hotline Center",
                    description = "24/7 crisis intervention and referral services for individuals in need.",
                    imageUrl = "https://via.placeholder.com/150"
                ),
                type = "Mental health",
                distanceMiles = 7.3
            ),
            ResourceMeta(
                resource = Resource(
                    name = "Community Outreach Recovery Program",
                    description = "Free counseling services, community outreach, and wellness education.",
                    imageUrl = "https://via.placeholder.com/150"
                ),
                type = "Mental health",
                distanceMiles = 9.0
            ),
            ResourceMeta(
                resource = Resource(
                    name = "Healing Heart Support Foundation",
                    description = "Support groups, guided therapy, and grief counseling resources.",
                    imageUrl = "https://via.placeholder.com/150"
                ),
                type = "Mental health",
                distanceMiles = 11.5
            )
        )
    }

    private fun applyFilters() {
        val typeSelection = typeSpinner.selectedItem?.toString() ?: "All types"
        val radiusSelection = radiusSpinner.selectedItem?.toString() ?: "Any distance"

        val maxRadius = when (radiusSelection) {
            "Within 5 miles" -> 5.0
            "Within 10 miles" -> 10.0
            "Within 25 miles" -> 25.0
            else -> Double.MAX_VALUE
        }

        val filteredMeta = allResources.filter { meta ->
            val typeMatches = when (typeSelection) {
                "Food banks" -> meta.type.equals("Food bank", ignoreCase = true)
                "Shelters" -> meta.type.equals("Shelter", ignoreCase = true)
                "Mental health" -> meta.type.equals("Mental health", ignoreCase = true)
                else -> true
            }
            val radiusMatches = meta.distanceMiles <= maxRadius
            typeMatches && radiusMatches
        }

        val filteredResources = filteredMeta.map { it.resource }
        recyclerView.adapter = ResourceAdapter(this, filteredResources)
    }
}
