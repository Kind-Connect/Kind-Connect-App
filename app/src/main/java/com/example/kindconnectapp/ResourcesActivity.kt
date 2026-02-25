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
            Resource("Petersburg Mental Health Support Center", "Provides crisis services, evaluations, and outpatient counseling.", ""),
            Resource("Hope Behavioral Wellness Clinic", "Therapy, substance abuse programs, and long-term treatment plans.", ""),
            Resource("Southside Family Health Services", "Family mental health services, behavioral therapy, and medical support.", ""),
            Resource("Better Pathways Addiction Recovery", "Offers outpatient and group substance abuse treatment programs.", ""),
            Resource("Petersburg Behavioral Treatment Center", "Intensive therapy, evaluations, and inpatient care.", ""),
            Resource("Northside Psychological Services", "Specialized treatment for anxiety, depression, trauma, and family counseling.", ""),
            Resource("Central Virginia Behavioral Care Center", "Outpatient therapy, psychiatric evaluations, and community wellness programs.", ""),
            Resource("Elm Street Mental Wellness Institute", "Counseling, medication management, and emotional support services.", ""),
            Resource("Neighborhood Mental Health & Recovery Center", "Crisis stabilization, recovery support, and long-term therapy.", ""),
            Resource("Healing Heart Support Foundation", "Support groups, guided therapy, and grief counseling resources.", ""),
            Resource("Petersburg Community Shelter", "Emergency shelter offering temporary housing and support services.", ""),
            Resource("Tri-Cities Women’s Resource Hub", "Shelter assistance, advocacy services, and housing resources for women.", ""),
            Resource("Southside Emergency Homeless Shelter", "Overnight housing, meals, and crisis support.", ""),
            Resource("Tri-Cities Family Crisis Shelter", "Domestic support, temporary housing, and safety resources.", ""),
            Resource("River City Youth Support Center", "Youth counseling, after-school mentoring, and crisis intervention.", ""),
            Resource("Community Outreach Recovery Program", "Free counseling services, community outreach, and wellness education.", ""),
            Resource("Petersburg Crisis Support Hotline Center", "24/7 crisis intervention and referral services.", ""),
            Resource("Pathways Addiction and Recovery Home", "Long-term recovery housing, therapy, and community programs.", "")
        )

        val adapter = ResourceAdapter(this, resourceList)
        recyclerView.adapter = adapter

        val searchBar = findViewById<SearchView>(R.id.searchBar)
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filterList(newText ?: "")
                return true
            }
        })
    }
}