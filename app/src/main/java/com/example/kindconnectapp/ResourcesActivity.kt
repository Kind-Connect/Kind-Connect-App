package com.example.kindconnectapp

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent

class ResourcesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resources)

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
                R.id.nav_resources -> true
                else -> false
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.resourcesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val resourceList = listOf(
            Resource(
                "Petersburg Mental Health Support Center",
                "Provides crisis services, evaluations, and outpatient counseling.",
                "200 Washington St, Petersburg, VA",
                37.225500, -77.403400
            ),
            Resource(
                "Hope Behavioral Wellness Clinic",
                "Therapy, substance abuse programs, and long-term treatment plans.",
                "450 Hope Ave, Petersburg, VA",
                37.226700, -77.401900
            ),
            Resource(
                "Southside Family Health Services",
                "Family mental health services, behavioral therapy, and medical support.",
                "123 Southside Dr, Petersburg, VA",
                37.227900, -77.400400
            ),
            Resource(
                "Better Pathways Addiction Recovery",
                "Offers outpatient and group substance abuse treatment programs.",
                "80 Recovery Ln, Petersburg, VA",
                37.229100, -77.402500
            ),
            Resource(
                "Petersburg Behavioral Treatment Center",
                "Intensive therapy, evaluations, and inpatient care.",
                "12 Treatment Plaza, Petersburg, VA",
                37.230300, -77.401000
            ),
            Resource(
                "Northside Psychological Services",
                "Specialized treatment for anxiety, depression, trauma, and family counseling.",
                "5 Northside Ave, Petersburg, VA",
                37.231500, -77.399500
            ),
            Resource(
                "Central Virginia Behavioral Care Center",
                "Outpatient therapy, psychiatric evaluations, and community wellness programs.",
                "269 Medical Park Blvd, Petersburg, VA",
                37.226300, -77.401600
            ),
            Resource(
                "Elm Street Mental Wellness Institute",
                "Counseling, medication management, and emotional support services.",
                "Elm St & 4th, Petersburg, VA",
                37.227500, -77.400100
            ),
            Resource(
                "Neighborhood Mental Health & Recovery Center",
                "Crisis stabilization, recovery support, and long-term therapy.",
                "33 Neighborhood Ct, Petersburg, VA",
                37.228700, -77.398600
            ),
            Resource(
                "Healing Heart Support Foundation",
                "Support groups, guided therapy, and grief counseling resources.",
                "77 Healing Way, Petersburg, VA",
                37.229900, -77.400700
            ),
            Resource(
                "Petersburg Community Shelter",
                "Emergency shelter offering temporary housing and support services.",
                "123 Main St, Petersburg, VA",
                37.231100, -77.399200
            ),
            Resource(
                "Tri-Cities Women’s Resource Hub",
                "Shelter assistance, advocacy services, and housing resources for women.",
                "10 Women’s Plaza, Petersburg, VA",
                37.232300, -77.397700
            ),
            Resource(
                "Southside Emergency Homeless Shelter",
                "Overnight housing, meals, and crisis support.",
                "200 Southside Shelter Rd, Petersburg, VA",
                37.227100, -77.399800
            ),
            Resource(
                "Tri-Cities Family Crisis Shelter",
                "Domestic support, temporary housing, and safety resources.",
                "5 Family Ct, Petersburg, VA",
                37.228300, -77.398300
            ),
            Resource(
                "River City Youth Support Center",
                "Youth counseling, after-school mentoring, and crisis intervention.",
                "50 River St, Petersburg, VA",
                37.229500, -77.396800
            ),
            Resource(
                "Community Outreach Recovery Program",
                "Free counseling services, community outreach, and wellness education.",
                "8 Outreach Ln, Petersburg, VA",
                37.230700, -77.398900
            ),
            Resource(
                "Petersburg Crisis Support Hotline Center",
                "24/7 crisis intervention and referral services.",
                "Hotline Center, Petersburg, VA",
                37.231900, -77.397400
            ),
            Resource(
                "Pathways Addiction and Recovery Home",
                "Long-term recovery housing, therapy, and community programs.",
                "12 Recovery Home Rd, Petersburg, VA",
                37.233100, -77.395900
            )
        )

        val adapter = ResourceAdapter(this, resourceList) { resource ->
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
        recyclerView.adapter = adapter
}}