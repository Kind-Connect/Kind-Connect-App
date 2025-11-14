package com.example.kindconnectapp


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ResourcesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resources)

        val recyclerView = findViewById<RecyclerView>(R.id.resourcesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val resourceList = listOf(
            Resource(
                name = "Inova Behavioral Health Services",
                description = "Provides inpatient & outpatient behavioral healthcare across Northern Virginia.",
                imageUrl = "https://via.placeholder.com/150"
            ),
            Resource(
                name = "North Spring Behavioral Healthcare – Leesburg",
                description = "Youth psychiatric treatment center offering acute inpatient care.",
                imageUrl = "https://via.placeholder.com/150"
            ),
            Resource(
                name = "Virginia Beach Psychiatric Center",
                description = "Adult mental health & chemical dependency treatment.",
                imageUrl = "https://via.placeholder.com/150"
            )
            // Add more resources here
        )

        recyclerView.adapter = ResourceAdapter(this, resourceList)
    }
}
