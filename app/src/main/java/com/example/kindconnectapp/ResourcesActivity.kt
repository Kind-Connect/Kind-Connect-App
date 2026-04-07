package com.example.kindconnectapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore

class ResourcesActivity : AppCompatActivity() {

    private lateinit var adapter: ResourceAdapter
    private val db = FirebaseFirestore.getInstance()
    private var userNameKey: String = "User"
    private val favoriteNames = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resources)

        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userNameKey = prefs.getString("name", "User") ?: "User"

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val recyclerView = findViewById<RecyclerView>(R.id.resourcesRecyclerView)
        val tabLayout = findViewById<TabLayout>(R.id.resourceTabs)

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
                R.id.nav_resources -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        val resourceList = listOf(
            Resource(
                name = "Petersburg Community Shelter",
                description = "Emergency shelter offering temporary housing and support services.",
                urlOrAddress = "123 Main St, Petersburg, VA",
                category = "Shelters",
                lat = 37.231100,
                lng = -77.399200
            ),
            Resource(
                name = "Tri-Cities Women’s Resource Hub",
                description = "Shelter assistance, advocacy services, and housing resources for women.",
                urlOrAddress = "10 Women’s Plaza, Petersburg, VA",
                category = "Shelters",
                lat = 37.232300,
                lng = -77.397700
            ),
            Resource(
                name = "Southside Emergency Homeless Shelter",
                description = "Overnight housing, meals, and crisis support.",
                urlOrAddress = "200 Southside Shelter Rd, Petersburg, VA",
                category = "Shelters",
                lat = 37.227100,
                lng = -77.399800
            ),
            Resource(
                name = "Tri-Cities Family Crisis Shelter",
                description = "Domestic support, temporary housing, and safety resources.",
                urlOrAddress = "5 Family Ct, Petersburg, VA",
                category = "Shelters",
                lat = 37.228300,
                lng = -77.398300
            ),
            Resource(
                name = "Petersburg Food Pantry",
                description = "Provides groceries, canned goods, and basic food assistance for local families.",
                urlOrAddress = "300 Food Bank Dr, Petersburg, VA",
                category = "Food Banks",
                lat = 37.229200,
                lng = -77.401200
            ),
            Resource(
                name = "Tri-Cities Community Food Bank",
                description = "Community food bank offering emergency food support and pantry essentials.",
                urlOrAddress = "145 Community Ln, Petersburg, VA",
                category = "Food Banks",
                lat = 37.230100,
                lng = -77.398800
            ),
            Resource(
                name = "Southside Family Food Assistance Center",
                description = "Offers free groceries, meal support, and food distribution services.",
                urlOrAddress = "220 South St, Petersburg, VA",
                category = "Food Banks",
                lat = 37.227800,
                lng = -77.400900
            ),
            Resource(
                name = "Hope Pantry and Food Outreach",
                description = "Supports families with pantry items, food boxes, and local meal assistance.",
                urlOrAddress = "88 Hope Ave, Petersburg, VA",
                category = "Food Banks",
                lat = 37.226900,
                lng = -77.402100
            ),
            Resource(
                name = "Petersburg Mental Health Support Center",
                description = "Provides crisis services, evaluations, and outpatient counseling.",
                urlOrAddress = "200 Washington St, Petersburg, VA",
                category = "Mental Health",
                lat = 37.225500,
                lng = -77.403400
            ),
            Resource(
                name = "Hope Behavioral Wellness Clinic",
                description = "Therapy, substance abuse programs, and long-term treatment plans.",
                urlOrAddress = "450 Hope Ave, Petersburg, VA",
                category = "Mental Health",
                lat = 37.226700,
                lng = -77.401900
            ),
            Resource(
                name = "Southside Family Health Services",
                description = "Family mental health services, behavioral therapy, and medical support.",
                urlOrAddress = "123 Southside Dr, Petersburg, VA",
                category = "Mental Health",
                lat = 37.227900,
                lng = -77.400400
            ),
            Resource(
                name = "Neighborhood Mental Health & Recovery Center",
                description = "Crisis stabilization, recovery support, and long-term therapy.",
                urlOrAddress = "33 Neighborhood Ct, Petersburg, VA",
                category = "Mental Health",
                lat = 37.228700,
                lng = -77.398600
            )
        )

        adapter = ResourceAdapter(
            context = this,
            resources = resourceList,
            onItemClick = { resource ->

                val message = """
        Category: ${resource.category}
        
        ${resource.description}
        
        Address:
        ${resource.urlOrAddress}
    """.trimIndent()

                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle(resource.name)
                    .setMessage(message)
                    .setPositiveButton("Go to Map") { _, _ ->

                        val intent = Intent(this, MapActivity::class.java).apply {
                            putExtra("name", resource.name)
                            resource.lat?.let { putExtra("lat", it) }
                            resource.lng?.let { putExtra("lng", it) }

                            if (resource.lat == null || resource.lng == null) {
                                putExtra("address", resource.urlOrAddress)
                            }
                        }

                        startActivity(intent)
                    }
                    .setNegativeButton("Close", null)
                    .show()
            },
            onFavoriteClick = { resource ->
                toggleFavorite(resource)
            },
            favoriteNames = favoriteNames
        )

        recyclerView.adapter = adapter

        tabLayout.removeAllTabs()
        tabLayout.addTab(tabLayout.newTab().setText("Shelters"))
        tabLayout.addTab(tabLayout.newTab().setText("Food Banks"))
        tabLayout.addTab(tabLayout.newTab().setText("Mental Health"))

        adapter.filterByCategory("Shelters")

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> adapter.filterByCategory("Shelters")
                    1 -> adapter.filterByCategory("Food Banks")
                    2 -> adapter.filterByCategory("Mental Health")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        loadFavoriteShelterNames()
    }

    private fun loadFavoriteShelterNames() {
        db.collection("users")
            .document(userNameKey)
            .collection("shelter_favorites")
            .get()
            .addOnSuccessListener { result ->
                favoriteNames.clear()
                favoriteNames.addAll(result.documents.mapNotNull { it.getString("name") })
                adapter.refreshFavorites()
            }
    }

    private fun toggleFavorite(resource: Resource) {
        val docRef = db.collection("users")
            .document(userNameKey)
            .collection("shelter_favorites")
            .document(resource.name)

        if (favoriteNames.contains(resource.name)) {
            docRef.delete().addOnSuccessListener {
                favoriteNames.remove(resource.name)
                adapter.refreshFavorites()
            }
        } else {
            val favoriteData = hashMapOf(
                "name" to resource.name,
                "category" to resource.category,
                "description" to resource.description,
                "address" to resource.urlOrAddress,
                "lat" to resource.lat,
                "lng" to resource.lng
            )

            docRef.set(favoriteData).addOnSuccessListener {
                favoriteNames.add(resource.name)
                adapter.refreshFavorites()
            }
        }
    }
}